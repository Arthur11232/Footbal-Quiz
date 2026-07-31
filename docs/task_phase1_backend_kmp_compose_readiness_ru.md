# Task: Phase 1 — Android Readiness For Backend, KMP, And Compose

Статус: Draft for review.

Этот документ фиксирует только подготовительную Android-фазу. Реальный backend, KMP-модуль и Compose-переписывание в эту фазу не входят.

## Цель

Подготовить текущий Android-проект `Football Quiz` к постепенному переходу на:

- собственный backend;
- Kotlin Multiplatform shared domain;
- Jetpack Compose UI;
- будущие iOS и Web клиенты.

Главная идея: сначала сделать границы внутри Android понятными и устойчивыми, чтобы следующий шаг не стал большим болезненным переписыванием.

## Что НЕ делаем в Phase 1

- не заменяем Firebase собственным backend;
- не удаляем Realm;
- не переписываем экраны на Compose;
- не создаём KMP-модуль;
- не меняем production data model без отдельного плана миграции;
- не ломаем текущий release flow;
- не начинаем iOS/Web разработку.

## Зафиксированные решения на текущий момент

- Первый собственный backend делаем прагматично: `PHP + MySQL` на текущем hosting/cPanel, без дорогого VPS на старте.
- Firebase Auth сохраняем как основной identity provider на первом backend этапе.
- Backend не хранит пароли пользователей и не реализует свой login/password flow на старте.
- Android продолжает использовать Firebase Auth / Google Sign-In и передаёт backend-у Firebase ID Token.
- Backend проверяет Firebase ID Token и использует Firebase `uid` как стабильный user identifier.
- Backend должен хранить canonical-ready question catalog с первого этапа, но Android migration на backend questions делается отдельно и постепенно.
- Backend API делаем отдельным repository: `Football-Quiz-Backend`.
- Текущий web repo остаётся landing/content site для `footballquiz.club`.
- Admin/content panel обязательна для управления questions catalog и сайтом, но делается после backend MVP.
- Questions catalog получает отдельную `content_version`, независимую от Android/iOS/Web app version.
- Перед настоящим backend switch нужен force update / minimum supported version.
- Целевой второй клиент после Android — iOS, но практический второй клиент до покупки Apple Developer Account — Web.
- Web используется как low-cost validation client для backend, question catalog, progress, certificates и admin/content workflows.

## Главный принцип

Phase 1 должна улучшить архитектурную готовность, но не остановить развитие продукта.

Любая правка должна отвечать хотя бы одной цели:

- уменьшить зависимость presentation/domain слоёв от Firebase, Realm, Android `Context`;
- сделать модели и use cases ближе к pure Kotlin;
- подготовить интерфейсы так, чтобы Firebase-реализацию позже можно было заменить backend-реализацией;
- упростить будущий перенос UI на Compose;
- улучшить тестируемость.

## Target Architecture Direction

Желаемое направление слоёв:

```text
UI Fragment / future Compose
    -> ViewModel
        -> UseCase
            -> Repository interface
                -> Firebase / Realm / Preferences / future Backend implementation
```

Будущий KMP слой должен находиться примерно на уровне:

```text
shared domain models
shared use cases
shared repository contracts
platform-specific repository implementations
```

## Phase 1 Scope

### 1. Domain Model Readiness

Зафиксировать и постепенно привести к backend/KMP-friendly виду ключевые модели:

- `Question`
- `Answer`
- `QuestionId`
- `StageId`
- `ProgressSnapshot`
- `CompletedStage`
- `IssuedCertificate`
- `PendingReward`
- `UserProfile`
- `NotificationPreference`

Критерии:

- модели не должны напрямую зависеть от Android SDK;
- модели не должны напрямую зависеть от Firebase DTO;
- модели не должны напрямую зависеть от Realm entities;
- `questionId`, `stageId`, `certificateId` должны оставаться стабильными string identifiers.

### 2. Repository Contract Readiness

Проверить текущие repository interfaces и привести их к формату, который можно будет реализовать не только Firebase/Realm, но и backend-клиентом.

Кандидаты:

- `QuestionsRepository`
- `GameProgressRepository`
- `ProgressionRepository`
- `CertificateRepository`
- `SyncRepository`
- `SettingsRepository`
- `NotificationRepository`

Что проверить:

- не протекают ли Firebase classes наружу;
- не протекают ли Realm classes наружу;
- нет ли Android `Context` в domain/use-case contracts;
- можно ли представить альтернативную реализацию `BackendQuestionsRepository`;
- можно ли представить альтернативную реализацию `BackendCertificateRepository`;
- можно ли протестировать use case без Firebase и Realm.

### 3. DTO Boundary Cleanup

DTO должны жить на границе data layer, а не в domain/presentation.

Текущие DTO-кандидаты:

- `UserStatsDTO`
- `UserCertificateDTO`
- `UserNotificationDeviceDTO`
- Firestore question document shape

Цель:

- domain layer работает с domain-моделями;
- data layer мапит Firebase/Firestore DTO в domain-модели;
- будущий backend сможет иметь свои DTO без переписывания ViewModel/use-case.

### 4. Sync Contract Stabilization

Синхронизация сейчас завязана на Firebase/Firestore, но правила sync должны быть описаны как продуктовый/domain contract.

Нужно зафиксировать:

- guest progress остаётся локальным до auth;
- auth user имеет cloud progress;
- restore не должен автоматически уничтожать локальный прогресс без понятного user decision;
- certificates не должны зависеть от restore/upload решения по progress;
- already issued certificate не выпускается повторно;
- `questionId` является основной стабильной единицей answered progress;
- старые app versions не должны ломаться от нового question dataset.

### 4.1. Local Storage Direction: Realm -> Room

Текущий Realm не удаляется в Phase 1.

Но стратегическое направление для Android local storage:

- новые local-storage решения проектировать в сторону `Room`;
- Realm считать legacy local database до отдельной миграции;
- не переносить весь Realm одним большим rewrite;
- сначала удерживать чистые repository contracts;
- затем переносить отдельные read/write paths в Room маленькими шагами;
- в будущем иметь возможность заменить local source без изменения ViewModel/use-case.

Почему Room предпочтительнее для следующего этапа:

- лучше подходит под Android-first стабильность;
- миграции более явные и контролируемые;
- хорошо работает с Coroutines/Flow;
- лучше ложится на Compose;
- легче тестируется;
- проще удерживать data/domain границу.

### 5. Compose Migration Map

Compose в Phase 1 не внедряется, но надо подготовить карту будущей миграции.

Первые кандидаты на Compose:

- certificate preview/share screen;
- profile certificates block;
- notification center screen;
- congratulations screen.

Почему эти экраны:

- они относительно изолированы;
- меньше legacy animation/state complexity;
- легче построить preview;
- меньше риск сломать gameplay.

Экраны, которые НЕ трогаем первыми:

- `PlayFragment`;
- versus gameplay screens;
- start page auth/sync flow.

### 6. Backend Contract Notes

В Phase 1 можно описать будущие backend endpoints, но нельзя начинать реализацию backend.

Первый backend target:

- `PHP 8.x`;
- `MySQL`;
- deployment через текущий hosting/cPanel;
- endpoint namespace: `/api/...` или отдельный subdomain `api.footballquiz.club`, если hosting/DNS позволяют;
- Firebase Auth остаётся identity provider.

Repository decision:

- Android app repo остаётся отдельным;
- Web landing repo остаётся отдельным;
- Backend/API/Admin делаем отдельным repository: `Football-Quiz-Backend`;
- backend repo должен содержать PHP API, MySQL migrations/schema, Firebase token verification и будущий admin/content panel;
- чувствительные файлы (`.env`, DB credentials, Firebase service account) не должны попадать в git и не должны лежать в публичном document root.

Рекомендуемая backend repo структура:

```text
Football-Quiz-Backend
├── public/
│   └── index.php
├── src/
│   ├── Auth/
│   ├── Controllers/
│   ├── Database/
│   ├── Middleware/
│   ├── Questions/
│   ├── Progress/
│   └── Certificates/
├── admin/
├── config/
├── database/
├── composer.json
├── .env.example
└── README.md
```

Firebase Auth verification direction:

- Android получает Firebase ID Token после Firebase/Google Sign-In;
- Android отправляет token в backend через `Authorization: Bearer <firebase_id_token>`;
- PHP backend проверяет token через Composer dependency, предпочтительно `kreait/firebase-php`;
- backend использует Firebase `uid` как стабильный user identifier;
- backend не хранит user password и не делает свой password login на первом этапе;
- Firebase service account JSON хранится вне `public_html`, например в приватной директории hosting account.

Предварительные backend domains:

- auth/session mapping;
- question catalog;
- user progress;
- achievements/stages;
- certificates;
- email delivery;
- notification device registration.

Canonical-ready question catalog должен быть заложен в backend с первого этапа.

Это означает:

- MySQL хранит вопросы как будущий главный источник;
- Firestore может временно оставаться production source для Android;
- Android switch на backend questions делается отдельной migration phase;
- backend catalog должен поддерживать `questionId`, language, category/type, answers, right answer и `content_version`.

`content_version` — это версия каталога вопросов, независимая от версии приложения.

Минимальный формат:

```json
{
  "content_version": "2026.08.001",
  "min_app_version_code": 55,
  "is_active": true,
  "published_at": "2026-08-01T00:00:00Z"
}
```

Правило:

- app version отвечает за код клиента;
- `content_version` отвечает за набор вопросов;
- клиент должен брать активную `content_version`, совместимую с текущей app version;
- backend должен иметь возможность отклонить слишком старую app version через minimum supported version / force update policy;
- старые content versions желательно хранить хотя бы временно для диагностики и безопасной миграции.

Admin/content panel:

- нужна обязательно, но не входит в первый backend MVP;
- должна быть закрыта авторизацией и whitelist по Firebase UID/email;
- не должна быть просто скрытой ссылкой;
- должна уметь добавлять, редактировать, отключать и валидировать вопросы;
- должна поддерживать EN/RU/HY переводы;
- должна проверять дубликаты `question_id`;
- должна управлять `content_version`;
- позже может управлять сайтом, новостями, feature flags, policy/terms content.

Пример будущего направления:

```text
GET /questions?language=en&version=...
GET /users/{uid}/progress
PUT /users/{uid}/progress
GET /users/{uid}/certificates
POST /users/{uid}/certificates/{stageId}/issue
POST /certificates/{certificateId}/email
```

Пример будущей минимальной MySQL-схемы для question catalog:

```text
questions
- id
- question_id
- category_type
- place_type
- language
- question
- answer_a
- answer_b
- answer_c
- answer_d
- right_answer
- content_version
- is_active
- created_at
- updated_at
```

Auth direction для backend:

```text
Android Google Sign-In
    -> Firebase Auth
    -> Firebase ID Token
    -> PHP backend verifies token
    -> backend resolves firebase_uid/email/display_name
    -> MySQL user row
```

Важно: это только черновой контракт, не implementation plan для backend.

## Detailed Tasks

### Task 1. Architecture Inventory

- пройти по `data/domain/presentation` пакетам;
- найти места, где Firebase/Realm/Android classes протекают выше data layer;
- составить короткий список проблемных точек;
- не чинить всё сразу.

Output:

- создан initial audit: `docs/backend_kmp_compose_readiness_audit_ru.md`.

Статус:

- initial audit started;
- главные leakage points зафиксированы: `FirebaseUser`, `Context`, `Intent`, `Uri`, presentation models в domain/usecase boundary;
- первые рекомендуемые code tasks: `AuthenticatedUser`, sync boundary cleanup, future `QuestionCatalogRepository`, `ContentVersion`.
- первый code slice выполнен: `AuthenticatedUser` введён, `FirebaseUser` убран из основных repository/usecase/ViewModel contracts, Kotlin compile проходит.
- `ProfileProgressSnapshot` перенесён в domain/progression.
- Добавлен future-only questions catalog contract: `QuestionCatalogRepository`, `CatalogQuestion`, `ContentVersion`.

### Task 2. Domain Model Checklist

- перечислить текущие domain-модели;
- отметить, какие из них уже KMP-friendly;
- отметить, какие нужно очистить от Android/Firebase/Realm деталей.

### Task 3. Repository Contract Checklist

- проверить каждый repository interface;
- отметить, можно ли сделать backend implementation без изменения ViewModel;
- отметить, где нужен mapper или новый domain model.

### Task 4. Sync Rules Document

- зафиксировать sync rules отдельно от Firebase implementation;
- описать guest/auth/restore/certificate behavior;
- использовать как основу для будущего backend.

### Task 5. Compose Candidate Map

- выбрать 2-4 экрана-кандидата;
- описать причину выбора;
- указать риски;
- не писать Compose код на этой phase.

### Task 6. Backend Boundary Prep

- выписать будущие backend domains;
- не утверждать API как финальный;
- отметить, какие current repositories будут первыми кандидатами на backend implementation.
- зафиксировать отдельный backend repository как будущую точку разработки API/Admin.
- зафиксировать Firebase ID Token verification через PHP/Composer/Kreait как first-stage auth bridge.

### Task 7. Local Storage Direction Prep

- зафиксировать Realm как legacy storage;
- выбрать первые Room-кандидаты;
- не начинать миграцию до audit;
- оценить, какие сущности проще перенести первыми.

Первые возможные кандидаты:

- issued certificates;
- pending rewards;
- progression snapshot;
- question metadata;
- answered state после стабилизации `questionId`.

## Acceptance Criteria

Phase 1 считается готовой, когда:

- есть audit текущих architectural boundaries;
- есть список KMP-ready и non-KMP-ready моделей;
- repository contracts проверены на backend-replaceability;
- sync rules описаны как domain/product contract;
- Compose migration candidates выбраны;
- Realm -> Room direction зафиксирован без немедленной миграции;
- backend first target зафиксирован как PHP + MySQL;
- Firebase Auth сохранён как first-stage identity provider;
- backend repository decision зафиксирован как отдельный repo;
- admin/content panel зафиксирована как обязательная post-MVP часть;
- минимальный `content_version` contract зафиксирован;
- нет production-breaking code changes;
- Android release/debug build продолжает собираться.

## Suggested First Implementation Order

1. Создать audit current boundaries.
2. Зафиксировать domain model checklist.
3. Проверить repository contracts.
4. Описать sync rules как backend-neutral contract.
5. Подготовить Compose migration map.
6. Зафиксировать Realm -> Room migration direction.
7. Только после этого решать, что из кода реально менять.

## Решённые вопросы

- Backend target для первого этапа: `PHP + MySQL` на текущем hosting/cPanel.
- Firebase Auth сохраняется как identity provider; свой login/password пока не делаем.
- PHP backend будет проверять Firebase ID Token через Composer dependency, предпочтительно `kreait/firebase-php`.
- Backend хранит canonical-ready question catalog, но Android switch на backend questions откладывается в отдельную migration phase.
- Backend API/Admin делаем отдельным repository: `Football-Quiz-Backend`.
- Admin/content panel нужна обязательно, но после backend MVP.
- Минимальный `content_version` contract: `content_version`, `min_app_version_code`, `is_active`, `published_at`.
- Перед настоящим backend switch нужен force update / minimum supported version.
- Второй стратегический клиент: iOS.
- Второй практический клиент до покупки Apple Developer Account: Web.

## Открытые вопросы

- Дождаться DNS propagation для `api.footballquiz.club` и проверить HTTPS/AutoSSL.
- Выбрать exact first backend MVP endpoints.
- Выбрать first MySQL schema format для users/progress/certificates/questions.
- Решить, когда создавать `Football-Quiz-Backend` repo: до Android audit или после него.

## Current Recommendation

Начать Phase 1 не с кода, а с audit-документа.

Причина: проект уже живой и опубликованный, поэтому архитектурные изменения должны идти маленькими точными шагами, а не большим переписыванием.
