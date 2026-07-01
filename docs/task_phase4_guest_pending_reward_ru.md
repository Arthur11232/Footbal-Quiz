# Task: Phase 4 — Guest Pending Reward Foundation

Связанные документы:
- [android_stability_achievements_roadmap_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/android_stability_achievements_roadmap_ru.md)
- [task_phase4_achievement_foundation_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase4_achievement_foundation_ru.md)
- [implementation_plan_android_stability_to_certificates_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/implementation_plan_android_stability_to_certificates_ru.md)

## Цель

Сделать `guest pending reward` отдельным и явным domain state, чтобы completion stage у гостя не выпускал сертификат напрямую, а корректно дооформлялся после authentication.

## Почему это нужно

Сейчас в проекте achievement/certificate pipeline уже частично работает, но для guest-сценария остаётся концептуальная проблема:

- completed stage уже считается;
- reward flow уже триггерится;
- локальные сертификаты уже могут появляться;
- но guest reward пока не выделен как отдельная сущность.

Из-за этого смешиваются разные уровни модели:

- `stage completed`
- `achievement earned`
- `pending reward`
- `certificate issued`

Эту границу нужно сделать явной.

## Scope

- explicit pending reward model
- guest vs authenticated branching
- idempotency for reward issuance
- pending -> certificate conversion after auth

## Out of Scope

В эту задачу не входят:

- bell icon
- notification center
- email delivery
- новый большой UI flow
- backend redesign

## Целевое поведение

### Authenticated user

Если пользователь авторизован и завершает stage:

- completion фиксируется;
- reward считается earned;
- сертификат может выпускаться сразу;
- certificate sync в cloud продолжается как и сейчас.

### Guest user

Если пользователь гость и завершает stage:

- completion фиксируется;
- reward считается earned;
- создаётся `pending reward`;
- сертификат сразу не выпускается;
- после authentication pending reward конвертируется в сертификат.

## Domain Contract

Нужно удерживать следующие правила:

1. `stage completed` не равен `certificate issued`
2. `pending reward` существует только как промежуточное reward state
3. для одного `stageId` не должно одновременно бесконтрольно появляться:
- несколько pending reward;
- несколько issued certificates;
- pending reward поверх уже выданного certificate

## Предлагаемая модель

### PendingRewardRecord

Минимальный состав:

- `stageId`
- `earnedAtMillis`
- `recipientNameSnapshot`
- `createdAsGuest`
- `consumedAtMillis` или явный consumed/remove flow

Допустимо начать без отдельного `userId`, если pending reward локален до authentication.

## План реализации

### 1. Ввести отдельный repository для pending rewards

Создать новый слой:

- `PendingRewardRepository`
- локальная реализация на `SharedPreferences`

Минимальные методы:

- `getPendingReward(stageId)`
- `getPendingRewards()`
- `savePendingRewards(records)`
- `removePendingReward(stageId)` или `markPendingRewardConsumed(stageId)`
- `clearAllPendingRewards()`

### 2. Развести issuance logic в Play flow

В `PlayViewModel` больше не выпускать сертификаты одинаково для всех пользователей.

Нужна развилка:

- если user authenticated:
  - выпускать `IssuedCertificateRecord`
- если user guest:
  - создавать `PendingRewardRecord`

При этом completion stage остаётся единым для обеих веток.

### 3. Добавить idempotency-проверки

Перед созданием новой reward-сущности нужно проверять:

- существует ли уже `IssuedCertificateRecord` для `stageId`
- существует ли уже `PendingRewardRecord` для `stageId`

Правила:

- если certificate уже есть -> ничего не создаём
- если pending reward уже есть -> ничего не создаём
- только если нет ни одного состояния -> создаём новое

### 4. Добавить consume-flow после authentication

На устойчивой authenticated точке нужно проверять локальные pending rewards.

Подходящие точки:

- `StartPageViewModel`
- `ProfileViewModel`

Поведение:

- найти pending rewards;
- для них выпустить certificates;
- сохранить issued certificates локально;
- загрузить их в cloud;
- удалить или пометить consumed pending rewards.

### 5. Защитить Profile recovery от неправильной автовыдачи

Сейчас `ProfileViewModel` тихо довыпускает недостающие сертификаты по completed stages.

Нужно изменить поведение так, чтобы:

- для authenticated user recovery оставалась допустимой;
- для guest completed stages не превращались автоматически в issued certificates;
- pending reward логика не обходилась через profile load.

### 6. Уточнить локально-облачный контракт

На этом этапе pending rewards можно оставить только локальными.

Но нужно явно зафиксировать:

- pending reward живёт локально до auth;
- после auth конвертируется в certificate;
- cloud хранит уже issued certificates, а не guest pending state.

### 7. Подготовить переход к следующим фазам

После выполнения этой задачи foundation должна быть готова для:

- `Phase 5` congratulations polishing
- `Phase 6` certificate experience polishing
- `Phase 7` email delivery

## Файлы, которые вероятнее всего будут затронуты

- `app/src/main/java/com/arthuralexandryan/footballquiz/presentation/play/PlayViewModel.kt`
- `app/src/main/java/com/arthuralexandryan/footballquiz/presentation/profile/ProfileViewModel.kt`
- `app/src/main/java/com/arthuralexandryan/footballquiz/presentation/start/StartPageViewModel.kt`
- `app/src/main/java/com/arthuralexandryan/footballquiz/data/repository/CertificateRepository.kt`
- новые файлы `PendingRewardRepository` и его реализация
- `RepositoryModule`
- `UseCaseModule`
- при необходимости `ViewModelModule`

## Acceptance Criteria

- guest completion больше не выпускает certificate напрямую
- authenticated completion по-прежнему может выпускать certificate сразу
- для одного `stageId` pending reward создаётся только один раз
- для одного `stageId` certificate создаётся только один раз
- после authentication pending reward корректно конвертируется в certificate
- profile/start restore flow не создают дублирующие reward-сущности

## Риски

- можно случайно сломать текущий certificate preview flow;
- можно задублировать certificates при login + profile recovery;
- можно сломать cloud merge, если не удержать idempotency на `stageId`.

## Статус

`Planned`

Следующий практический шаг после согласования этого task:

1. создать `PendingRewardRepository`
2. внедрить guest/auth branching в `PlayViewModel`
3. добавить consume-flow после authentication
