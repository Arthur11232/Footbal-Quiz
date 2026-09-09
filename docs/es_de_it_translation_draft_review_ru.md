# Draft Review: Spanish, German, Italian Questions

Статус: draft review scaffold.

Дата: 2026-09-09.

Branch: `codex/add-es-de-it-localization`.

## Цель

Подготовить безопасный review-документ перед переводом вопросов Football Quiz на новые языки:

- Spanish (`es`);
- German (`de`);
- Italian (`it`).

Этот файл нужен, чтобы ревьюить работу поэтапно и не смешивать перевод, проверку фактов и Firestore update в один рискованный шаг.

## Важное правило

Нельзя считать перевод готовым, пока не выполнены все условия:

- каждый `questionId` сохранён без изменений;
- `categoryType` и `type` совпадают с source audit;
- вопрос на `es/de/it` совпадает по смыслу с `en/ru/hy`;
- варианты `answer_A..answer_D` совпадают по смыслу с исходными вариантами;
- `right_answer` является одним из вариантов текущего языка;
- нет пустых questions/answers;
- нет случайного смешивания языков внутри одного вопроса;
- финальный audit проходит `validate-alignment`;
- Firestore update проходит сначала только через `--dry-run`.

## Source Of Truth

Для новых языков источником истины является свежий Firestore backup:

```text
app/questions_audit_backup_BEFORE_es_de_it_2026_09_09.json
```

Старый локальный audit больше не является source of truth и сохранён только как archive:

```text
app/questions_audit_archive_2026_09_09.json
```

## Рабочий Файл

Главный workbench:

```text
app/questions_translation_workbench_es_de_it_2026_09_09.json
```

В нём 689 items. Каждый item содержит:

- `questionId`;
- `categoryType`;
- `type`;
- `source.en`;
- `source.ru`;
- `source.hy`;
- `targets.es`;
- `targets.de`;
- `targets.it`.

На момент создания этого review-файла target-переводы ещё пустые и должны быть заполнены отдельно.

## Batch-Файлы Для Ревью

Перевод делаем не одним огромным файлом, а по блокам:

| Batch file | Questions | Priority | Status |
| --- | ---: | --- | --- |
| `app/question_translation_batches_es_de_it_2026_09_09/top5_spain.json` | 71 | High | translation needed |
| `app/question_translation_batches_es_de_it_2026_09_09/top5_germany.json` | 67 | High | translation needed |
| `app/question_translation_batches_es_de_it_2026_09_09/top5_italy.json` | 70 | High | translation needed |
| `app/question_translation_batches_es_de_it_2026_09_09/top5_france.json` | 66 | Medium | translation needed |
| `app/question_translation_batches_es_de_it_2026_09_09/top5_english.json` | 76 | Medium | translation needed |
| `app/question_translation_batches_es_de_it_2026_09_09/uefa_champions_league.json` | 47 | Medium | translation needed |
| `app/question_translation_batches_es_de_it_2026_09_09/uefa_europa_league.json` | 44 | Medium | translation needed |
| `app/question_translation_batches_es_de_it_2026_09_09/world_cup_world_championship.json` | 54 | Medium | translation needed |
| `app/question_translation_batches_es_de_it_2026_09_09/uefa_super_cup.json` | 31 | Low | translation needed |
| `app/question_translation_batches_es_de_it_2026_09_09/uefa_european_championship.json` | 21 | Low | translation needed |
| `app/question_translation_batches_es_de_it_2026_09_09/ron_mes_vsrm.json` | 71 | Low | translation needed |
| `app/question_translation_batches_es_de_it_2026_09_09/real_barce_vsrb.json` | 71 | Low | translation needed |

Почему первый приоритет `Spain/Germany/Italy`:

- эти языки напрямую усиливают соответствующие football-аудитории;
- легче проверить термины национальных лиг;
- меньше риск маркетингово странных переводов на первом публичном rollout.

## Как Ревьюить Один Batch

Для каждого вопроса проверяем:

- `source.en.question`;
- `source.ru.question`;
- `source.hy.question`;
- `targets.es.question`;
- `targets.de.question`;
- `targets.it.question`;
- все варианты ответов;
- `right_answer`.

Если сомневаемся в футбольном факте, вопрос лучше пометить как `needs_fact_check`, а не угадывать.

Рекомендуемый temporary marker внутри batch item:

```json
{
  "review_status": "needs_fact_check",
  "review_note": "Check exact season winner before Firestore update"
}
```

Эти поля можно держать только в batch/workbench review-файлах. В финальный Firestore audit они не должны попадать.

## Команды Проверки

Проверить свежий backup:

```bash
python3 tools/update_firestore_questions.py validate-alignment \
  --audit app/questions_audit_backup_BEFORE_es_de_it_2026_09_09.json \
  --language-order en,ru,hy
```

Применить заполненный batch обратно в workbench:

```bash
python3 tools/update_firestore_questions.py apply-translation-batch \
  --workbench app/questions_translation_workbench_es_de_it_2026_09_09.json \
  --batch app/question_translation_batches_es_de_it_2026_09_09/top5_germany.json \
  --target-languages es,de,it \
  --require-complete \
  --output app/questions_translation_workbench_es_de_it_2026_09_09.json
```

Собрать финальный audit после заполнения всех batch-файлов:

```bash
python3 tools/update_firestore_questions.py build-audit-from-workbench \
  --source-audit app/questions_audit_backup_BEFORE_es_de_it_2026_09_09.json \
  --workbench app/questions_translation_workbench_es_de_it_2026_09_09.json \
  --source-languages en,ru,hy \
  --target-languages es,de,it \
  --output app/questions_audit_es_de_it_2026_09_001.json
```

Dry-run перед Firestore:

```bash
python3 tools/update_firestore_questions.py update \
  --service-account tools/service-account.local.json \
  --audit app/questions_audit_es_de_it_2026_09_001.json \
  --language-order en,ru,hy,es,de,it \
  --require-question-id \
  --dry-run
```

## Что Пока Не Делаем

- Не заливаем `es/de/it` в Firestore.
- Не создаём production audit с пустыми target-переводами.
- Не меняем scoring/progress/certificate logic.
- Не трогаем backend/PHP/KMP/Compose.
- Не отправляем вопросы во внешний переводчик без явного разрешения владельца проекта.

## Machine Draft Helper

Добавлен локальный helper для создания machine-draft batch-файлов:

```text
tools/draft_translate_question_batches.py
```

Он сохраняет результат в отдельную output-папку и помечает batch как `machine_draft_needs_review`. Helper не должен использоваться для production update без ручной проверки.

Пример запуска после явного разрешения на внешний machine translation:

```bash
env PYTHONPATH=/tmp/fq_translate_deps python3 tools/draft_translate_question_batches.py \
  --batch-dir app/question_translation_batches_es_de_it_2026_09_09 \
  --output-dir app/question_translation_batches_es_de_it_2026_09_09_machine_draft \
  --cache app/question_translation_batches_es_de_it_2026_09_09_machine_draft_cache.json \
  --only uefa_european_championship.json \
  --sleep-seconds 0.05 \
  --retries 2
```

## Следующий Практический Шаг

Начать с первого High-priority batch:

```text
app/question_translation_batches_es_de_it_2026_09_09/top5_spain.json
```

После заполнения `top5_spain.json` нужно:

1. применить batch обратно в workbench через `apply-translation-batch --require-complete`;
2. проверить, что команда не падает;
3. перейти к `top5_germany.json`;
4. затем к `top5_italy.json`.
