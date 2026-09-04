# Task: Versus MVVM Cleanup

Статус: In progress.

Дата: 2026-08-13.

## Цель

Перевести entry-экраны Versus (`Ronaldo vs Messi`, `Real Madrid vs Barcelona`) с legacy presenter bridge на общий ViewModel-подход, не меняя gameplay и unlock rules.

## Решения

- `VSRM_ScoreboardPresenter` и `VSRB_ScoreboardPresenter` пока не удалять.
- Оставить presenters как deprecated legacy bridge до отдельного cleanup.
- Commit делать только после ручного тестирования владельцем проекта.

## Scope

- создать общий `VersusEntryViewModel`;
- создать `VersusEntryUiState`;
- использовать `GameCatalogRepository` внутри ViewModel;
- `FragmentRonMessi` и `FragmentRealBarc` должны только render state и выполнять navigation;
- запрет ручного движения lock seekbar сохранить;
- unlock thresholds не менять:
  - `vsRM`: `Constant.FOR_RM_SCORE`;
  - `vsRB`: `Constant.FOR_RB_SCORE`.

## Acceptance Criteria

- оба Versus entry экрана показывают тот же scoreboard;
- lock screen работает как раньше;
- unlock считается по общему score;
- на unlocked screen клик запускает правильный `entryKey`;
- presenters остаются в проекте, но помечены `@Deprecated`;
- `./gradlew :app:compileDevDebugKotlin` проходит;
- commit не создаётся до ручного теста.
