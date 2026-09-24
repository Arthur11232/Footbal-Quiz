# Task: KMP Shared + iOS Foundation

Статус: минимальный `:shared` и перенос progression-моделей проверены пользователем; следующий шаг — общие правила завершения этапов.

Дата: 2026-09-22.

Предлагаемая ветка: `codex/kmp-shared-ios-foundation`.

Текущая работа: `codex/multiplatform-foundation`. Merge в `master` и новая ветка отложены по решению пользователя.

## Цель

Создать первый настоящий Kotlin Multiplatform слой для Football Quiz и подключить к нему минимальное iOS-приложение на SwiftUI.

Порядок развития продукта:

```text
Android production
    -> shared KMP domain
        -> iOS SwiftUI client
            -> Web client позже
```

На этом этапе Android остаётся основным production-клиентом. Его Firebase, Realm, XML UI и release flow не переписываются одним большим изменением.

## Что входит в задачу

- новый KMP-модуль `:shared`;
- `commonMain` с pure Kotlin моделями и правилами;
- `commonTest` с общими unit-тестами;
- Android использует выбранный shared slice без изменения UX;
- `iosMain` и сборка framework для Xcode;
- минимальный iOS shell на SwiftUI;
- первый общий вертикальный slice: progress/stage completion;
- документация по запуску Android и iOS.

## Что пока не входит

- собственный PHP/backend;
- перенос production questions с Firestore;
- полная замена Realm;
- переписывание Android UI на Compose;
- Compose Multiplatform UI;
- Google Sign-In на iOS;
- cloud sync между Android и iOS;
- сертификаты по email;
- Web-клиент;
- публикация в App Store.

## Архитектурное правило

```text
SwiftUI / Android UI
    -> platform ViewModel / adapter
        -> shared use case
            -> shared repository contract
                -> Android or iOS implementation
```

В `commonMain` запрещены:

- `android.*`;
- `Context`, `Fragment`, `ViewModel` из AndroidX;
- Firebase SDK types;
- Realm SDK types;
- Android resources и `R`;
- platform-specific date, file и network APIs без abstraction.

## Phase 0: Закрыть текущий Android foundation

### Задачи

- [x] Завершить ручную проверку restore dialog:
  - Cancel скрывает автоматический диалог для текущего `userId`;
  - успешный Restore также скрывает повторный автоматический диалог;
  - ручной Restore из Profile остаётся доступным.
- [x] Очистить только generated build artifacts, если они мешают Gradle verification.
- [x] Выполнить `:app:compileProdDebugKotlin`.
- [x] Закоммитить только app-side restore changes отдельным коммитом (`65d1dd2`).
- [x] Обновить статус `docs/task_multiplatform_foundation_ru.md`.
- [x] Закоммитить обновление документов отдельно.
- [ ] Merge `codex/multiplatform-foundation` в `master` после проверки (отложен пользователем).
- [ ] Создать ветку `codex/kmp-shared-ios-foundation` от актуального `master` (после merge).

### Acceptance criteria

- рабочее дерево не содержит случайных app/build, `.idea` или временных файлов в commit;
- Android production behavior не изменён, кроме исправленного restore flow;
- Phase 1 Android foundation формально закрыта.

## Phase 1: KMP compatibility audit

### Задачи

- [x] Зафиксировать исходные версии перед обновлением:
  - Kotlin Gradle Plugin `2.3.20` из `gradle/libs.versions.toml`;
  - Gradle `9.4.1`;
  - Java/JVM target `17`;
  - Android Gradle Plugin `9.1.0` из version catalog;
  - установленный Xcode `27.0` на Apple Silicon.
- [x] Проверить совместимость Kotlin Multiplatform plugin с текущим Gradle/AGP: текущая связка выходит за опубликованную матрицу KMP.
- [x] Выбрать менять только Kotlin Gradle Plugin на `2.4.20` отдельным code slice с Android regression build; Gradle и AGP пока сохранить.
- [x] Выбрать формат Gradle для нового модуля: `shared/build.gradle.kts`.
- [x] Выбрать direct integration через `embedAndSignAppleFrameworkForXcode` после создания iOS shell.
- [x] Зафиксировать минимальные iOS targets:
  - `iosArm64`;
  - `iosSimulatorArm64`;
  - `iosX64` не нужен на текущем Apple Silicon Mac.

### Результат аудита (2026-09-23)

- До обновления Android build использовал KGP `2.3.20` через version catalog, а корневой `build.gradle` содержал устаревшую переменную `ext.kotlin_version = '2.2.10'`. Теперь обе записи указывают `2.4.20`; отдельного использования `ext.kotlin_version` в исходниках проекта не найдено.
- По [матрице JetBrains](https://kotlinlang.org/docs/multiplatform/multiplatform-compatibility-guide.html), KMP `2.3.20` заявлен для Gradle до `9.3.0`, AGP до `9.0.0` и Xcode до `26.0`. Это ниже текущих Gradle `9.4.1`, AGP `9.1.0` и Xcode `27.0`.
- KMP `2.4.20` в той же матрице покрывает Gradle `9.4.1` и AGP `9.1.0`, но опубликованная поддержка Xcode заканчивается на `26.4`. Xcode `27.0` может работать, однако не входит в официально проверенную комбинацию; успешную iOS-сборку нельзя обещать до проверки.
- [AGP 9.1 требует Gradle минимум `9.3.1` и JDK 17](https://developer.android.com/build/releases/agp-9-1-0-release-notes); текущие `9.4.1` и JDK 17 подходят для Android части.
- Для Android target в новом KMP-модуле использовать [`com.android.kotlin.multiplatform.library`](https://developer.android.com/kotlin/multiplatform/plugin) с `kotlin { android { ... } }`. `:app` остаётся Android application; новый shared-модуль не получит app flavors или View Binding.
- Для Xcode выбрать [direct integration](https://kotlinlang.org/docs/multiplatform-direct-integration.html) с `embedAndSignAppleFrameworkForXcode`.
- На машине установлен только `/Applications/Xcode.app` версии `27.0`. После обновления `xcodebuild -version` работает, но проверка `simctl` в текущем sandbox не смогла подключиться к CoreSimulatorService. Для iOS verification потребуется использовать Xcode версии, поддерживаемой выбранным Kotlin, либо отдельно подтвердить работоспособность Xcode 27.

### Выполнение Kotlin upgrade (2026-09-23)

- [x] Обновить KGP `2.3.20 -> 2.4.20` в version catalog и синхронизировать `ext.kotlin_version`; AGP `9.1.0` и Gradle `9.4.1` не менялись.
- [x] Выполнить `:app:compileProdDebugKotlin` — успешно.
- [x] Выполнить `:app:testProdDebugUnitTest` — 4 теста, 0 ошибок.
- [x] Выполнить `:app:bundleProdRelease` — успешно; локальный bundle: `app/build/outputs/bundle/prodRelease/app-prod-release.aab`.
- [x] Ручная проверка Android после обновления Kotlin подтверждена пользователем; коммит `9690780`.
- [x] Сборка iOS simulator framework после создания `:shared`.
- [ ] Запуск iOS-приложения в Simulator после создания iOS shell.

### Следующий проверяемый шаг

1. Kotlin upgrade проверен пользователем и зафиксирован коммитом `9690780`.
2. Минимальный `:shared` собран с установленным Xcode 27; Android с подключённым модулем проверен пользователем.

### Acceptance criteria

- версия KGP обновлена и Android build проверен; для iOS доступен поддерживаемый Xcode или подтверждена работоспособность Xcode 27;
- выбран один способ интеграции с Xcode;
- нет лишнего массового Gradle migration.

## Phase 2: Создать минимальный `:shared` module

### Задачи

- [x] Добавить `include ':shared'` в project settings.
- [x] Создать `shared/build.gradle.kts`.
- [x] Настроить targets:
  - Android/JVM target;
  - `iosArm64`;
  - `iosSimulatorArm64`.
- [x] Настроить source sets Kotlin Multiplatform (платформенные каталоги пока пусты):
  - `commonMain`;
  - `commonTest`;
  - `androidMain`;
  - `iosMain`.
- [x] Добавить минимальный pure Kotlin API `SharedBuildInfo` и общий smoke-тест.
- [x] Подключить `:shared` к Android `:app`.
- [x] Проверить `:app:compileProdDebugKotlin` и `:app:testProdDebugUnitTest`.
- [x] Собрать `:shared:linkDebugFrameworkIosSimulatorArm64` с установленным Xcode 27.
- [x] Ручная проверка Android-приложения после подключения `:shared` подтверждена пользователем.

### Результат сборки (2026-09-23)

- `:shared:assembleAndroidMain`, `:app:compileProdDebugKotlin` и `:shared:linkDebugFrameworkIosSimulatorArm64` прошли успешно.
- `:shared:compileTestKotlinIosSimulatorArm64` и существующие Android unit-тесты прошли успешно. Общий тест только скомпилирован, не запущен в Simulator.
- Framework создан в `shared/build/bin/iosSimulatorArm64/debugFramework/FootballQuizShared.framework`.
- iOS shell ещё нет, поэтому интеграция с Xcode и запуск на симуляторе пока не проверены.

### Acceptance criteria

- `:shared` собирается отдельно;
- Android компилируется с dependency на `:shared`;
- iOS framework создаётся;
- ни один production flow пока не переключён.

### Предлагаемый commit

`Add minimal Kotlin Multiplatform shared module`

## Phase 3: Перенести первый pure domain slice

Первым переносим progress/stage completion. Не начинаем с Auth, Firebase sync или questions download.

Первый checkpoint: `ProgressionStageId` и progression-модели перенесены в `:shared/commonMain` без изменения package или содержимого. Android-компиляция, существующие unit-тесты и iOS simulator framework собираются. Общие тесты скомпилированы для iOS, но не запущены. Ручная проверка прогресса и сертификатов подтверждена пользователем 2026-09-24.

### Кандидаты для переноса

- `CompletedStageRecord`;
- stable stage identifier;
- stage progress snapshot;
- правила определения завершённого этапа;
- правило no duplicate completion;
- правила eligibility для certificate achievement без UI payload.

### Задачи

- [x] Отделить `ProgressionStageId` и progression-модели от Android-модуля без изменения их API.
- [ ] Перенести остальные выбранные модели в `shared/src/commonMain` после проверки первого checkpoint.
- [ ] Перенести чистые stage completion rules в shared use case/service.
- [ ] Не переносить Realm entities и Firebase DTO.
- [ ] Добавить mapping Android data models -> shared domain, если он нужен временно.
- [ ] Переключить Android progression calculation на shared rule.
- [ ] Сохранить текущие stage IDs и production semantics.

### Acceptance criteria

- одинаковый вход даёт одинаковый completion result до и после переноса;
- Android UI и сертификаты работают без изменения поведения;
- общая логика не зависит от Android SDK.

### Предлагаемый commit

`Move stage completion rules into shared domain`

## Phase 4: Общие unit-тесты

### Обязательные сценарии

- [ ] незавершённый этап не становится completed;
- [ ] ровно достигнутый threshold завершает этап;
- [ ] результат выше threshold остаётся completed;
- [ ] один stage не выдаётся повторно;
- [ ] завершение France/Germany/Italy/Spain/England считается одинаково;
- [ ] aggregate stages `TOP5`, `UEFA`, `World`, `Versus`, `All App` считаются по текущим production rules;
- [ ] неизвестный stage ID обрабатывается безопасно;
- [ ] пустой progress snapshot не создаёт achievement.

### Задачи

- [ ] Создать tests в `shared/src/commonTest`.
- [ ] Добавить regression fixtures для текущих 16 stages.
- [ ] Выполнить shared tests на JVM.
- [ ] Выполнить iOS simulator test target, если toolchain позволяет.

### Acceptance criteria

- stage business rules покрыты common tests;
- тесты не используют Realm/Firebase/Android resources.

### Предлагаемый commit

`Add shared progression regression tests`

## Phase 5: Repository contracts для shared слоя

На этом этапе переносим только контракты, которые действительно нужны первому shared slice.

### Задачи

- [ ] Определить shared contract для чтения progress snapshot.
- [ ] Определить shared contract для completed stage records.
- [ ] Решить, нужен ли shared certificate eligibility contract сейчас или в следующей фазе.
- [ ] Оставить Android Realm implementations в `:app`.
- [ ] Создать Android adapters, реализующие shared contracts.
- [ ] Не переносить `Context`, `Prefer`, Realm objects или Firestore DTO в shared API.
- [ ] Не создавать универсальный repository только ради абстракции.

### Acceptance criteria

- shared use case получает данные только через pure Kotlin contracts;
- Android adapters инкапсулируют текущий Realm/Preferences код;
- Android gameplay продолжает работать с существующими данными.

### Предлагаемый commit

`Connect Android progress storage to shared contracts`

## Phase 6: Создать iOS SwiftUI shell

### Задачи

- [ ] Создать отдельный Xcode project/app target `FootballQuizIos`.
- [ ] Установить уникальный bundle identifier.
- [ ] Подключить KMP framework.
- [ ] Создать минимальную SwiftUI navigation structure.
- [ ] Добавить первый экран диагностики shared слоя:
  - версия shared module;
  - список stage definitions;
  - тестовый progress snapshot;
  - вычисленный completion result.
- [ ] Не подключать Firebase Auth на этом шаге.
- [ ] Проверить запуск на iOS Simulator.
- [ ] Зафиксировать команды сборки и требования к Xcode.

### Acceptance criteria

- iOS-приложение запускается в Simulator;
- SwiftUI вызывает общий Kotlin API;
- результат shared stage rule отображается на iOS;
- Android продолжает собираться.

### Предлагаемый commit

`Add SwiftUI iOS shell backed by shared domain`

## Phase 7: Первый реальный cross-platform экран

Рекомендуемый первый экран: read-only Progress / Completed Stages.

### Почему он первый

- не требует Google Sign-In;
- не требует gameplay timer или рекламы;
- использует уже перенесённые shared rules;
- позволяет сравнить одинаковый результат Android и iOS;
- не блокирует будущий дизайн.

### Задачи

- [ ] Создать shared presentation/read model без Android resources.
- [ ] На Android оставить существующий Profile UI, используя shared result через adapter.
- [ ] На iOS создать SwiftUI Progress screen.
- [ ] Добавить preview/demo data для iOS до подключения реального storage.
- [ ] Проверить одинаковый порядок и status stage IDs на Android/iOS.
- [ ] Не выдавать настоящие cloud certificates из demo data.

### Acceptance criteria

- один shared progress snapshot одинаково интерпретируется двумя клиентами;
- iOS показывает completed и incomplete stages;
- Android regression отсутствует.

### Предлагаемый commit

`Add shared progress read model for Android and iOS`

## Phase 8: Следующая задача после foundation

После успешного Phase 7 отдельно спланировать mobile sync phase:

1. Firebase Auth для iOS или временный authenticated test flow.
2. Общая identity model `AuthenticatedUser` в shared.
3. Platform auth adapters для Android/iOS.
4. Общий sync decision rule.
5. iOS persistence strategy.
6. Cloud progress read/restore/upload.
7. Certificates sync.
8. Questions catalog и gameplay только после стабилизации sync.

Это должна быть новая задача. Не смешивать её с созданием KMP foundation.

## Общая проверка после каждого code slice

```bash
./gradlew :shared:allTests
./gradlew :app:compileProdDebugKotlin
```

Перед merge:

```bash
./gradlew :app:testProdDebugUnitTest
./gradlew :app:bundleProdRelease
```

Дополнительно:

- собрать iOS framework;
- запустить iOS Simulator;
- проверить Android вручную;
- не коммитить generated frameworks, DerivedData и build directories.

## Definition of Done

- [ ] `:shared` является настоящим KMP module;
- [ ] `commonMain` не зависит от Android/Firebase/Realm;
- [ ] progress/stage completion работает из shared кода;
- [ ] shared rules покрыты common tests;
- [ ] Android использует shared slice без изменения UX;
- [ ] iOS SwiftUI app запускается в Simulator;
- [ ] iOS вызывает тот же shared domain code;
- [ ] release Android bundle собирается;
- [ ] документация обновлена;
- [ ] backend и Web остаются отдельными последующими фазами.

## План коммитов

1. `Close Android multiplatform foundation phase`
2. `Add minimal Kotlin Multiplatform shared module`
3. `Move stage completion rules into shared domain`
4. `Add shared progression regression tests`
5. `Connect Android progress storage to shared contracts`
6. `Add SwiftUI iOS shell backed by shared domain`
7. `Add shared progress read model for Android and iOS`
8. `Document KMP and iOS build workflow`
