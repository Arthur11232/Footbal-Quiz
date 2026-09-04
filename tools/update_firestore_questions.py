#!/usr/bin/env python3
"""
Backup or update Firestore `questions` using audit-format JSON files.

Recommended flow:
  1. Download a Firebase service account JSON from Firebase Console.
  2. Keep it outside git, for example: tools/service-account.local.json
  3. Install dependency:
       python3 -m pip install -r tools/requirements-firestore-importer.txt
  4. Inspect current numeric Firestore docs:
       python3 tools/update_firestore_questions.py inspect \
         --service-account tools/service-account.local.json
  5. Back up current Firestore docs before any update:
       python3 tools/update_firestore_questions.py backup \
         --service-account tools/service-account.local.json \
         --output app/questions_audit_backup_YYYY_MM_DD.json \
         --language-order en,ru,hy \
         --require-question-id
  6. Dry-run the update:
       python3 tools/update_firestore_questions.py update \
         --service-account tools/service-account.local.json \
         --audit app/questions_audit_fixed_2026_08_001.json \
         --language-order en,ru,hy \
         --require-question-id \
         --dry-run
  7. Apply:
       python3 tools/update_firestore_questions.py update \
         --service-account tools/service-account.local.json \
         --audit app/questions_audit_fixed_2026_08_001.json \
         --language-order en,ru,hy \
         --require-question-id \
         --commit
"""

from __future__ import annotations

import argparse
import json
import sys
from collections import Counter, defaultdict
from datetime import datetime
from pathlib import Path
from typing import Any
import xml.etree.ElementTree as ET


DEFAULT_AUDIT_PATH = Path("app/questions_audit.json")
DEFAULT_COLLECTION = "questions"
DEFAULT_LANGUAGE_ORDER = "en,ru,hy"
BATCH_LIMIT = 450
REQUIRED_FIELDS = ("question", "type", "answer_A", "answer_B", "answer_C", "answer_D", "right_answer")
QUESTION_ID_FIELD = "questionId"
RESOURCE_FILES = {
    "en": Path("app/src/main/res/values/strings.xml"),
    "ru": Path("app/src/main/res/values-ru/strings.xml"),
    "hy": Path("app/src/main/res/values-hy/strings.xml"),
}
QUESTION_ARRAY_BY_GROUP = {
    ("TOP5", "France"): "france_questions",
    ("TOP5", "Germany"): "germany_questions",
    ("TOP5", "Italy"): "italy_questions",
    ("TOP5", "English"): "english_questions",
    ("TOP5", "Spain"): "spain_questions",
    ("UEFA", "Super Cup"): "super_cup_questions",
    ("UEFA", "Europa League"): "europa_league_questions",
    ("UEFA", "Champions League"): "champions_league_questions",
    ("UEFA", "European Championship"): "european_cup_questions",
    ("WORLD_CUP", "World Championship"): "world_cup_questions",
    ("RON_MES", "vsRM"): "ron_mes_questions",
    ("REAL_BARCE", "vsRB"): "real_barce_questions",
}
CATEGORY_TYPE_BY_PLACE = {
    "France": "TOP5",
    "Germany": "TOP5",
    "Italy": "TOP5",
    "English": "TOP5",
    "Spain": "TOP5",
    "Super Cup": "UEFA",
    "Europa League": "UEFA",
    "Champions League": "UEFA",
    "European Championship": "UEFA",
    "World Championship": "WORLD_CUP",
    "vsRM": "RON_MES",
    "vsRB": "REAL_BARCE",
}


def load_audit(path: Path) -> dict[str, list[dict[str, Any]]]:
    with path.open("r", encoding="utf-8") as fh:
        data = json.load(fh)
    if not isinstance(data, dict):
        raise ValueError("questions_audit.json must be an object with language keys")
    return data


def parse_language_order(value: str) -> list[str]:
    languages = [item.strip() for item in value.split(",") if item.strip()]
    if not languages:
        raise ValueError("language order cannot be empty")
    return languages


def normalize_question_text(value: str) -> str:
    return " ".join(
        value.replace('\\"', '"').replace("ё", "е").replace("Ё", "Е").split()
    ).strip()


def build_question_id(category_type: str, place_type: str, index: int) -> str:
    slug_map = {
        ("TOP5", "France"): "france",
        ("TOP5", "Germany"): "germany",
        ("TOP5", "Italy"): "italy",
        ("TOP5", "English"): "english",
        ("TOP5", "Spain"): "spain",
        ("UEFA", "Super Cup"): "super_cup",
        ("UEFA", "Europa League"): "europa_league",
        ("UEFA", "Champions League"): "champions_league",
        ("UEFA", "European Championship"): "european_championship",
        ("WORLD_CUP", "World Championship"): "world_cup",
        ("RON_MES", "vsRM"): "vsrm",
        ("REAL_BARCE", "vsRB"): "vsrb",
    }
    prefix = slug_map[(category_type, place_type)]
    return f"{prefix}_{index:03d}"


def load_resource_question_arrays(lang: str) -> dict[str, list[str]]:
    resource_path = RESOURCE_FILES.get(lang)
    if resource_path is None:
        raise ValueError(f"No resource question source configured for language: {lang}")
    root = ET.parse(resource_path).getroot()
    arrays: dict[str, list[str]] = {}
    for element in root.findall("string-array"):
        name = element.attrib.get("name", "")
        arrays[name] = [normalize_question_text(item.text or "") for item in element.findall("item")]
    return arrays


def validate_audit(
    data: dict[str, list[dict[str, Any]]],
    languages: list[str],
    *,
    require_question_id: bool = False,
) -> None:
    missing_languages = [lang for lang in languages if lang not in data]
    if missing_languages:
        raise ValueError(f"Missing languages in audit file: {', '.join(missing_languages)}")

    counts = {lang: len(data[lang]) for lang in languages}
    if len(set(counts.values())) != 1:
        raise ValueError(f"Language question counts differ: {counts}")

    errors: list[str] = []
    for lang in languages:
        for index, item in enumerate(data[lang]):
            for field in REQUIRED_FIELDS:
                if not str(item.get(field, "")).strip():
                    errors.append(f"{lang}[{index}] missing {field}")

            if require_question_id and not str(item.get(QUESTION_ID_FIELD, "")).strip():
                errors.append(f"{lang}[{index}] missing {QUESTION_ID_FIELD}")

            answers = [item.get("answer_A"), item.get("answer_B"), item.get("answer_C"), item.get("answer_D")]
            if item.get("right_answer") not in answers:
                errors.append(f"{lang}[{index}] right_answer is not one of answer_A..D: {item.get('question')}")

    if errors:
        preview = "\n".join(errors[:25])
        suffix = f"\n... and {len(errors) - 25} more" if len(errors) > 25 else ""
        raise ValueError(f"Audit validation failed:\n{preview}{suffix}")


def to_firestore_document(lang: str, item: dict[str, Any]) -> dict[str, Any]:
    payload = {
        "question": item["question"],
        "translation": lang,
        "answered": bool(item.get("answered", False)),
        "place": item["type"],
        "answers": {
            "a": item["answer_A"],
            "b": item["answer_B"],
            "c": item["answer_C"],
            "d": item["answer_D"],
            "right": item["right_answer"],
        },
    }
    question_id = str(item.get(QUESTION_ID_FIELD, "")).strip()
    if question_id:
        payload[QUESTION_ID_FIELD] = question_id
    return payload


def from_firestore_document(payload: dict[str, Any]) -> dict[str, Any]:
    answers = payload.get("answers") or {}
    place = str(payload.get("place", "")).strip()
    category_type = str(payload.get("categoryType", "")).strip() or CATEGORY_TYPE_BY_PLACE.get(place, "")
    item = {
        "answer_A": answers.get("a", ""),
        "answer_B": answers.get("b", ""),
        "answer_C": answers.get("c", ""),
        "answer_D": answers.get("d", ""),
        "categoryType": category_type,
        "question": payload.get("question", ""),
        "right_answer": answers.get("right", ""),
        "type": place,
        "answered": bool(payload.get("answered", False)),
    }
    question_id = str(payload.get(QUESTION_ID_FIELD, "")).strip()
    if question_id:
        item[QUESTION_ID_FIELD] = question_id
    return item


def default_backup_output_path() -> Path:
    timestamp = datetime.now().strftime("%Y_%m_%d_%H%M%S")
    return Path(f"app/questions_audit_backup_{timestamp}.json")


def summarize_question_id_readiness(
    data: dict[str, list[dict[str, Any]]],
    languages: list[str],
) -> tuple[dict[str, int], dict[str, list[str]], dict[str, list[str]], dict[str, bool]]:
    missing_counts: dict[str, int] = {}
    duplicate_ids: dict[str, list[str]] = {}
    inconsistent_ids: dict[str, list[str]] = defaultdict(list)
    block_order_matches: dict[str, bool] = {}

    per_group_ids: dict[str, dict[tuple[str, str], list[str]]] = {}
    block_orders: dict[str, list[tuple[str, str]]] = {}
    for lang in languages:
        missing_counts[lang] = 0
        ids: list[str] = []
        group_map: dict[tuple[str, str], list[str]] = defaultdict(list)
        block_order: list[tuple[str, str]] = []
        prev_group: tuple[str, str] | None = None
        for item in data[lang]:
            question_id = str(item.get(QUESTION_ID_FIELD, "")).strip()
            if not question_id:
                missing_counts[lang] += 1
            group_key = (str(item.get("categoryType", "")).strip(), str(item.get("type", "")).strip())
            if group_key != prev_group:
                block_order.append(group_key)
                prev_group = group_key
            if question_id:
                ids.append(question_id)
                group_map[group_key].append(question_id)
        duplicates = [question_id for question_id, count in Counter(ids).items() if count > 1]
        duplicate_ids[lang] = sorted(duplicates)
        per_group_ids[lang] = group_map
        block_orders[lang] = block_order

    if languages:
        base_lang = languages[0]
        for group_key, base_ids in per_group_ids[base_lang].items():
            for lang in languages[1:]:
                lang_ids = per_group_ids[lang].get(group_key, [])
                if lang_ids != base_ids:
                    inconsistent_ids[lang].append(f"{group_key[0]}/{group_key[1]}")
        for lang in languages:
            block_order_matches[lang] = block_orders[lang] == block_orders[base_lang]

    return missing_counts, duplicate_ids, dict(inconsistent_ids), block_order_matches


def build_numbered_documents(
    data: dict[str, list[dict[str, Any]]],
    languages: list[str],
) -> list[tuple[str, dict[str, Any]]]:
    docs: list[tuple[str, dict[str, Any]]] = []
    next_id = 0
    for lang in languages:
        for item in data[lang]:
            docs.append((str(next_id), to_firestore_document(lang, item)))
            next_id += 1
    return docs


def init_firestore(service_account: Path):
    try:
        import firebase_admin
        from firebase_admin import credentials, firestore
    except ImportError:
        print(
            "Missing dependency: firebase-admin\n"
            "Install it with: python3 -m pip install -r tools/requirements-firestore-importer.txt",
            file=sys.stderr,
        )
        sys.exit(1)

    cred = credentials.Certificate(str(service_account))
    firebase_admin.initialize_app(cred)
    return firestore.client()


def inspect_numbered_docs(db, collection: str, sample_ids: list[str]) -> None:
    print(f"Inspecting collection `{collection}`")
    for doc_id in sample_ids:
        snap = db.collection(collection).document(doc_id).get()
        if not snap.exists:
            print(f"{doc_id}: <missing>")
            continue
        data = snap.to_dict() or {}
        print(
            f"{doc_id}: translation={data.get('translation')!r}, "
            f"place={data.get('place')!r}, question={data.get('question')!r}, "
            f"questionId={data.get('questionId')!r}, answers={data.get('answers')!r}"
        )


def fetch_numbered_docs(db, collection: str) -> list[tuple[int, dict[str, Any]]]:
    docs: list[tuple[int, dict[str, Any]]] = []
    for snap in db.collection(collection).stream():
        try:
            doc_number = int(snap.id)
        except ValueError:
            continue
        docs.append((doc_number, snap.to_dict() or {}))
    docs.sort(key=lambda item: item[0])
    return docs


def commit_batches(db, collection: str, docs: list[tuple[str, dict[str, Any]]]) -> None:
    for offset in range(0, len(docs), BATCH_LIMIT):
        batch = db.batch()
        chunk = docs[offset : offset + BATCH_LIMIT]
        for doc_id, payload in chunk:
            ref = db.collection(collection).document(doc_id)
            batch.set(ref, payload)
        batch.commit()
        print(f"Committed {offset + len(chunk)}/{len(docs)}")


def command_inspect(args: argparse.Namespace) -> None:
    db = init_firestore(args.service_account)
    inspect_numbered_docs(db, args.collection, args.sample_ids)


def command_update(args: argparse.Namespace) -> None:
    languages = parse_language_order(args.language_order)
    data = load_audit(args.audit)
    validate_audit(data, languages, require_question_id=args.require_question_id)
    docs = build_numbered_documents(data, languages)

    print(f"Prepared {len(docs)} docs for `{args.collection}`")
    print(f"Language order: {', '.join(languages)}")
    print(f"First doc id: {docs[0][0]} translation={docs[0][1]['translation']!r}")
    print(f"Last doc id: {docs[-1][0]} translation={docs[-1][1]['translation']!r}")

    if args.dry_run:
        print("Dry-run only. Nothing written.")
        return
    if not args.commit:
        raise SystemExit("Refusing to write without --commit. Use --dry-run to preview.")

    db = init_firestore(args.service_account)
    commit_batches(db, args.collection, docs)
    print("Done.")


def command_backup(args: argparse.Namespace) -> None:
    languages = parse_language_order(args.language_order)
    output = args.output or default_backup_output_path()

    db = init_firestore(args.service_account)
    numbered_docs = fetch_numbered_docs(db, args.collection)
    if not numbered_docs:
        raise SystemExit(f"No numeric docs found in collection `{args.collection}`")

    data: dict[str, list[dict[str, Any]]] = {lang: [] for lang in languages}
    unknown_languages: Counter[str] = Counter()
    for doc_number, payload in numbered_docs:
        lang = str(payload.get("translation", "")).strip()
        if lang not in data:
            unknown_languages[lang or "<missing>"] += 1
            continue
        data[lang].append(from_firestore_document(payload))

    if unknown_languages:
        print(f"Skipped docs with unknown languages: {dict(unknown_languages)}")

    validate_audit(data, languages, require_question_id=args.require_question_id)

    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

    print(f"Wrote Firestore backup to {output}")
    print(f"Collection: {args.collection}")
    print(f"Numeric docs read: {len(numbered_docs)}")
    for lang in languages:
        print(f"{lang}: {len(data[lang])}")


def command_question_id_status(args: argparse.Namespace) -> None:
    languages = parse_language_order(args.language_order)
    data = load_audit(args.audit)
    validate_audit(data, languages, require_question_id=False)

    missing_counts, duplicate_ids, inconsistent_ids, block_order_matches = summarize_question_id_readiness(data, languages)

    print("Question ID readiness report")
    print(f"Languages: {', '.join(languages)}")
    print()

    for lang in languages:
        print(f"{lang}:")
        print(f"  missing questionId: {missing_counts[lang]}")
        print(f"  duplicate questionId: {len(duplicate_ids[lang])}")
        if duplicate_ids[lang]:
            preview = ", ".join(duplicate_ids[lang][:10])
            suffix = " ..." if len(duplicate_ids[lang]) > 10 else ""
            print(f"    examples: {preview}{suffix}")
        inconsistent = inconsistent_ids.get(lang, [])
        print(f"  group order mismatch vs {languages[0]}: {len(inconsistent)}")
        if inconsistent:
            preview = ", ".join(inconsistent[:10])
            suffix = " ..." if len(inconsistent) > 10 else ""
            print(f"    groups: {preview}{suffix}")
        print(f"  block order matches {languages[0]}: {'yes' if block_order_matches.get(lang, True) else 'no'}")
        print()


def command_annotate_question_ids_from_resources(args: argparse.Namespace) -> None:
    data = load_audit(args.audit)
    languages = parse_language_order(args.languages)

    resource_arrays_by_lang = {lang: load_resource_question_arrays(lang) for lang in languages}
    output_data = json.loads(json.dumps(data))

    unmatched: dict[str, list[str]] = defaultdict(list)
    assigned: dict[str, int] = defaultdict(int)

    for lang in languages:
        if lang not in output_data:
            raise ValueError(f"Language {lang!r} not found in audit")

        arrays = resource_arrays_by_lang[lang]
        lookups: dict[tuple[str, str], dict[str, list[int]]] = {}
        for group_key, array_name in QUESTION_ARRAY_BY_GROUP.items():
            if array_name not in arrays:
                unmatched[lang].append(f"missing resource array {array_name}")
                continue
            group_lookup: dict[str, list[int]] = defaultdict(list)
            for index, question_text in enumerate(arrays[array_name], start=1):
                group_lookup[question_text].append(index)
            lookups[group_key] = group_lookup

        for item in output_data[lang]:
            group_key = (str(item.get("categoryType", "")).strip(), str(item.get("type", "")).strip())
            question_text = normalize_question_text(str(item.get("question", "")))
            indexes = lookups.get(group_key, {}).get(question_text, [])
            if len(indexes) != 1:
                unmatched[lang].append(f"{group_key[0]}/{group_key[1]} :: {item.get('question', '')}")
                continue
            index = indexes[0]
            item[QUESTION_ID_FIELD] = build_question_id(group_key[0], group_key[1], index)
            assigned[lang] += 1

    output_path = args.output or args.audit
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", encoding="utf-8") as fh:
        json.dump(output_data, fh, ensure_ascii=False, indent=2)
        fh.write("\n")

    print(f"Wrote annotated audit to {output_path}")
    for lang in languages:
        print(f"{lang}: assigned questionId for {assigned[lang]} items")
        print(f"{lang}: unmatched {len(unmatched[lang])} items")
        if unmatched[lang]:
            preview = "\n".join(unmatched[lang][:10])
            suffix = f"\n... and {len(unmatched[lang]) - 10} more" if len(unmatched[lang]) > 10 else ""
            print(preview + suffix)
        print()


def command_export_question_id_report(args: argparse.Namespace) -> None:
    data = load_audit(args.audit)
    all_languages = parse_language_order(args.language_order)
    resource_languages = parse_language_order(args.resource_languages)

    validate_audit(data, all_languages, require_question_id=False)
    missing_counts, duplicate_ids, inconsistent_ids, block_order_matches = summarize_question_id_readiness(
        data, all_languages
    )

    resource_arrays_by_lang = {lang: load_resource_question_arrays(lang) for lang in resource_languages}
    unmatched_by_lang: dict[str, list[str]] = defaultdict(list)
    assigned_by_lang: dict[str, int] = defaultdict(int)

    for lang in resource_languages:
        arrays = resource_arrays_by_lang[lang]
        lookups: dict[tuple[str, str], dict[str, list[int]]] = {}
        for group_key, array_name in QUESTION_ARRAY_BY_GROUP.items():
            if array_name not in arrays:
                unmatched_by_lang[lang].append(f"missing resource array {array_name}")
                continue
            group_lookup: dict[str, list[int]] = defaultdict(list)
            for index, question_text in enumerate(arrays[array_name], start=1):
                group_lookup[question_text].append(index)
            lookups[group_key] = group_lookup

        for item in data[lang]:
            if str(item.get(QUESTION_ID_FIELD, "")).strip():
                assigned_by_lang[lang] += 1
                continue
            group_key = (str(item.get("categoryType", "")).strip(), str(item.get("type", "")).strip())
            question_text = normalize_question_text(str(item.get("question", "")))
            indexes = lookups.get(group_key, {}).get(question_text, [])
            if len(indexes) != 1:
                unmatched_by_lang[lang].append(f"{group_key[0]}/{group_key[1]} :: {item.get('question', '')}")
            else:
                assigned_by_lang[lang] += 1

    output_lines: list[str] = [
        "# Phase 1 Question ID Readiness Report",
        "",
        f"Source audit: `{args.audit}`",
        "",
        f"All languages checked: `{', '.join(all_languages)}`",
        f"Resource-assisted languages: `{', '.join(resource_languages)}`",
        "",
        "## Global status",
        "",
    ]

    for lang in all_languages:
        output_lines.extend(
            [
                f"### {lang}",
                "",
                f"- missing `questionId`: **{missing_counts[lang]}**",
                f"- duplicate `questionId`: **{len(duplicate_ids[lang])}**",
                f"- group order mismatch vs `{all_languages[0]}`: **{len(inconsistent_ids.get(lang, []))}**",
                f"- block order matches `{all_languages[0]}`: **{'yes' if block_order_matches.get(lang, True) else 'no'}**",
                "",
            ]
        )
        if duplicate_ids[lang]:
            output_lines.append("- duplicate examples:")
            for question_id in duplicate_ids[lang][:20]:
                output_lines.append(f"  - `{question_id}`")
            output_lines.append("")

    output_lines.extend(
        [
            "## Resource-assisted matching status",
            "",
            "This section shows how many questions are already resolved with stable IDs, either because the ID is already present or because it can be matched via canonical Android resource arrays.",
            "",
        ]
    )

    for lang in resource_languages:
        output_lines.extend(
            [
                f"### {lang}",
                "",
                f"- resolved or already assigned: **{assigned_by_lang[lang]}**",
                f"- unresolved manual review items: **{len(unmatched_by_lang[lang])}**",
                "",
            ]
        )
        if unmatched_by_lang[lang]:
            output_lines.append("#### Unresolved items")
            output_lines.append("")
            for item in unmatched_by_lang[lang]:
                output_lines.append(f"- {item}")
            output_lines.append("")

    output_path = args.output
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text("\n".join(output_lines).rstrip() + "\n", encoding="utf-8")
    print(f"Wrote questionId readiness report to {output_path}")


def main() -> None:
    parser = argparse.ArgumentParser(description="Update Firestore questions from questions_audit.json")
    subparsers = parser.add_subparsers(required=True)

    inspect_parser = subparsers.add_parser("inspect", help="Inspect current numbered Firestore docs")
    inspect_parser.add_argument("--service-account", type=Path, required=True)
    inspect_parser.add_argument("--collection", default=DEFAULT_COLLECTION)
    inspect_parser.add_argument("--sample-ids", nargs="+", default=["0", "645", "646", "1291", "1292", "1937"])
    inspect_parser.set_defaults(func=command_inspect)

    update_parser = subparsers.add_parser("update", help="Overwrite numbered Firestore docs from audit JSON")
    update_parser.add_argument("--service-account", type=Path, required=True)
    update_parser.add_argument("--audit", type=Path, default=DEFAULT_AUDIT_PATH)
    update_parser.add_argument("--collection", default=DEFAULT_COLLECTION)
    update_parser.add_argument("--language-order", default=DEFAULT_LANGUAGE_ORDER)
    update_parser.add_argument("--dry-run", action="store_true")
    update_parser.add_argument("--commit", action="store_true")
    update_parser.add_argument("--require-question-id", action="store_true")
    update_parser.set_defaults(func=command_update)

    backup_parser = subparsers.add_parser(
        "backup",
        help="Read numbered Firestore question docs and write a local audit-format JSON backup",
    )
    backup_parser.add_argument("--service-account", type=Path, required=True)
    backup_parser.add_argument("--output", type=Path)
    backup_parser.add_argument("--collection", default=DEFAULT_COLLECTION)
    backup_parser.add_argument("--language-order", default=DEFAULT_LANGUAGE_ORDER)
    backup_parser.add_argument("--require-question-id", action="store_true")
    backup_parser.set_defaults(func=command_backup)

    status_parser = subparsers.add_parser("question-id-status", help="Report questionId readiness in audit JSON")
    status_parser.add_argument("--audit", type=Path, default=DEFAULT_AUDIT_PATH)
    status_parser.add_argument("--language-order", default=DEFAULT_LANGUAGE_ORDER)
    status_parser.set_defaults(func=command_question_id_status)

    annotate_parser = subparsers.add_parser(
        "annotate-question-ids-from-resources",
        help="Fill questionId in audit JSON using aligned Android resource question arrays for specific languages",
    )
    annotate_parser.add_argument("--audit", type=Path, default=DEFAULT_AUDIT_PATH)
    annotate_parser.add_argument("--languages", default="ru,hy")
    annotate_parser.add_argument("--output", type=Path)
    annotate_parser.set_defaults(func=command_annotate_question_ids_from_resources)

    report_parser = subparsers.add_parser(
        "export-question-id-report",
        help="Export a markdown report with questionId readiness and unresolved resource-based matches",
    )
    report_parser.add_argument("--audit", type=Path, default=DEFAULT_AUDIT_PATH)
    report_parser.add_argument("--language-order", default=DEFAULT_LANGUAGE_ORDER)
    report_parser.add_argument("--resource-languages", default="ru,hy")
    report_parser.add_argument("--output", type=Path, required=True)
    report_parser.set_defaults(func=command_export_question_id_report)

    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
