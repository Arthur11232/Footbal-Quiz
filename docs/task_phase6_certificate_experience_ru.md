# Task: Phase 6 — Certificate Experience

Связанные документы:
- [android_stability_achievements_roadmap_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/android_stability_achievements_roadmap_ru.md)
- [implementation_plan_android_stability_to_certificates_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/implementation_plan_android_stability_to_certificates_ru.md)

## Цель

Добавить персонализированный сертификат как визуальную награду.

## Scope

- in-app certificate preview
- user name
- stage name
- date
- author signature

## Detailed Subtasks

### 1. Certificate template
- выбрать визуальный шаблон
- определить обязательные поля

### 2. Personalization
- имя пользователя
- завершённый этап
- дата получения
- подпись автора/проекта

### 3. Единообразие между stage-ами

Нужно убедиться, что:

- все stage используют один и тот же смысловой шаблон сертификата;
- category-themed различия не ломают читаемость и узнаваемость сертификата;
- certificate payload одинаково собирается для single-stage и multi-stage reward cases.

### 4. Guest behavior
- у guest нет полного персонального сертификата до auth
- при этом пользователь должен понимать, что stage completion уже засчитан

### 5. Profile experience

Нужно довести profile presentation layer до более цельного поведения:

- completed stages и issued certificates не должны визуально противоречить друг другу;
- пользователь должен понимать, какие stage завершены, а какие сертификаты уже действительно выданы;
- preview сертификата из profile должен ощущаться как повторный доступ к награде, а не как отдельный непонятный экран.

## Product Decisions To Fix

- что именно считаем финальным `certificate issued moment`;
- показываем ли certificate preview сразу всегда или допускаем только profile revisit;
- нужен ли отдельный статус для pending reward в profile сейчас или это останется скрытой внутренней моделью до следующего phase;
- какие поля обязательны в preview первой стабильной версии.

## Acceptance Criteria

- пользователь видит персонализированный сертификат в приложении
- guest path мотивирует к регистрации
- profile experience не расходится по смыслу с reward pipeline

## Текущий статус

`Partially implemented`

Основа уже существует:
- in-app certificate preview работает;
- profile уже показывает сертификаты;
- есть runtime themed certificate flow.

Но phase ещё открыта, потому что:
- нужен единый финальный product contract;
- нужно выровнять preview/profile semantics;
- нужно решить, что именно будет первой стабильной product-версией certificate experience.
