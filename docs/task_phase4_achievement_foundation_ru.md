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
- idempotency уже частично обеспечивается через локально сохранённые issued certificates;
- certificate/reward trigger уже используется в `PlayViewModel`;
- profile умеет тихо довыпускать недостающие сертификаты для уже достигнутых stages;
- local/cloud certificate merge уже существует как часть текущего sync pipeline.

## Что остаётся незакрытым

### 1. Явная domain-граница между сущностями

Сейчас в проекте частично смешаны следующие понятия:
- `stage completed`
- `achievement earned`
- `certificate issued`
- `reward ready`

Нужно формально зафиксировать и удерживать разницу между ними.

### 2. Явный guest pending reward state

Сейчас guest achievement flow работает, но не оформлен как отдельное понятное domain state.

Нужно определить:
- как хранится pending reward;
- чем он отличается от уже выданного сертификата;
- как он дооформляется после authentication;
- как не допустить двойной выдачи после login/restore/sync.

### 3. Более явная idempotency-модель

Нужно убедиться, что для одного и того же `stageId`:
- achievement не создаётся повторно;
- pending reward не создаётся повторно;
- certificate не перевыпускается бесконтрольно;
- cloud restore не создаёт дублирующее состояние.

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

`Phase 4` — частично реализована раньше плана, но формально ещё не закрыта.

Практически это означает:
- базовый achievement/certificate pipeline уже есть;
- следующая реальная задача внутри `Phase 4` — привести `guest pending reward` к явной и чистой domain-модели.
