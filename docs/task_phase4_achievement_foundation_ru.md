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

## Acceptance Criteria

- achievement создаётся один раз на completed stage
- auth и guest сценарии различимы
- pending reward можно дооформить после аутентификации

