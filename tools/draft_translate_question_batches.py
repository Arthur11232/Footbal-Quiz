#!/usr/bin/env python3
"""
Create machine-translation draft targets in question translation batch files.

This is intentionally a draft helper, not a production importer. The output must
be reviewed before being applied to the workbench or uploaded to Firestore.
"""

from __future__ import annotations

import argparse
import json
import time
from pathlib import Path
from typing import Any


QUESTION_FIELDS = ("question", "answer_A", "answer_B", "answer_C", "answer_D")
ANSWER_FIELDS = ("answer_A", "answer_B", "answer_C", "answer_D")
DEFAULT_LANGUAGES = ("es", "de", "it")


def load_json(path: Path) -> dict[str, Any]:
    with path.open("r", encoding="utf-8") as fh:
        payload = json.load(fh)
    if not isinstance(payload, dict) or not isinstance(payload.get("items"), list):
        raise ValueError(f"{path} must be an object with an items array")
    return payload


def save_json(path: Path, payload: dict[str, Any]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8") as fh:
        json.dump(payload, fh, ensure_ascii=False, indent=2)
        fh.write("\n")


def load_cache(path: Path) -> dict[str, str]:
    if not path.exists():
        return {}
    with path.open("r", encoding="utf-8") as fh:
        payload = json.load(fh)
    if not isinstance(payload, dict):
        raise ValueError(f"{path} cache must be an object")
    return {str(key): str(value) for key, value in payload.items()}


def cache_key(source_lang: str, target_lang: str, text: str) -> str:
    return f"{source_lang}>{target_lang}:{text}"


def translate_text(
    text: str,
    *,
    source_lang: str,
    target_lang: str,
    cache: dict[str, str],
    sleep_seconds: float,
    retries: int,
) -> str:
    normalized = text.strip()
    if not normalized:
        return ""

    key = cache_key(source_lang, target_lang, normalized)
    if key in cache:
        return cache[key]

    from deep_translator import GoogleTranslator

    last_error: Exception | None = None
    for attempt in range(retries + 1):
        try:
            translated = GoogleTranslator(source=source_lang, target=target_lang).translate(normalized)
            translated = str(translated or "").strip()
            if not translated:
                raise ValueError("empty translation result")
            cache[key] = translated
            if sleep_seconds:
                time.sleep(sleep_seconds)
            return translated
        except Exception as error:  # noqa: BLE001 - CLI tool should report provider failures.
            last_error = error
            time.sleep(max(1.0, sleep_seconds) * (attempt + 1))

    raise RuntimeError(f"Failed to translate to {target_lang}: {normalized!r}") from last_error


def find_right_answer_field(source: dict[str, Any]) -> str | None:
    right_answer = str(source.get("right_answer", "")).strip()
    for field in ANSWER_FIELDS:
        if str(source.get(field, "")).strip() == right_answer:
            return field
    return None


def draft_translate_batch(
    payload: dict[str, Any],
    *,
    source_lang: str,
    target_languages: list[str],
    cache: dict[str, str],
    sleep_seconds: float,
    retries: int,
    overwrite: bool,
) -> tuple[dict[str, Any], int]:
    translated_fields = 0

    for item in payload["items"]:
        source_by_lang = item.get("source") or {}
        source = source_by_lang.get(source_lang)
        if not isinstance(source, dict):
            raise ValueError(f"{item.get('questionId')} missing source language: {source_lang}")

        targets = item.setdefault("targets", {})
        right_answer_field = find_right_answer_field(source)

        for target_lang in target_languages:
            target = targets.setdefault(
                target_lang,
                {
                    "answer_A": "",
                    "answer_B": "",
                    "answer_C": "",
                    "answer_D": "",
                    "categoryType": item.get("categoryType", ""),
                    "question": "",
                    "right_answer": "",
                    "type": item.get("type", ""),
                    "answered": False,
                    "questionId": item.get("questionId", ""),
                },
            )

            for field in QUESTION_FIELDS:
                if not overwrite and str(target.get(field, "")).strip():
                    continue
                target[field] = translate_text(
                    str(source.get(field, "")),
                    source_lang=source_lang,
                    target_lang=target_lang,
                    cache=cache,
                    sleep_seconds=sleep_seconds,
                    retries=retries,
                )
                translated_fields += 1

            if right_answer_field:
                target["right_answer"] = target[right_answer_field]
            elif overwrite or not str(target.get("right_answer", "")).strip():
                target["right_answer"] = translate_text(
                    str(source.get("right_answer", "")),
                    source_lang=source_lang,
                    target_lang=target_lang,
                    cache=cache,
                    sleep_seconds=sleep_seconds,
                    retries=retries,
                )
                translated_fields += 1

            target["categoryType"] = item.get("categoryType", "")
            target["type"] = item.get("type", "")
            target["answered"] = False
            target["questionId"] = item.get("questionId", "")

    payload["status"] = "machine_draft_needs_review"
    payload["draft_translation"] = {
        "provider": "GoogleTranslator via deep-translator",
        "source_language": source_lang,
        "target_languages": target_languages,
        "review_required": True,
        "note": "Machine draft only. Review football facts, names, answer wording, and right_answer before Firestore update.",
    }
    return payload, translated_fields


def iter_batch_paths(batch_dir: Path, only: list[str]) -> list[Path]:
    if only:
        return [batch_dir / item for item in only]
    return sorted(batch_dir.glob("*.json"))


def main() -> None:
    parser = argparse.ArgumentParser(description="Draft-translate question batch files")
    parser.add_argument("--batch-dir", type=Path, required=True)
    parser.add_argument("--output-dir", type=Path, required=True)
    parser.add_argument("--cache", type=Path, required=True)
    parser.add_argument("--source-language", default="en")
    parser.add_argument("--target-languages", default=",".join(DEFAULT_LANGUAGES))
    parser.add_argument("--only", nargs="*", default=[])
    parser.add_argument("--sleep-seconds", type=float, default=0.05)
    parser.add_argument("--retries", type=int, default=2)
    parser.add_argument("--overwrite", action="store_true")
    args = parser.parse_args()

    target_languages = [lang.strip() for lang in args.target_languages.split(",") if lang.strip()]
    if not target_languages:
        raise ValueError("target languages cannot be empty")

    cache = load_cache(args.cache)
    total_fields = 0
    paths = iter_batch_paths(args.batch_dir, args.only)
    if not paths:
        raise ValueError("No batch files found")

    for path in paths:
        if not path.exists():
            raise FileNotFoundError(path)
        payload = load_json(path)
        payload, translated_fields = draft_translate_batch(
            payload,
            source_lang=args.source_language,
            target_languages=target_languages,
            cache=cache,
            sleep_seconds=args.sleep_seconds,
            retries=args.retries,
            overwrite=args.overwrite,
        )
        output_path = args.output_dir / path.name
        save_json(output_path, payload)
        save_json(args.cache, cache)
        total_fields += translated_fields
        print(f"{path.name}: translated fields={translated_fields}, cache size={len(cache)}")

    print(f"Done. Files: {len(paths)}, translated fields: {total_fields}")
    print(f"Output: {args.output_dir}")
    print(f"Cache: {args.cache}")


if __name__ == "__main__":
    main()
