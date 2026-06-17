# Task: Phase 1 — Android Stability 95%+

Связанные документы:
- [android_stability_achievements_roadmap_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/android_stability_achievements_roadmap_ru.md)
- [implementation_plan_android_stability_to_certificates_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/implementation_plan_android_stability_to_certificates_ru.md)
- [question_id_localization_plan_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/question_id_localization_plan_ru.md)
- [task_question_id_localization_execution_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_question_id_localization_execution_ru.md)
- [phase1_question_id_readiness_report_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/phase1_question_id_readiness_report_ru.md)

## Цель

Довести Android-приложение до стабильного, релизно-готового состояния и подготовить базу для `2.0.3`.

## Scope

- rollout `questionId`
- безопасное обновление Firestore questions
- language switch stability
- restore/sync stability
- Google Sign-In release sanity
- release build health

## Out of scope

- achievements
- notifications
- certificates
- email delivery
- полноценный backend

## Detailed Subtasks

### 1. Завершить client-side `questionId` rollout
- проверить все question flows на использование `questionId`
- оставить fallback только там, где он нужен для переходного периода
- убедиться, что Realm schema/migration стабильно работает

### 2. Подготовить Firestore data migration
- определить финальный формат `questionId`
- проставить `questionId` в актуальном вопросном банке
- не ломать старую document structure
- подготовить importer/update pipeline

### 3. Обновить question dataset
- добавить свежие вопросы там, где это уже утверждено
- не смешивать massive content rewrite с несовместимыми schema changes
- обновить Firestore совместимо с текущими client expectations

### 4. Проверить критические пользовательские сценарии
- first launch
- guest play
- sign in
- restore cloud data
- overwrite cloud data
- смена языка
- повторный запуск после смены языка

### 5. Проверить release-ready paths
- `compileDevDebugKotlin`
- `bundleProdRelease`
- sanity test на устройстве
- проверить, что нет новых crash paths после restore/sync

### 6. Подготовить release decision
- собрать release scope для `2.0.3`
- определить, что точно входит в релиз
- определить, что переносится в следующую фазу

## Acceptance Criteria

- новая версия клиента понимает `questionId`
- Firestore questions могут содержать `questionId` без ломки старых полей
- смена языка не сбрасывает answered-state в новой схеме
- restore/sync сценарии не падают
- release bundle собирается стабильно
- есть понятное решение по release `2.0.3`

## Current Checkpoint

### Уже сделано

- client-side `questionId` rollout в основной question flow уже внедрён;
- Realm migration под `questionId` уже добавлена;
- answer persistence уже поддерживает `questionId` и legacy fallback;
- localization sync уже умеет накладывать answered-state по `questionId`;
- importer/tooling под Firestore `questionId` уже подготовлен.

### Закрыто в этом checkpoint

- добавлен защитный guard от разрушительного locale sync, если новый question dataset ещё не готов под безопасную `questionId`-миграцию;
- если у пользователя уже есть answered progress и новый язык приходит без полного `questionId` coverage, sync теперь блокируется вместо тихого перезаписывания локальной базы;
- пользователь теперь получает отдельное сообщение о том, что обновление вопросов для языка ещё не готово, а не общий network toast.
- сгенерирован отдельный readiness report по `questionId`, который показывает текущее покрытие, unresolved вопросы и зоны, где dataset ещё нельзя безопасно мигрировать автоматически.

### Проверка checkpoint

- `:app:compileDevDebugKotlin` — `BUILD SUCCESSFUL`

### Что остаётся следующим шагом

1. довести Firestore question dataset до безопасного состояния с `questionId`;
   опираясь на [phase1_question_id_readiness_report_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/phase1_question_id_readiness_report_ru.md)
2. вручную проверить сценарии:
   - смена языка
   - restore cloud data
   - overwrite cloud data
3. после этого прогнать release-ready проверки:
   - `bundleProdRelease`
   - sanity test на устройстве
4. подготовить release scope для `2.0.3`.
