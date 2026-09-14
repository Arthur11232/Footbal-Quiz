# Draft Review: Spanish, German, Italian Questions

Статус: active localization tracking / Firestore не обновлялся.

Дата: 2026-09-09.

Branch: `codex/add-es-de-it-localization`.

## Цель

Подготовить безопасный review-документ перед переводом вопросов Football Quiz на новые языки:

- Spanish (`es`);
- German (`de`);
- Italian (`it`).

Этот файл нужен, чтобы ревьюить работу поэтапно и не смешивать перевод, проверку фактов и Firestore update в один рискованный шаг.

Генеральный сводный отчёт по всем 12 файлам Human Review (689 вопросов): [review_all_12_batches_human_review_es_de_it_ru.md](review_all_12_batches_human_review_es_de_it_ru.md).

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

Для новых языков рабочим источником истины является current source audit:

```text
app/questions_audit_source_current_es_de_it_2026_09_09.json
```

Он создан из свежего Firestore backup и содержит локальные fact/content corrections, которые уже одобрены ревью. Сейчас применены:

- `spain_037`: правильный ответ обновлён на `Lionel Messi / Лионель Месси / Լիոնել Մեսսի`;
- `spain_058`: вариант `answer_D` заменён с `Athletic Bilbao / Атлетик Билбао / Ատլետիկ Բիլբաո` на `Sevilla / Севилья / Սևիլիա`.
- `france_038`: лучший бомбардир сборной Франции обновлён с `Olivier Giroud` на `Kylian Mbappé` по актуальным данным FFF/FIFA.
- `champions_league_006`: правильный ответ обновлён с `Milan` на `Milan, Bayern München`, потому что среди предложенных вариантов у Milan и Bayern по 11 финалов Кубка европейских чемпионов / Champions League.
- `champions_league_024`: правильный ответ обновлён с `Italy` на `Spain and Italy`, потому что после побед PSG в 2025 и 2026 по данным UEFA у тренеров Италии и Испании равенство `13 / 13`.
- `champions_league_030`: вопрос уточнён через `Of these...` / `Из перечисленных...`, чтобы ответ `Bafé Gomis` был корректен среди предложенных вариантов после актуального рекорда Mohamed Salah.
- `champions_league_037`: дубликат вопроса про победителя Лиги чемпионов 2023/24 заменён на вопрос о финале 2025/26, где PSG победил Arsenal.
- `europa_league_009`: рекорд по матчам обновлён с `Giuseppe Bergomi` на `João Moutinho` по UEFA all-time stats.
- `europa_league_015`: количество клубов-победителей UEFA Cup / Europa League обновлено с `30` на `31`.
- `europa_league_016`: убрана неоднозначность вариантов, где `Tottenham` был и отдельным вариантом, и частью правильного ответа.
- `europa_league_024`: убран tie-risk между Netherlands/Germany в вариантах ответа.
- `europa_league_027`: убран tie-risk между France/Portugal в вариантах ответа.
- `europa_league_034`: дубликат вопроса про сезон 2024/25 заменён на вопрос о победителе сезона 2025/26, `Aston Villa`.
- `world_cup_007`: после финала ЧМ-2026 правильный ответ обновлён на `Germany and Argentina`, потому что обе сборные имеют по 4 поражения в финалах.
- `world_cup_015`: список действующих чемпионов XXI века, прошедших групповой этап, обновлён на `Brazil, France and Argentina`.
- `world_cup_022`: all-time top scorer обновлён с `Miroslav Klose` на `Kylian Mbappé` после ЧМ-2026.
- `world_cup_042`: вопрос про FIFA Club World Cup 2025 переведён из будущего времени в прошедшее.
- `world_cup_043`: вопрос про финал FIFA World Cup 2026 переведён из будущего времени в прошедшее.
- `vsrb_007`: английская формулировка очищена с `flew from` на корректное `relegated from`.
- `vsrb_011`: вопрос уточнён как победа во всех основных официальных турнирах за один календарный год.
- `vsrb_017`: устаревшее название `Wanda Metropolitano` заменено на нейтральное `Metropolitano Stadium`.
- `vsrb_029`: вопрос уточнён как all-time лидер по третьим местам в LaLiga, чтобы ответ `None` был однозначным.
- `vsrb_051`: `Fly Emirates` заменено на актуальное `Emirates`.
- `vsrb_052`: вопрос про Luis Figo исправлен: правильный ответ теперь `Barcelona`, потому что переход в Real Madrid был из Barcelona.
- `vsrb_071`: правильный ответ обновлён на `Both`, потому что в сезоне 2018-2019 Barcelona выиграла внутренние трофеи, а Real Madrid выиграл FIFA Club World Cup 2018.
- `spain_050`: source-дубль с `spain_020` разведен в отдельный вопрос про клуб, выигравший LaLiga ровно два раза.

Свежий Firestore backup остаётся нетронутым архивным снимком перед локализацией:

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

На текущем этапе все TOP5, UEFA/World и Versus batch-и заполнены и применены в workbench. Firestore пока не обновлялся.

## Batch-Файлы Для Ревью

Перевод делаем не одним огромным файлом, а по блокам:

| Batch file | Questions | Priority | Status |
| --- | ---: | --- | --- |
| `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/top5_spain.json` | 71 | High | approved and applied to workbench |
| `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/top5_germany.json` | 67 | High | approved and applied to workbench |
| `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/top5_italy.json` | 70 | High | approved and applied to workbench |
| `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/top5_france.json` | 66 | Medium | approved and applied to workbench |
| `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/top5_english.json` | 76 | Medium | approved and applied to workbench |
| `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/uefa_champions_league.json` | 47 | Medium | approved and applied to workbench |
| `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/uefa_europa_league.json` | 44 | Medium | approved and applied to workbench |
| `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/world_cup_world_championship.json` | 54 | Medium | approved and applied to workbench |
| `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/uefa_super_cup.json` | 31 | Low | prepared and applied to workbench |
| `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/uefa_european_championship.json` | 21 | Low | prepared and applied to workbench |
| `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/ron_mes_vsrm.json` | 71 | Low | prepared and applied to workbench |
| `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/real_barce_vsrb.json` | 71 | Low | approved and applied to workbench |

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

Проверить текущий source-audit:

```bash
python3 tools/update_firestore_questions.py validate-alignment \
  --audit app/questions_audit_source_current_es_de_it_2026_09_09.json \
  --language-order en,ru,hy
```

Применить approved batch обратно в workbench:

```bash
python3 tools/update_firestore_questions.py apply-translation-batch \
  --workbench app/questions_translation_workbench_es_de_it_2026_09_09.json \
  --batch app/question_translation_batches_es_de_it_2026_09_09_manual_draft/uefa_champions_league.json \
  --target-languages es,de,it \
  --require-complete \
  --output app/questions_translation_workbench_es_de_it_2026_09_09.json
```

Собрать финальный audit после заполнения всех batch-файлов:

```bash
python3 tools/update_firestore_questions.py build-audit-from-workbench \
  --source-audit app/questions_audit_source_current_es_de_it_2026_09_09.json \
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

## Текущий Статус и Следующий Шаг

Финальный human-review завершён по всем 12 batch-файлам.

Сводный отчёт:

```text
docs/review_all_12_batches_human_review_es_de_it_ru.md
```

Итог:

- `top5_spain.json`: 71/71 approved;
- `top5_germany.json`: 67/67 approved;
- `top5_italy.json`: 70/70 approved;
- `top5_france.json`: 66/66 approved;
- `top5_english.json`: 76/76 approved;
- `uefa_champions_league.json`: 47/47 approved;
- `uefa_europa_league.json`: 44/44 approved;
- `world_cup_world_championship.json`: 54/54 approved;
- `real_barce_vsrb.json`: 71/71 approved;
- `uefa_super_cup.json`: 31/31 approved;
- `uefa_european_championship.json`: 21/21 approved;
- `ron_mes_vsrm.json`: 71/71 approved.

Общий итог:

- approved: **689 / 689**;
- flagged: **0**;
- translated target blocks: **2067** (`es`, `de`, `it`);
- missing fields: **0**;
- invalid `right_answer`: **0**;
- same-type duplicates: **0**;
- final audit alignment: **OK**;
- Firestore dry-run: **OK**, `4134` docs prepared;
- Firestore real update: **не выполнялся**.

Последняя точечная правка после human-review:

- `vsrb_006` Italian typo fixed: `giocado` -> `giocato`.

Две проверки из human-review были ложными тревогами и оставлены без изменения данных:

- `super_cup_018`: Luis Enrique остаётся в правильном варианте, потому что по UEFA имеет back-to-back UEFA Super Cup wins 2025/2026 с Paris;
- `vsrm_015`: Cristiano Ronaldo остаётся правильным ответом, потому что по FIFA он имеет рекордные 4 гола в финалах FIFA Club World Cup, на один больше Lionel Messi.

Финальные файлы перед Firestore:

```text
app/questions_audit_es_de_it_2026_09_001.json
app/questions_audit_backup_BEFORE_es_de_it_firestore_update_2026_09_10.json
```

Следующий шаг:

1. при необходимости ещё раз выполнить `validate-alignment`;
2. ещё раз выполнить Firestore `--dry-run`;
3. запускать реальный Firestore `--commit` только после отдельного явного подтверждения.
