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
- [ ] Проверить guest/auth/post-login/cancel restore сценарии.

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

- [ ] Проверить, какие question models уже pure Kotlin.
- [ ] Не трогать production Firestore update flow.
- [ ] Зафиксировать future backend contract отдельно от текущего legacy `QuestionsRepository`.
- [ ] Подготовить migration notes для force update / minimum supported version.

Acceptance criteria:

- текущий Firestore gameplay не меняется;
- появляется понятный future API boundary для backend/Web/iOS.

## Phase 1E: Compose Candidate Map

Цель: не начинать Compose с самого сложного gameplay.

Первые candidates:

- certificate preview/share screen;
- profile certificate block;
- notification center;
- congratulations screen.

Не трогать первыми:

- `PlayFragment`;
- versus gameplay screens;
- Start Page auth/sync flow.

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

## Текущий next step

Продолжить Phase 1C: `Sync Boundary Cleanup`.

Перед следующим изменением кода:

1. вручную проверить guest/auth/post-login/cancel restore сценарии;
2. при успешной проверке закрыть Phase 1C как app-side sync boundary cleanup;
3. перейти к Phase 1D question catalog boundary.
