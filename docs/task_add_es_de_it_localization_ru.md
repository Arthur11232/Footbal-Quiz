# Task: Spanish, German, Italian Localization

Статус: Phase 1-2 done, Phase 3 started.

Дата старта: 2026-09-08.

Branch: `codex/add-es-de-it-localization`.

Base: `master` после merge rating prompt release `2.1.1`.

## Цель

Добавить в Football Quiz полноценную поддержку испанского, немецкого и итальянского языков без поломки текущих языков `en`, `ru`, `hy`, локального Realm progress, Firestore question sync и сертификатов.

Это большой product release, ориентировочно `2.2.0`, а не маленький patch `2.1.x`.

## Почему это важно

- Испанский язык открывает большую football-аудиторию в Испании и Латинской Америке.
- Немецкий язык усиливает Bundesliga/Germany сегмент.
- Итальянский язык логично ложится на Serie A/Italy аудиторию.
- Новые языки могут дать ASO/SEO прирост в Google Play, если перевести не только app UI, но и store listing.

## Текущее состояние

- App UI сейчас имеет ресурсы:
  - `values` как English/default;
  - `values-ru`;
  - `values-hy`.
- Language dialog сейчас содержит только:
  - English;
  - Հայերեն;
  - Русский.
- `StartPageFragment` сейчас мапит выбор языка только в:
  - `en`;
  - `hy`;
  - `ru`.
- `QuestionsRepositoryImpl` уже умеет sync выбранной локали из Firestore и сохраняет `questionId`, что важно для safe locale switch.
- В `strings.xml` лежат не только UI strings, но и большие question arrays. Поэтому добавление языков затрагивает и UI, и content.

## Основной принцип

Нельзя просто добавить `values-es/de/it` и считать задачу готовой.

Нужно обеспечить одинаковую структуру вопросов во всех языках:

- один общий `questionId`;
- один и тот же смысл вопроса;
- те же варианты ответов по смыслу;
- правильный ответ должен соответствовать тексту варианта в конкретном языке;
- порядок документов Firestore не должен ломать старые версии приложения;
- старые `en/ru/hy` не должны измениться без отдельной проверки.

## Phase 1: UI Localization

Цель: добавить интерфейсные строки для `es`, `de`, `it`.

Implementation status: базовый UI/resource layer добавлен для Spanish, German и Italian. В новые `values-es`, `values-de`, `values-it` намеренно не добавлены question arrays, потому что вопросы должны идти через отдельный audit/Firestore flow.

Tasks:

- создать `app/src/main/res/values-es/strings.xml`;
- создать `app/src/main/res/values-de/strings.xml`;
- создать `app/src/main/res/values-it/strings.xml`;
- перевести только UI strings и reusable labels;
- не переводить question arrays вручную в этом же шаге, если они будут переноситься через Firestore audit flow;
- проверить, что все string keys совпадают с default `values/strings.xml`;
- убедиться, что Android resource merge проходит без missing/extra keys.

Acceptance criteria:

- `./gradlew :app:compileDevDebugKotlin` проходит;
- app стартует на English/default;
- текущие `ru` и `hy` не ломаются;
- новые resource folders не содержат пустых или English-placeholder строк, кроме временно явно помеченных технических случаев.

## Phase 2: Language Picker

Цель: добавить новые языки в UI выбора языка.

Implementation status: language picker расширен до 6 языков, а `StartPageFragment` теперь сохраняет locale codes `es`, `de`, `it` вместе с уже существующими `en`, `hy`, `ru`.

Tasks:

- добавить RadioButton для Spanish;
- добавить RadioButton для German;
- добавить RadioButton для Italian;
- обновить маппинг `checkedId -> locale` в `StartPageFragment`;
- сохранить English как основной/default язык;
- проверить повторный клик по уже выбранному языку;
- убедиться, что смена языка запускает корректный `recreate()`;
- убедиться, что выбранный язык сохраняется в settings.

Locale codes:

- English: `en`;
- Armenian: `hy`;
- Russian: `ru`;
- Spanish: `es`;
- German: `de`;
- Italian: `it`.

Acceptance criteria:

- можно выбрать каждый язык;
- повторный выбор текущего языка не ломает dialog;
- app после restart остаётся на выбранном языке;
- если вопросов для языка нет или sync не готов, пользователь получает понятный `language_sync_not_ready`.

## Phase 3: Questions Catalog Preparation

Цель: подготовить новые языки для Firestore без потери соответствия вопросов.

Implementation status: создан свежий read-only backup текущего Firestore перед началом подготовки новых языков:

```text
app/questions_audit_backup_BEFORE_es_de_it_2026_09_09.json
```

Также создан translation workbench для безопасного перевода новых языков:

```text
app/questions_translation_workbench_es_de_it_2026_09_09.json
```

Для удобной поэтапной работы workbench дополнительно разрезан на category batch-файлы:

```text
app/question_translation_batches_es_de_it_2026_09_09/
```

Состав batch-файлов:

- `top5_france.json` — 66 items;
- `top5_germany.json` — 67 items;
- `top5_italy.json` — 70 items;
- `top5_english.json` — 76 items;
- `top5_spain.json` — 71 items;
- `uefa_super_cup.json` — 31 items;
- `uefa_europa_league.json` — 44 items;
- `uefa_champions_league.json` — 47 items;
- `uefa_european_championship.json` — 21 items;
- `world_cup_world_championship.json` — 54 items;
- `ron_mes_vsrm.json` — 71 items;
- `real_barce_vsrb.json` — 71 items.

Workbench построен от Firestore backup, а не от локального архивного `app/questions_audit_archive_2026_09_09.json`. В нём 689 items, каждый item содержит:

- `questionId`;
- `categoryType`;
- `type`;
- `source.en`;
- `source.ru`;
- `source.hy`;
- пустые target-блоки `targets.es`, `targets.de`, `targets.it`.

Перед сборкой финального audit-файла target-блоки должны быть заполнены переводами, а затем проверены через строгую `questionId` alignment validation.

Добавлены новые команды в `tools/update_firestore_questions.py`:

- `backup`: read-only export текущей Firestore коллекции в audit JSON;
- `validate-alignment`: строгая проверка языков по `questionId`, `categoryType`, `type`, обязательным полям и валидности `right_answer`;
- `export-translation-workbench`: подготовка рабочего файла для перевода target languages;
- `export-translation-batch`: экспорт отдельного category/type batch из workbench;
- `apply-translation-batch`: применение заполненного batch обратно в workbench;
- `build-audit-from-workbench`: сборка финального audit из заполненного workbench. Команда намеренно падает, если хотя бы один target-перевод пустой.

Важно по machine translation: отправка Firestore-derived вопросов во внешний переводчик требует явного разрешения владельца проекта. Без такого разрешения используем только локальные batch-файлы и ручной/контролируемый перевод.

Важно: свежий Firestore backup не полностью совпадает со старым локальным audit-файлом. Поэтому для `es/de/it` источником истины на этом этапе считается именно backup из Firestore.

Старый локальный файл переименован в архив:

```text
app/questions_audit_archive_2026_09_09.json
```

Краткая сверка backup vs archive audit:

- `en`: отличается 116 items, в основном `France`, `Germany`, 1 item `vsRB`;
- `ru`: отличается 45 items, в основном `France`, `Germany`, `Italy`, `English`, `Spain`;
- `hy`: отличается 22 items, в основном `France`, `Germany`, плюс единичные `World Championship`, `Spain`, `Champions League`;
- `questionId` sets совпадают во всех трёх языках;
- duplicate `questionId` не найдено;
- `right_answer` валиден внутри вариантов во всех трёх языках.

Tasks:

- взять текущий clean audit/canonical source как источник истины;
- добавить `es`, `de`, `it` в новый audit-format файл;
- не менять `questionId`;
- не менять category/type mapping;
- не менять количество вопросов внутри языков;
- для каждого `questionId` проверить alignment между `en`, `ru`, `hy`, `es`, `de`, `it`;
- отдельно проверить спорные футбольные факты, особенно сезоны `2023/2024`, `2024/2025`, UEFA/World Cup и versus blocks.

Новый файл должен быть отдельным, например:

```text
app/questions_audit_es_de_it_2026_09_001.json
```

Acceptance criteria:

- количество вопросов одинаковое во всех 6 языках;
- каждый item имеет `questionId`;
- нет пустых `question`, `answer_A`, `answer_B`, `answer_C`, `answer_D`, `right_answer`;
- `right_answer` существует среди вариантов ответа текущего языка;
- нет duplicate `questionId` внутри одного языка;
- нет случайного смешивания языков внутри одного вопроса.

## Phase 4: Validation Tooling

Цель: перед Firestore update иметь строгую проверку.

Tasks:

- расширить или использовать существующий `tools/update_firestore_questions.py`;
- добавить validation для `es,de,it`;
- запускать dry-run перед любым update;
- сохранить Firestore backup перед update;
- сохранить локальный backup audit после update.

Команды должны идти в таком порядке:

```text
python3 tools/update_firestore_questions.py backup --service-account tools/service-account.local.json --output app/questions_audit_backup_BEFORE_es_de_it_YYYY_MM_DD.json --language-order en,ru,hy --require-question-id
```

```text
python3 tools/update_firestore_questions.py validate-alignment --audit app/questions_audit_backup_BEFORE_es_de_it_YYYY_MM_DD.json --language-order en,ru,hy
```

```text
python3 tools/update_firestore_questions.py export-translation-workbench --audit app/questions_audit_backup_BEFORE_es_de_it_YYYY_MM_DD.json --source-languages en,ru,hy --target-languages es,de,it --output app/questions_translation_workbench_es_de_it_YYYY_MM_DD.json
```

```text
python3 tools/update_firestore_questions.py export-translation-batch --workbench app/questions_translation_workbench_es_de_it_YYYY_MM_DD.json --category-type TOP5 --place-type Germany --target-languages es,de,it --output app/question_translation_batches_es_de_it_YYYY_MM_DD/top5_germany.json
```

```text
python3 tools/update_firestore_questions.py apply-translation-batch --workbench app/questions_translation_workbench_es_de_it_YYYY_MM_DD.json --batch app/question_translation_batches_es_de_it_YYYY_MM_DD/top5_germany.json --target-languages es,de,it --require-complete --output app/questions_translation_workbench_es_de_it_YYYY_MM_DD.json
```

```text
python3 tools/update_firestore_questions.py build-audit-from-workbench --source-audit app/questions_audit_backup_BEFORE_es_de_it_YYYY_MM_DD.json --workbench app/questions_translation_workbench_es_de_it_YYYY_MM_DD.json --source-languages en,ru,hy --target-languages es,de,it --output app/questions_audit_es_de_it_2026_09_001.json
```

```text
python3 tools/update_firestore_questions.py update --service-account tools/service-account.local.json --audit app/questions_audit_es_de_it_2026_09_001.json --language-order en,ru,hy,es,de,it --require-question-id --dry-run
```

```text
python3 tools/update_firestore_questions.py update --service-account tools/service-account.local.json --audit app/questions_audit_es_de_it_2026_09_001.json --language-order en,ru,hy,es,de,it --require-question-id --commit
```

Acceptance criteria:

- backup создан до update;
- dry-run проходит без ошибок;
- update выполняется только после ручного подтверждения владельца проекта;
- после update app может скачать вопросы для `es/de/it`.

## Phase 5: QA Matrix

Цель: проверить не только язык, но и gameplay.

Manual tests:

- fresh install -> English -> start game;
- fresh install -> Spanish -> start game;
- fresh install -> German -> start game;
- fresh install -> Italian -> start game;
- switch `en -> es`;
- switch `es -> de`;
- switch `de -> it`;
- switch `it -> ru`;
- switch `ru -> hy`;
- check answered question preservation by `questionId`;
- complete France stage in new language;
- check certificate title/text;
- check profile certificate preview;
- check rating prompt release path stays intact.

Risk checks:

- no answered questions reset after language switch;
- no mixed-language answer options;
- no missing score/category;
- no certificate issued for incomplete stage;
- no duplicate certificate after reset/replay.

## Phase 6: Play Console Localization

Цель: чтобы новые языки помогли продвижению, а не только UI.

Tasks:

- подготовить short description для `es`, `de`, `it`;
- подготовить full description для `es`, `de`, `it`;
- подготовить release notes для `2.2.0`;
- при возможности добавить localized screenshots или хотя бы captions;
- проверить, что title остаётся понятным и не конфликтует с branding.

Acceptance criteria:

- Google Play listing содержит новые языки;
- тексты звучат естественно, без keyword stuffing;
- ключевые слова встроены аккуратно: football quiz, soccer trivia, clubs, players, tournaments.

## Release Strategy

Рекомендуемый release:

```text
2.2.0
```

Почему не `2.1.x`:

- добавляются 3 новых языка;
- меняется language picker;
- расширяется Firestore content;
- увеличивается QA scope;
- это заметная user-facing feature.

## Что не делать в этой ветке

- не трогать backend migration на PHP/MySQL;
- не менять scoring rules;
- не менять certificate business rules;
- не делать Compose/KMP migration;
- не смешивать с Play Vitals cleanup;
- не коммитить release `.aab/.apk`, `.pem`, `.DS_Store`, `.idea` runtime files.

## First Implementation Steps

1. Создать `values-es`, `values-de`, `values-it`.
2. Перевести UI strings без question arrays.
3. Обновить `dialog_language.xml`.
4. Обновить locale mapping в `StartPageFragment`.
5. Запустить compile.
6. Потом отдельно перейти к questions audit для Firestore.

## Current Next Step

Подготовить `app/questions_audit_es_de_it_2026_09_001.json` от свежего Firestore backup, затем пройти validation/dry-run перед любой заливкой.
