# Backend / KMP / Compose Readiness Audit

Статус: Initial audit.

Ветка: `codex/phase1-backend-kmp-compose-readiness`.

Цель документа: зафиксировать текущие architectural boundary points перед тем, как менять код. Это не список срочных багов и не требование переписать всё сразу. Это карта, чтобы дальше двигаться маленькими безопасными шагами.

## Executive Summary

В проекте уже есть хорошая база для постепенного перехода к backend/KMP/Compose:

- есть Koin DI;
- появились repository interfaces;
- gameplay/progression/certificate state частично вынесены из fragments;
- есть pure Kotlin domain-модели для progression/rewards/certificates;
- сертификаты и pending rewards уже выглядят как хорошие кандидаты для будущего local/domain слоя;
- certificates flow уже отделён достаточно хорошо, чтобы позже подключить backend без большого UI rewrite.

Главная проблема текущей границы: часть repository/usecase contracts всё ещё тянет Android/Firebase/presentation-типы выше data layer.

Самые важные leakage points:

- `FirebaseUser` используется в repository interfaces и domain use cases;
- `Intent`, `Uri`, `Context` встречаются в repository/domain contracts;
- некоторые domain use cases возвращают или принимают presentation-модели;
- repository interfaces находятся в `data.repository`, но используются как domain contracts;
- Realm остаётся главным local source для progress/questions и содержит много legacy category-specific write/read paths;
- Firestore question sync пока живёт за `QuestionsRepository`, но contract слишком общий для будущего backend catalog.

## Current Package Shape

Текущие ключевые зоны:

```text
data/source/local
data/repository
data/repository/impl
domain/progression
domain/certificate
domain/reward
domain/usecase
presentation/start
presentation/play
presentation/profile
presentation/achievement
```

Позитивный момент: новые feature-зоны уже разделены лучше, чем legacy fragments. Особенно это видно вокруг progression/certificate/reward.

## Хорошие KMP-Friendly Кандидаты

Эти модели уже близки к pure Kotlin и хорошо подходят для будущего shared/domain:

- `domain/progression/CompletedStageRecord`
- `domain/progression/ProgressionStageStatus`
- `domain/progression/ProgressionSnapshot`
- `domain/progression/ProgressionStageId`
- `domain/certificate/IssuedCertificateRecord`
- `domain/reward/PendingRewardRecord`

Почему они хорошие:

- не зависят от Android SDK;
- не зависят от Firebase;
- не зависят от Realm;
- используют стабильные identifiers (`stageId`, `certificateId`);
- могут быть легко перенесены в будущий KMP shared module.

Риск: `ProgressionStageId` сейчас enum. Для Android это нормально, но для backend/content-driven stages позже может понадобиться более гибкая string/value модель. Пока менять не нужно, но держать в голове.

## Repository Contract Audit

### `AuthRepository`

Проблемы:

- возвращает `FirebaseUser`;
- возвращает Android `Intent`;
- принимает `FirebaseUser` в `deleteUser`;
- возвращает `AuthManager.AuthResult`, то есть наружу протекает auth implementation detail.

Текущее состояние:

```text
Не backend/KMP-ready.
```

Рекомендуемое направление:

- ввести domain-модель `AuthenticatedUser`;
- ввести presentation/platform wrapper для Google Sign-In intent отдельно от domain auth contract;
- оставить Firebase implementation внутри data/platform layer;
- ViewModel должен знать userId/email/displayName, а не `FirebaseUser`.

### `SyncRepository`

Проблемы:

- принимает `FirebaseUser`;
- `SyncDecision.UploadLocal` хранит `FirebaseUser`;
- использует `UserStatsDTO` из legacy models;
- sync rules уже продуктовые, но contract всё ещё Firebase/DTO-specific.

Текущее состояние:

```text
Частично готов, но требует boundary cleanup перед backend switch.
```

Рекомендуемое направление:

- заменить `FirebaseUser` на `AuthenticatedUser` или `UserSession`;
- заменить `UserStatsDTO` на domain `CloudProgressSnapshot` / `SyncProgressSnapshot`;
- оставить mapping `Firestore DTO -> domain` внутри data layer;
- позже добавить `BackendSyncRepositoryImpl` без изменения ViewModel/usecase.

### `CertificateRepository`

Сильные стороны:

- уже использует `IssuedCertificateRecord`;
- cloud methods принимают `userId`, `recipientName`, `userEmail`;
- не протаскивает `FirebaseUser`;
- contract достаточно близок к backend-compatible.

Текущее состояние:

```text
Один из лучших кандидатов для backend implementation.
```

Рекомендуемое направление:

- сохранить текущий contract как основу;
- позже разделить local/cloud concerns, если methods начнут разрастаться;
- добавить backend implementation после готовности PHP API.

### `GameProgressRepository`

Проблемы:

- `saveProfilePhoto(userId, uri: Uri)` протаскивает Android `Uri`;
- `ProfileProgressSnapshot` находится в data repository package, хотя по смыслу ближе к domain/profile progress read model;
- profile photo storage смешан с game progress.

Текущее состояние:

```text
Частично готов, но смешивает progress и Android file/profile-photo concerns.
```

Рекомендуемое направление:

- вынести `ProfileProgressSnapshot` в domain/read-model слой;
- profile photo вынести в отдельный `ProfilePhotoRepository`;
- заменить Android `Uri` на platform-specific input model на boundary ниже ViewModel.

### `NotificationRepository`

Проблемы:

- `syncSignedInUser(user: FirebaseUser)`;
- implementation зависит от Android permission APIs, Firebase Messaging и Context;
- repository contract смешивает remote device sync, local settings и permission checks.

Текущее состояние:

```text
Android-specific. Для KMP/backend-ready слоя требует разделения.
```

Рекомендуемое направление:

- domain contract должен работать с `userId`;
- permission checks оставить platform implementation;
- device registration выделить отдельно;
- notification preferences можно позже хранить в backend.

### `QuestionsRepository`

Сильные стороны:

- contract очень маленький;
- Firestore implementation уже спрятан за repository.

Проблемы:

- contract описывает только localization sync, а не canonical question catalog;
- не содержит `content_version`;
- не описывает read-only получение вопросов из backend;
- пока завязан на destructive/local sync decisions.

Текущее состояние:

```text
Хороший wrapper для текущей Firestore sync, но недостаточный для backend catalog.
```

Рекомендуемое направление:

- не ломать текущий `QuestionsRepository` сразу;
- добавить будущий contract отдельно: `QuestionCatalogRepository`;
- в нём использовать `content_version`, language, category/type, questionId;
- текущий Firestore sync оставить legacy path до force update/backend switch.

## Domain Use Case Audit

### Хорошее

Use cases уже начали отделять business flow от fragments:

- start sync decision;
- restore/upload use cases;
- progression refresh;
- pending rewards consume;
- issued certificates cloud sync;
- notification session flow.

Это правильное направление.

### Leakage Points

Примеры проблем:

- `BuildStartPageStateUseCase` принимает `FirebaseUser?` и возвращает `StartPageUiState`;
- `SyncNotificationSessionUseCase` принимает `FirebaseUser`;
- `ShowRewardReadyNotificationUseCase` зависит от Android `Context`, `R.string`, `showNotification` и presentation payload;
- start use cases местами используют `FirebaseUser` как основной user object.

Почему это важно:

- такой use case сложно перенести в KMP;
- backend switch потребует менять больше ViewModel/usecase кода;
- domain слой становится зависимым от Android/Firebase/presentation.

Рекомендуемое направление:

- domain use cases должны принимать domain input models;
- presentation mapping должен жить во ViewModel или mapper;
- Android notification rendering должен быть platform-specific service;
- Firebase user должен быть замаплен в `AuthenticatedUser` максимально рано.

## Local Data Source Audit

### Realm

Realm сейчас остаётся production local source и не должен удаляться в Phase 1.

Главные Realm-зоны:

- `RealmGameStateLocalDataSource`;
- `RealmGameCatalogLocalDataSource`;
- `RealmPlayProgressLocalDataSource`.

Проблемы:

- много category-specific logic;
- many write paths напрямую знают Realm entities;
- reset/restore/answered-state всё ещё сильно связано с legacy DB classes;
- Realm query/write logic трудно будет переиспользовать в KMP;
- миграция на Room должна идти маленькими отдельными кусками.

Рекомендуемое направление:

- не переписывать Realm целиком;
- сначала удерживать repository contracts;
- первыми Room-кандидатами оставить issued certificates, pending rewards, progression snapshot;
- questions/answered state переносить только после стабилизации `questionId` и backend catalog plan.

## Presentation Audit

### Start

`StartPageViewModel` уже использует use cases, но всё ещё работает с `FirebaseUser`.

Рекомендуемое направление:

- сначала ввести `AuthenticatedUser`;
- затем ViewModel будет читать userId/email/displayName из domain model;
- Google/Firebase-specific details оставить в auth implementation.

### Play

`PlayViewModel` уже держит много игрового state и achievement logic. Это нормально для текущего этапа, но для будущего Compose/KMP нужно постепенно выделять:

- answer evaluation;
- stage completion detection;
- reward/certificate issuing rules;
- progress snapshot calculation.

Не трогать первым. Это самый рискованный экран.

### Profile

Profile уже хорошая зона для постепенной полировки:

- profile certificates block;
- certificate preview;
- restore/upload UX;
- profile progress read model.

Кандидат для будущей Compose migration после стабилизации contracts.

### Achievement / Certificate

Certificate preview/share flow выглядит лучшим первым UI-кандидатом для Compose:

- изолированный экран;
- сильная визуальная ценность;
- меньше legacy gameplay dependency;
- уже есть payload/read model.

## Backend Readiness Priority

### Priority 1: Auth Boundary

Цель:

- убрать `FirebaseUser` из repository/usecase contracts;
- ввести `AuthenticatedUser`.

Статус:

- выполнен первый code slice;
- добавлены `domain/auth/AuthenticatedUser` и `domain/auth/AuthSignInResult`;
- добавлен mapper `FirebaseUser -> AuthenticatedUser` на data boundary;
- `AuthRepository`, `SyncRepository`, `NotificationRepository`, `PlaySessionRepository`, start/play/profile use cases и ViewModel paths переведены на `AuthenticatedUser`;
- `FirebaseUser` остался только внутри `AuthManager` и Firebase mapper;
- проверено: `./gradlew :app:compileDevDebugKotlin` проходит успешно.

Почему первый:

- backend будет использовать Firebase ID Token, но app не должен быть навсегда завязан на Firebase SDK objects;
- это откроет путь к PHP backend token verification без ломки presentation.

### Priority 2: Sync Boundary

Цель:

- заменить `UserStatsDTO` в sync contract на domain sync model;
- явно описать local/cloud progress decision.

Почему второй:

- sync самый рискованный продуктовый сценарий;
- сертификаты не должны зависеть от restore/upload decision;
- backend switch будет идти именно через sync contracts.

### Priority 3: Question Catalog Boundary

Цель:

- добавить future-ready `QuestionCatalogRepository` contract;
- поддержать `content_version`;
- не ломать текущий Firestore path.

Почему третий:

- questions catalog станет backend-owned;
- старые версии приложения нельзя ломать;
- нужен force update перед настоящим switch.

### Priority 4: Local Storage Direction

Цель:

- определить первые Room-кандидаты;
- не делать big-bang Realm migration.

Почему четвертый:

- Realm работает сейчас;
- риск migration высокий;
- лучше двигаться от маленьких независимых сущностей.

## Suggested First Code Tasks

Порядок маленьких безопасных шагов:

1. Создать `AuthenticatedUser` domain model. Status: done.
2. Добавить mapper `FirebaseUser -> AuthenticatedUser` внутри auth/data layer. Status: done.
3. Постепенно заменить `FirebaseUser` в `SyncRepository`, `NotificationRepository`, start/usecase/ViewModel paths. Status: first slice done.
4. Вынести `ProfileProgressSnapshot` ближе к domain/read-model package. Status: done.
5. Ввести черновой `QuestionCatalogRepository` contract без подключения backend. Status: done.
6. Зафиксировать `ContentVersion` domain model. Status: done.
7. Только после этого создавать `Football-Quiz-Backend` repo skeleton.

## Что Не Делаем Сейчас

- не удаляем Realm;
- не переписываем PlayFragment/PlayViewModel;
- не подключаем PHP backend к Android;
- не переключаем questions с Firestore на backend;
- не делаем Compose rewrite;
- не меняем production data shape без отдельной migration task.

## Initial Conclusion

Проект уже в хорошем направлении: у нас не хаос, а живой legacy+new architecture mix. Самый умный ход сейчас — не начинать backend с API endpoints, а сначала стабилизировать Android contracts вокруг auth/sync/questions. Тогда backend, KMP и Compose станут не революцией, а нормальной эволюцией.
