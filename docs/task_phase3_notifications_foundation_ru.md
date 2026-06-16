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

