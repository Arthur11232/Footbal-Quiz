# Task: Phase 2 — Progression Foundation

Связанные документы:
- [android_stability_achievements_roadmap_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/android_stability_achievements_roadmap_ru.md)
- [implementation_plan_android_stability_to_certificates_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/implementation_plan_android_stability_to_certificates_ru.md)

## Цель

Ввести формальную модель этапов и completion rules.

## Scope

- `stageId` model
- stage metadata
- completion criteria
- local persistence
- cloud-ready persistence design

## Detailed Subtasks

### 1. Зафиксировать stage registry
- `top5_france_complete`
- `top5_germany_complete`
- `top5_complete`
- `uefa_complete`
- `world_complete`
- `versus_complete`
- `app_complete`

### 2. Определить source of truth для completion
- какие aggregate state значения считаются достаточными
- как completion считается после restore
- как completion считается после language switch

### 3. Реализовать stage evaluation layer
- детерминированная проверка каждого этапа
- защита от случайного повторного trigger

### 4. Реализовать completed stages persistence
- local storage
- подготовка к cloud sync

## Acceptance Criteria

- completion каждого этапа вычислим формально
- этап не зависит от текущего языка
- completed stages сохраняются и восстанавливаются
- этап не награждается повторно без отдельной логики

