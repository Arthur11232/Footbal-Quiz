# Task: Phase 7 — Email Certificate Delivery

Связанные документы:
- [android_stability_achievements_roadmap_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/android_stability_achievements_roadmap_ru.md)
- [implementation_plan_android_stability_to_certificates_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/implementation_plan_android_stability_to_certificates_ru.md)

## Цель

Отправлять сертификат зарегистрированному пользователю на email.

## Scope

- email-ready auth flow
- delivery trigger
- success feedback
- guest -> auth -> deliver flow

## Detailed Subtasks

### 1. Delivery prerequisites
- есть authenticated user
- есть email
- есть completed achievement
- есть certificate payload

### 2. Delivery orchestration
- сначала показать congratulations/certificate screen
- потом отправить сертификат
- потом показать user feedback

### 3. Guest conversion flow
- guest завершает этап
- видит prompt на регистрацию
- проходит auth
- возвращается к pending reward
- получает сертификат и email

## Acceptance Criteria

- зарегистрированный пользователь получает сертификат на email
- guest можно безопасно провести через conversion flow к сертификату

