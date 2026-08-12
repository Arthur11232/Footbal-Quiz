# Task: Repository Contract Audit For KMP Readiness

Статус: In progress.

Дата: 2026-08-12.

Ветка: `codex/phase1-backend-kmp-compose-readiness`.

## Цель

Проверить текущие repository contracts в Android app и понять, какие из них уже готовы к будущему backend/KMP/Compose направлению, а какие всё ещё протаскивают Firebase, Realm, Android SDK или legacy DTO выше data layer.

Главная идея: не создавать KMP-модуль сейчас, а сделать текущие Android contracts такими, чтобы позже shared domain/use cases можно было вынести без болезненного переписывания.

## Что Не Делаем

- не создаём KMP/shared Gradle module;
- не подключаем Android к backend questions/progress;
- не удаляем Realm;
- не мигрируем storage на Room;
- не переписываем UI на Compose;
- не меняем production sync rules;
- не трогаем Firestore production data.

## Контекст

Уже сделано:

- `AuthenticatedUser` убрал `FirebaseUser` из основных auth contracts;
- `CloudProgressSnapshot` убрал `UserStatsDTO` из start/profile restore contracts и `SyncRepository.SyncDecision`;
- `QuestionCatalogRepository`, `CatalogQuestion`, `ContentVersion` добавлены как future-only catalog contract;
- `./gradlew :app:compileDevDebugKotlin` проходит после sync-boundary cleanup.
- repository contract audit report создан: `docs/repository_contract_audit_kmp_readiness_ru.md`;
- первый code slice выполнен: `ProfileViewModel` больше не зависит напрямую от `UserStatsRepository`, delete cloud progress идёт через `SyncRepository.deleteCloudProgress()`.

Это значит, что следующий правильный шаг - не новый большой feature, а внимательная ревизия repository boundaries.

## Repository Scope

Проверить:

- `AuthRepository`
- `SyncRepository`
- `UserStatsRepository`
- `QuestionsRepository`
- `QuestionCatalogRepository`
- `GameProgressRepository`
- `GameCatalogRepository`
- `ProgressionRepository`
- `PlaySessionRepository`
- `CertificateRepository`
- `PendingRewardRepository`
- `NotificationRepository`
- `SettingsRepository`

## Checklist Для Каждого Repository

Для каждого contract отметить:

- протекают ли Firebase SDK classes наружу;
- протекают ли Realm entities наружу;
- протекает ли Android `Context`, `Uri`, `Bitmap`, resource id или Fragment/UI detail;
- возвращаются ли legacy DTO наружу;
- есть ли domain-friendly input/output models;
- можно ли представить backend implementation без изменения ViewModel;
- можно ли представить fake implementation для unit tests;
- должен ли contract жить в будущем shared/KMP layer или остаться Android-only.

## Предварительная Оценка

### Уже Хорошие Или Почти Готовые

- `AuthRepository` - после `AuthenticatedUser` стал backend-friendly.
- `SyncRepository` - после `CloudProgressSnapshot` стал значительно чище, но нужно отдельно проверить upload/download boundary.
- `CertificateRepository` - хороший кандидат для backend implementation.
- `PendingRewardRepository` - хороший кандидат для будущего backend-backed claim flow.
- `ProgressionRepository` - в основном domain-friendly, но local persistence пока Android-specific.

### Средний Риск

- `QuestionsRepository` - сейчас больше legacy Firestore/bootstrap sync, чем backend catalog contract.
- `QuestionCatalogRepository` - future-only, надо проверить, что model действительно не завязана на legacy Firestore shape.
- `GameProgressRepository` - важный contract, но может тянуть local storage / Realm assumptions.
- `GameCatalogRepository` - selection/scoreboard-friendly, но вероятно Android-only до полной стабилизации local model.
- `SettingsRepository` - может остаться Android-specific, потому что preferences/platform settings не обязательно тащить в KMP.

### Высокий Риск

- `PlaySessionRepository` - содержит core gameplay rules и local persistence bridge. Нужно аккуратно отделять pure game rules от Android/Realm/ad/UI assumptions.
- `NotificationRepository` - Android/Firebase-device specific; в KMP shared layer его тащить нельзя, но можно выделить domain preferences отдельно.
- `UserStatsRepository` - пока это Firebase DTO boundary. Это нормально, но ViewModel/use-case не должны зависеть от него напрямую для restore decision.

## Deliverable

Создать audit section или отдельный report с таблицей:

```text
Repository | Current boundary | KMP readiness | Backend replaceability | Risk | Next action
```

Минимальные статусы:

- `ready`
- `mostly_ready`
- `android_only`
- `needs_domain_model`
- `legacy_boundary`
- `high_risk`

## Первый Code Slice После Audit

Audit подтвердил текущую картину. Первый безопасный code slice:

1. Уменьшить прямую зависимость `ProfileViewModel` от `UserStatsRepository`, если она осталась только для delete path. Status: done.
2. Вынести delete cloud stats в `SyncRepository` или отдельный use case с domain-friendly названием. Status: done через `SyncRepository.deleteCloudProgress()`.
3. Оставить `UserStatsRepository` как data/Firebase boundary.
4. Прогнать `./gradlew :app:compileDevDebugKotlin`. Status: done.
5. Сделать отдельный commit после проверки.

Почему именно это:

- маленький и безопасный слой;
- продолжает уже начатый sync-boundary cleanup;
- не трогает gameplay;
- не меняет production behaviour;
- улучшает backend-replaceability.

## Acceptance Criteria

Task считается выполненной, когда:

- список repository contracts проверен;
- для каждого repository указан readiness/risk status;
- найден минимум один следующий безопасный code slice;
- `PROJECT_CONTEXT.md` обновлён короткой app-only заметкой;
- при code changes проходит `./gradlew :app:compileDevDebugKotlin`;
- changes закоммичены отдельным commit.

## Следующий Шаг

Сделать отдельный commit для audit + code slice.
