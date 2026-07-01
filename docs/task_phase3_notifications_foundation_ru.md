# Task: Phase 3 — Notifications Foundation

Связанные документы:
- [android_stability_achievements_roadmap_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/android_stability_achievements_roadmap_ru.md)
- [implementation_plan_android_stability_to_certificates_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/implementation_plan_android_stability_to_certificates_ru.md)

## Цель

Подготовить notifications как стабильный канал возврата и оповещения.

## Scope

- permission flow
- FCM token lifecycle
- auth-only delivery strategy
- notification preference basics

Важное уточнение по границам текущей задачи:

- этот task покрывает только `Phase 3A` technical foundation;
- `Phase 3B` product/UI layer сознательно не входит в текущий цикл;
- bell icon, notification center, history/read-model и отдельный notification UX откладываются до завершения остальных core phase-ов roadmap.

## Detailed Subtasks

### 1. Permission strategy
- определить, когда спрашивать permission
- не ломать early UX

### 2. Token lifecycle
- получить token
- привязать token к authenticated user
- очистить/обновить token при logout/login

### 3. Notification preferences
- global on/off
- задел под progress/reward notifications

### 4. First scenarios
- reminder to continue progress
- reward-ready notification
- update/new stage notification

## Acceptance Criteria

- permission flow не раздражает пользователя
- token корректно привязывается к user lifecycle
- можно безопасно отправить notification зарегистрированному пользователю

## Status

`Phase 3A` — завершена.

`Phase 3B` — отложена до завершения остальных core phase-ов roadmap.

### Что уже внедрено

- подключён `firebase-messaging`;
- добавлен `FirebaseMessagingService`;
- реализован `FCM token lifecycle` с привязкой к `users/{uid}/devices/{installationId}`;
- добавлен базовый слой notification preferences в `SettingsRepository`;
- добавлен one-shot permission flow для авторизованного пользователя;
- при login / app start / permission result выполняется sync notification device state в Firestore.
- исправлены lifecycle-сценарии account switch / logout / delete account для notification device binding.

### Контекст перед стартом

- `Phase 1` завершена;
- `Phase 2` завершена;
- achievement/certificate flow уже частично существует;
- notifications теперь должны интегрироваться поверх существующего progression/reward pipeline, а не проектироваться в вакууме.

### Что сознательно отложено

- bell icon для authenticated user;
- отдельный `Notifications` screen;
- notification history/read-model;
- empty/disabled/filled product states;
- визуальный notification UX polishing.
