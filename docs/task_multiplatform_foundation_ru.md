# Task: Multiplatform Foundation

Статус: In progress.

Дата: 2026-09-14.

Ветка: `codex/multiplatform-foundation`.

Base: `master` после production rollout ES/DE/IT localization `2.2.0 (58)`.

## Цель

Подготовить Android project к будущему переходу на:

- собственный backend;
- Kotlin Multiplatform shared domain;
- Web client;
- iOS client;
- позже Jetpack Compose для новых/изолированных экранов.

Эта фаза не создаёт KMP module и не переписывает UI на Compose. Главная цель — сделать границы внутри Android чистыми, чтобы backend/KMP не стали большим rewrite.

## Production baseline

- ES/DE/IT localization уже в production.
- Текущая production/release версия: `2.2.0`, `versionCode 58`.
- Firestore `questions`: `4134` docs, 6 языков по 689 вопросов.
- Актуальный post-update backup:
  - `app/questions_audit_backup_AFTER_es_de_it_firestore_update_2026_09_11.json`.
- Старую ветку `codex/phase1-backend-kmp-compose-readiness` используем как source of ideas, но не merge-им целиком.

## Что НЕ делаем в этой фазе

- не создаём KMP module;
- не переписываем screens на Compose;
- не удаляем Realm;
- не заменяем Firestore/backend одним большим switch;
- временно не трогаем backend-реализацию и PHP/API часть;
- не меняем production question catalog без отдельного backup/dry-run/commit flow;
- не смешиваем backend repo changes с Android repo changes;
- не ломаем release flow.

## Главный принцип

Любое изменение должно уменьшать протечки Android/Firebase/Realm/DTO из public contracts:

```text
Fragment / future Compose
    -> ViewModel
        -> UseCase
            -> Repository contract
                -> Android/Firebase/Realm/Backend implementation
```

Будущий shared/KMP слой должен содержать только:

- pure Kotlin domain models;
- use cases без Android SDK;
- repository contracts без Firebase/Realm/Context;
- business rules для progress, stages, certificates, content version.

## Phase 1A: Branch Sync And Planning

Цель: совместить полезную память старой ветки с текущим production baseline.

Tasks:

- [x] Проверить старую ветку `codex/phase1-backend-kmp-compose-readiness`.
- [x] Зафиксировать, почему её нельзя merge-ить целиком.
- [x] Обновить `docs/web_android_chat_sync_ru.md` под production `2.2.0`.
- [x] Создать review-файл:
  - `docs/multiplatform_foundation_branch_review_ru.md`.
- [x] После ревью пользователя закоммитить docs-only sync.

Acceptance criteria:

- понятно, какие старые commits полезны;
- понятно, какие старые changes опасны;
- текущая ветка остаётся на production baseline без отката ES/DE/IT.

## Phase 1B: Auth Boundary Cleanup

Цель: подготовить auth flow к backend и KMP.

Проблема:

- `FirebaseUser` не должен протекать в domain/usecase/repository contracts там, где нужен только stable user identity.
- Google Sign-In `Intent` — Android-only деталь, её нельзя тащить в будущий shared/domain слой.

Target direction:

- domain model:
  - `AuthenticatedUser`
  - `AuthSignInResult`
- Firebase mapping остаётся в data/platform layer.
- ViewModel работает с `userId`, `email`, `displayName`, `photoUrl`, а не с Firebase SDK object.

Tasks:

- [x] Найти все public contracts, где используется `FirebaseUser`.
- [x] Разделить Android sign-in launcher boundary и domain auth result.
- [x] Перенести Firebase-to-domain mapping в data mapper.
- [x] Не менять UX Google Sign-In.
- [x] Прогнать compile.

Acceptance criteria:

- Google Sign-In работает как раньше.
- Start/Profile flows не меняют user-facing поведение.
- В новых domain/usecase contracts нет прямой зависимости от Firebase SDK.

## Phase 1C: Sync Boundary Cleanup

Цель: сделать progress/cloud sync backend-ready.

Проблема:

- Firestore `UserStatsDTO` не должен быть внешней моделью для ViewModel/usecase.
- Sync rules — product/domain logic, а не Firestore DTO logic.

Target direction:

- domain model:
  - `CloudProgressSnapshot`
  - `SyncDecision`
  - later `SyncConflict`
- Firestore DTO mapping остаётся внутри data implementation.
- Certificates не должны зависеть от выбора restore/upload progress.

Tasks:

- [x] Проверить `SyncRepository` public contract.
- [x] Убрать DTO leakage из effect/state там, где ещё осталось.
- [x] Добавить delete-cloud-progress boundary через `SyncRepository`, если profile/delete flow всё ещё зависит от `UserStatsRepository` напрямую.
- [x] Проверить guest/auth/post-login/cancel restore сценарии.

Acceptance criteria:

- restore/upload UX не меняется;
- profile delete account flow не ломается;
- ViewModel не зависит напрямую от Firestore DTO.

## Phase 1D: Question Catalog Boundary

Цель: подготовить future backend content catalog без повторного хаоса вокруг question files.

Текущее состояние:

- Android production пока читает Firestore `questions`.
- Firestore docs имеют stable `questionId`.
- ES/DE/IT rollout уже production.

Target direction:

- ввести/зафиксировать future contract:
  - `QuestionCatalogRepository`;
  - `ContentVersion`;
  - language/category/type filters;
  - stable `questionId`.
- Пока не переключать Android на backend questions.
- Backend content-version будет отдельным этапом.

Tasks:

- [x] Проверить, какие question models уже pure Kotlin.
- [x] Не трогать production Firestore update flow.
- [x] Зафиксировать future backend contract отдельно от текущего legacy `QuestionsRepository`.
- [x] Подготовить migration notes для force update / minimum supported version.

Acceptance criteria:

- текущий Firestore gameplay не меняется;
- появляется понятный future API boundary для backend/Web/iOS.

### Phase 1D Migration Notes

Current production behavior:

- Android `2.2.0 (58)` still loads questions from Firestore collection `questions`.
- Local gameplay still reads Realm tables through `GameCatalogRepository` / `RealmGameCatalogLocalDataSource`.
- Stable `questionId` is already required for safe answered-question preservation across language changes.
- Firestore is currently the production source of truth for Android question content.

Future backend catalog switch rules:

- Do not switch Android runtime from Firestore to backend catalog without a force-update gate.
- Before backend catalog becomes source of truth, define a server-side `minimum_supported_version_code`.
- If the backend catalog format changes answered-state semantics, require users below the supported version to update before sync/restore.
- Backend catalog must expose a stable `content_version` and stable `question_id` for every language.
- A single logical question must keep the same `question_id` across languages.
- Answer correctness must be represented by stable answer identity or canonical answer key, not by localized text comparison.
- Old Firestore-backed Android versions must either continue receiving compatible Firestore content or be force-updated before incompatible catalog changes.

Safe rollout order:

1. Keep current Firestore flow for production Android.
2. Add backend catalog read endpoints separately and test with Web/internal clients first.
3. Add Android adapter behind a feature/config flag, disabled by default.
4. Add force-update/minimum-version check before enabling backend catalog for Android.
5. Enable backend catalog only after backup, dry-run validation, and a rollback plan.

## Phase 1E: Compose Candidate Map

Цель: не начинать Compose с самого сложного gameplay.

Candidates:

- certificate preview/share screen;
- profile certificate block;
- notification center;
- congratulations screen.

Recommended first Compose proof-of-concept:

- `Notification Center` / notification preferences details screen.

Why:

- не влияет на игровой цикл;
- не зависит от question selection/timers/ads;
- хорошо ложится на state-driven UI;
- можно держать рядом с текущим Profile notification state;
- позже пригодится для Web/iOS notification settings UX.

Second candidate:

- certificate preview/share screen.

Why:

- уже имеет относительно изолированный payload;
- визуально важный экран;
- может стать общим дизайн-ориентиром для будущих Compose screens.

Не трогать первыми:

- `PlayFragment`;
- versus gameplay screens;
- Start Page auth/sync flow.

Phase 1E rules:

- Не добавлять Compose runtime в этом slice без отдельного решения.
- Не переписывать существующие XML screens прямо сейчас.
- Сначала подготовить map, затем отдельным этапом выбрать dependency/setup strategy.
- Первый Compose proof-of-concept должен быть feature-flagged или изолирован так, чтобы release можно было собрать без риска для gameplay.

## Проверки

После каждого code slice:

```bash
./gradlew :app:compileProdDebugKotlin
```

Для release-sensitive изменений:

```bash
./gradlew :app:bundleProdRelease
./gradlew :app:testProdDebugUnitTest
```

## Progress Log

### 2026-09-14

- Добавлены domain-модели `AuthenticatedUser` и `AuthSignInResult`.
- Firebase `FirebaseUser` оставлен внутри data/platform mapping через `FirebaseAuthMapper`.
- `AuthRepository`, `SyncRepository`, `NotificationRepository`, `PlaySessionRepository` и связанные use cases переведены на domain user contract.
- `StartPageViewModel`, `ProfileViewModel`, `PlayViewModel` больше не используют `FirebaseUser.uid` напрямую.
- Начат `Sync Boundary Cleanup`: delete cloud progress вынесен из `ProfileViewModel` в `SyncRepository`.
- Manual restore в `ProfileViewModel` теперь получает cloud stats через `SyncRepository`, без прямой зависимости от `UserStatsRepository`.
- Restore dialogs в `StartPageFragment` и `ProfileFragment` больше не получают `UserStatsDTO`; вместо этого используется `CloudRestorePromptUiModel`.
- Backend/API часть намеренно отложена.
- Проверка: `./gradlew :app:compileProdDebugKotlin` прошла успешно.

### 2026-09-15

- Phase 1C app-side sync boundary cleanup закрыт после ручной проверки restore сценариев.
- Проверены guest/auth/post-login/cancel restore сценарии без изменения user-facing UX.
- Известный внешний cloud-config пункт: Firestore rules для `users/{uid}/devices/{installationId}` нужны отдельно для notification settings sync.
- Начат Phase 1D:
  - текущий question flow: `FirestoreQuestionService` загружает Firestore `questions` по `translation`, `QuestionsRepositoryImpl` мапит в `QuestionModel`, `DB_Helper` сохраняет в Realm, gameplay читает через `GameCatalogRepository` / `RealmGameCatalogLocalDataSource`;
  - добавлен future contract `QuestionCatalogRepository` с `ContentVersion`, `QuestionId`, `QuestionCatalogFilter`, `QuestionCatalogSnapshot`;
  - production Firestore update flow и gameplay runtime не переключались.
- Phase 1D contract-only foundation закрыт:
  - migration notes для force update / minimum supported version добавлены;
  - backend catalog switch оставлен на будущий отдельный этап.
- Начат Phase 1E:
  - первым Compose proof-of-concept выбран notification center / notification preferences details screen;
  - certificate preview/share screen выбран вторым кандидатом;
  - gameplay, versus и Start Page auth/sync исключены из первых Compose переписываний.

## Текущий next step

Подготовить Compose setup decision отдельно от gameplay.

Перед следующим изменением кода:

1. проверить текущие Gradle/AGP/Kotlin версии и Compose compatibility;
2. решить, добавляем ли Compose dependencies сейчас или откладываем до отдельной ветки;
3. если добавляем, начать с изолированного notification screen, а не с gameplay.
