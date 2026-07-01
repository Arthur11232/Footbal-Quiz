# Task: Phase 5 — Congratulations Screen

Связанные документы:
- [android_stability_achievements_roadmap_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/android_stability_achievements_roadmap_ru.md)
- [implementation_plan_android_stability_to_certificates_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/implementation_plan_android_stability_to_certificates_ru.md)

## Цель

Создать отдельный reward moment после завершения этапа.

## Scope

- congratulations screen
- guest/auth branching
- certificate preview placeholder

## Detailed Subtasks

### 1. Screen content
- название этапа
- поздравление
- progress context
- CTA continue
- визуальный акцент на том, что пользователь именно завершил stage, а не просто ответил на очередной вопрос

### 2. Authenticated branch
- показать, что сертификат уже доступен в приложении;
- не обещать email delivery раньше, чем `Phase 7` реально будет внедрена;
- при необходимости показывать мягкий текст, что email delivery появится позже, но не строить текущий UX вокруг несуществующей отправки

### 3. Guest branch
- prompt на регистрацию
- объяснение пользы регистрации для получения сертификата
- объяснение, что прогресс этапа уже засчитан, а reward будет дооформлен после auth

### 4. Единые правила показа

Нужно формально решить и зафиксировать:

- congratulations screen показывается после фактического завершения stage;
- один и тот же completed stage не должен бесконтрольно показывать congratulations повторно;
- flow должен одинаково работать для:
  - category stages;
  - big stages (`TOP5`, `UEFA`, `World`, `Versus`, `app_complete`);
  - guest и authenticated пользователей.

### 5. Контракт между congratulations и certificate preview

Нужно определить:

- congratulations screen это отдельный шаг перед certificate preview или один объединённый reward moment;
- может ли пользователь пропустить preview;
- где потом снова найти сертификат;
- как profile подхватывает уже полученную награду.

## Product Rules To Fix

- сертификат в текущем продукте считается наградой за завершение stage по нашим completion rules;
- если stage completion уже достигнут и reward создан, повторные прохождения не должны порождать новый reward moment;
- guest видит completion moment, но персональная reward-ценность должна быть связана с auth;
- authenticated user должен понимать, что сертификат уже доступен в app без лишней двусмысленности.

## Acceptance Criteria

- экран существует как отдельный flow
- auth и guest пути различаются
- UX воспринимается как награда за прохождение этапа
- правила показа одинаковы для всех stage-сценариев

## Текущий статус

`Partially implemented`

В коде уже есть рабочий congratulations/certificate flow, но phase остаётся открытой, потому что:
- нет формально зафиксированного единого product contract;
- guest/auth messaging ещё можно улучшить;
- поведение по всем stage нужно выровнять и дополировать.
