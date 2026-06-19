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
- `top5_italy_complete`
- `top5_england_complete`
- `top5_spain_complete`
- `top5_complete`
- `uefa_super_cup_complete`
- `uefa_europa_league_complete`
- `uefa_champions_league_complete`
- `uefa_european_championship_complete`
- `uefa_complete`
- `world_complete`
- `versus_vsrm_complete`
- `versus_vsrb_complete`
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

## Current Checkpoint

- введён `ProgressionStageId` registry для:
  - `top5_france_complete`
  - `top5_germany_complete`
  - `top5_italy_complete`
  - `top5_england_complete`
  - `top5_spain_complete`
  - `top5_complete`
  - `uefa_super_cup_complete`
  - `uefa_europa_league_complete`
  - `uefa_champions_league_complete`
  - `uefa_european_championship_complete`
  - `uefa_complete`
  - `world_complete`
  - `versus_vsrm_complete`
  - `versus_vsrb_complete`
  - `versus_complete`
  - `app_complete`
- добавлен `ProgressionRepository` с local persistence через SharedPreferences
- completion хранится как persisted `achievedAt` timestamp и не теряется при обычном reset progress
- добавлен `RefreshProgressionUseCase`
- progression refresh уже вызывается:
  - после ответа в `PlayViewModel`
  - при загрузке `ProfileViewModel`
  - после restore cloud progress
- добавлен cloud-ready serialization contract:
  - `UserStatsDTO.progression`
  - `ProgressionStateDTO`
  - `CompletedStageDTO`
- completed stages теперь:
  - загружаются в cloud upload payload
  - восстанавливаются из cloud restore payload
- расширен progress snapshot granular-данными:
  - `France/Germany/Italy/England/Spain`
  - `Super Cup/Europa League/Champions League/European Championship`
  - `Versus RM-Messi / Versus Real Madrid-Barcelona`
- completion rules теперь покрывают и мелкие этапы, и aggregate-этапы
- добавлен presentation read-model слой для completed stages:
  - mapper `ProgressionSnapshot -> CompletedStageUiModel`
  - profile UI теперь показывает список завершённых этапов и summary `done/total`
- добавлен отдельный `PlayEffect` для freshly completed stages
- play flow теперь умеет отдавать новый completion event отдельно от answer animation
- completion event поднят до achievement trigger:
  - `PlayViewModel` теперь генерирует `AchievementCertificatePayload`
  - trigger срабатывает только на genuinely new stage completion
- добавлен отдельный congratulations flow:
  - новый `AchievementCertificateFragment`
  - navigation из `PlayFragment` после freshly completed stage
- сертификат теперь генерируется как отдельный branded screen:
  - stage title
  - player name
  - issue date
  - certificate id
  - signature block
  - support для нескольких stage completion за один ответ
- compile check:
  - `./gradlew :app:compileDevDebugKotlin --no-daemon --console=plain`
  - `BUILD SUCCESSFUL`

## Next Step

- сохранить generated certificates как отдельные сущности/history
- добавить auth-aware branch для guest:
  - показать prompt на sign-in ради сохранения сертификата
- затем переходить к notification + email certificate pipeline
