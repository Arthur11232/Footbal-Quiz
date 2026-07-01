# Task: Phase 4 — Achievement Foundation

Связанные документы:
- [android_stability_achievements_roadmap_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/android_stability_achievements_roadmap_ru.md)
- [implementation_plan_android_stability_to_certificates_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/implementation_plan_android_stability_to_certificates_ru.md)

## Цель

Сделать завершение этапа отдельным rewardable domain event.

## Scope

- achievement event
- persistence
- pending reward state

Важное уточнение по текущему состоянию проекта:

- значительная часть `achievement foundation` уже реализована раньше формального завершения phase;
- текущая задача не про создание achievement flow с нуля;
- текущая задача про доведение foundation до более чистой и явной доменной модели.

## Detailed Subtasks

### 1. Achievement event model
- `stageId`
- `userState`
- `earnedAt`
- display metadata

### 2. Idempotency
- этап не должен создавать achievement несколько раз

### 3. Guest reward pending
- если этап завершён гостем, сохранять pending reward opportunity

## Что уже фактически внедрено

- progression stages уже считаются и имеют стабильные `stageId`;
- completed stage определяется детерминированно;
- idempotency удерживается через локально сохранённые issued certificates и отдельный pending reward state;
- certificate/reward trigger уже используется в `PlayViewModel`;
- guest completion больше не выпускает certificate напрямую;
- pending reward конвертируется в certificate после authentication;
- `StartPageViewModel` и `ProfileViewModel` умеют consume pending rewards после auth;
- profile recovery больше не обходит guest pending reward логику;
- local/cloud certificate merge уже существует как часть текущего sync pipeline.

## Что остаётся незакрытым

### 1. Формально зафиксировать итоговый domain contract в docs

Теперь важно удерживать в документации и в дальнейшей разработке разницу между сущностями:
- `stage completed`
- `achievement earned`
- `pending reward`
- `certificate issued`
- `reward ready`

### 2. Не размыть foundation во время product polishing

Следующие phase-ы не должны снова смешать:
- domain completion logic;
- certificate issuance logic;
- notification logic;
- будущую email delivery logic.

## Acceptance Criteria

- achievement создаётся один раз на completed stage
- auth и guest сценарии различимы
- pending reward можно дооформить после аутентификации

## Обновлённая интерпретация Definition of Done

`Phase 4` можно считать завершённой, когда:

- achievement layer концептуально отделён от certificate layer;
- guest reward pending существует как явное состояние, а не как неявное побочное поведение;
- повторное завершение одного и того же stage не создаёт дублирующих reward-сущностей;
- после authentication pending reward корректно восстанавливается и дооформляется;
- foundation готова для спокойного перехода к `Phase 5/6` polishing.

## Текущий статус

`Phase 4` — завершена как foundation milestone.

Практически это означает:
- базовый achievement/certificate pipeline уже есть;
- guest pending reward приведён к явной и чистой domain-модели;
- следующая реальная работа лежит уже в `Phase 5-7`, а не внутри foundation.
