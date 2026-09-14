# Multiplatform Foundation Branch Review

Дата: 2026-09-14.

Текущая ветка: `codex/multiplatform-foundation`.

Старая ветка для анализа: `codex/phase1-backend-kmp-compose-readiness`.

## Короткий вывод

Старую ветку нельзя целиком merge-ить в текущую ветку.

Причина: она была создана до ES/DE/IT production rollout и содержит устаревшие изменения вокруг question-audit/catalog файлов. Прямой merge или cherry-pick всех commits может вернуть старые файлы вопросов, удалить ES/DE/IT translation artifacts или смешать pre-production question tooling с уже опубликованным production состоянием.

Правильная стратегия: забрать из старой ветки только архитектурную память и безопасные code slices, а не весь diff.

## Что полезного есть в старой ветке

Commits старой ветки:

- `84f065e Prepare auth boundary for backend readiness`
- `86f4329 Add web Android chat sync context`
- `f6f5a33 Clean up cloud progress sync boundary`
- `08a8822 Update project sync documentation`
- `21cc29c Audit repository contracts for KMP readiness`
- `8b65a2b Polish gameplay state and review prompts`
- `9956298 Refresh question catalog audit tooling`

Полезные темы:

- `AuthenticatedUser` вместо прямого протаскивания `FirebaseUser`.
- `CloudProgressSnapshot` вместо внешнего использования `UserStatsDTO`.
- `QuestionCatalogRepository` как будущий contract для backend/content catalog.
- Repository contract audit для KMP/backend readiness.
- Backend direction: PHP + MySQL, Firebase Auth как identity provider, отдельный backend repo.
- Compose migration map: начинать не с gameplay, а с изолированных экранов certificate/profile/notifications/congratulations.

## Что НЕ переносить автоматически

- Массовые changes вокруг `questions_audit*`, `questions_structured*`, `questions_alignment_review.json`.
- Удаления ES/DE/IT localization artifacts.
- Старые docs/question answer reference deletes.
- Любые изменения, которые считают `questions_audit.json` актуальным production source.
- Большой commit `9956298` целиком, потому что он завязан на pre-ES/DE/IT question tooling.

## Что можно перенести безопасно

В первую очередь переносить документы/идеи:

- phase plan для backend/KMP/Compose readiness;
- repository contract audit;
- sync/backend context;
- список безопасных code slices.

Код переносить только отдельными задачами после проверки на текущем production baseline `2.2.0 (58)`.

## Текущее состояние после ES/DE/IT production

- `master` содержит merge commit `3a652ba Merge ES DE IT localization release`.
- ES/DE/IT localization уже production.
- Firestore `questions` содержит 6 языков по 689 вопросов: `4134` docs.
- Актуальный post-update backup:
  - `app/questions_audit_backup_AFTER_es_de_it_firestore_update_2026_09_11.json`.
- Текущая рабочая ветка для следующего этапа:
  - `codex/multiplatform-foundation`.

## Где мы находимся по Multiplatform

Мы ещё не на этапе создания KMP module.

Сейчас правильный этап:

```text
Android architecture boundary cleanup
    -> backend-ready contracts
    -> content version contract
    -> shared-domain candidates
    -> позже KMP module
```

То есть ближайшая работа должна быть Android-side, но с правильными границами под будущий backend/KMP/Web/iOS.

## Рекомендуемый порядок

1. Зафиксировать актуальный Phase 1 plan в `docs/task_multiplatform_foundation_ru.md`.
2. Перенести идеи `AuthenticatedUser` / `CloudProgressSnapshot` как отдельные маленькие PR/commit slices.
3. Проверить, какие из этих code slices уже безопасно применимы после localization production.
4. Не трогать Firestore questions и ES/DE/IT файлы в рамках architecture cleanup.
5. После каждого code slice запускать минимум:
   - `./gradlew :app:compileProdDebugKotlin`
   - при release-sensitive изменениях `./gradlew :app:bundleProdRelease`.

## Первый безопасный code slice

Самый логичный первый code slice:

- убрать прямое протаскивание `FirebaseUser` из repository/usecase boundary там, где это можно сделать без UI rewrite;
- ввести/использовать domain auth model уровня `AuthenticatedUser`;
- оставить Google Sign-In `Intent` как Android-only boundary, не тащить его в будущий shared/domain layer.

Почему это первый шаг:

- не затрагивает Firestore question catalog;
- не затрагивает ES/DE/IT production data;
- готовит backend Auth header flow;
- упрощает будущую проверку Firebase ID Token на backend.
