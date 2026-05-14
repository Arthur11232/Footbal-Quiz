# Project Context: Football Quiz (Current)

Этот файл служит основным источником контекста для ИИ-помощника. Здесь фиксируется текущее состояние проекта, выполненные задачи и план по модернизации.

## 📌 Обзор проекта
- **Название:** Football Quiz
- **Версия:** 2.0.2
- **Основной стек:** Kotlin, Android SDK (`compileSdk 36`, `targetSdk 36`), Single Activity Architecture (SAA), Jetpack Navigation Component, Firebase (Firestore, Auth, Analytics), Realm DB, Coroutines, Koin.
- **Цель:** Переход от legacy feature-driven UI-логики к сильной MVVM-архитектуре с `ViewModel`, `Repository`, `UseCase`, DI через Koin и заделом на будущую многомодульность.

---

## 🛠 Выполненные задачи
- **Миграция на SAA:** Весь проект переведен в `MainActivity` с использованием Fragments.
- **Навигация:** Внедрен `Navigation Component`.
- **Сборка:** Переход на `Version Catalog` (`libs.versions.toml`).
- **Язык:** Практически полная миграция на Kotlin.
- **UI:** Поддержка Edge-to-Edge (Android 15), внедрены кастомные диалоги (New Game, Language) с дизайном из версии 1.8.
- **Localization 2.0:** Реализован "Smart Sync" (авто-обновление вопросов при смене языка) и исправлено отображение локализованного UI через `BaseActivity` (`attachBaseContext`).
- **Cloud Sync 2.0:** Реализовано автоматическое восстановление и резервное копирование прогресса через Firestore. При входе в аккаунт и каждом открытии стартового экрана приложение сравнивает локальный и облачный прогресс, предлагает восстановление при необходимости и автоматически выгружает локальные данные, если они новее.
- **Profile Action UI:** В профиле sync-действия signed-in пользователя оставлены компактными icon-actions рядом со статистикой, а `Sign Out` и `Delete Account` вынесены под статистику как аккуратные icon+text chips. Опасные действия по-прежнему требуют подтверждения через кастомный dialog.
- **Profile Progress UI:** Профиль показывает понятный прогресс: общие `answered/total`, процент прохождения и отдельный блок по категориям `Top 5`, `UEFA`, `World Cup`, `Versus`. Расчёт `Versus` учитывает оба режима (`vsRM + vsRB`).
- **One-Time Auto Restore Prompt:** Автоматическое предложение восстановления cloud-save на Start Page показывается только до первого отказа пользователя для конкретного `uid`; после cancel/back/outside tap приложение больше не повторяет restore prompt автоматически, оставляя ручной выбор в Profile.
- **Recovery UX:** После восстановления прогресса автоматически разблокируется `Continue` (`isFirstPlay = false`), чтобы пользователь мог сразу продолжить игру после переустановки приложения или смены устройства.
- **Custom Sync Dialogs:** Системные диалоги восстановления/конфликта синхронизации заменены на кастомный layout `dialog_cloud_sync.xml` в стиле проекта.
- **GDPR / Data Deletion:** Добавлен сценарий удаления пользовательских данных из профиля: удаляются Firestore `users/{uid}`, локальный прогресс/Realm state, локальная custom profile photo и Firebase Auth user, после чего выполняется logout и переход на стартовый экран.
- **Privacy & Terms:** Документы Privacy Policy и Terms & Conditions вынесены в отдельные Markdown/HTML-файлы для публикации, а приложение использует внешние ссылки на опубликованные документы.
- **Web Legal Pages:** В `Arthur11232/Football-Quiz-Web` обновлены `privacy_policy.md`, `terms_and_conditions.md`, `privacy-policy.html`, `terms-and-conditions.html`; добавлены `data_deletion.md` и `data-deletion.html` для Play Console account/data deletion URL.
- **PlayFragment Release Hardening:** Экран игры усилен перед релизом: устранён риск бесконечного выбора вопроса, `save_question_state` стал scoped по категории/месту, `CountDownTimer` и анимация корректно отменяются при destroy, ad callbacks защищены от detached fragment, Realm закрывается в горячих методах ответа.
- **Ads Disabled in First Release:** Рекламная инфраструктура сохранена в проекте для будущего включения, но в версии 1.0.0 фактически выключена через `Constant.ADS_ENABLED = false`: баннеры скрыты, AdMob не инициализируется, interstitial/rewarded не загружаются, reset/reload работают бесплатно.
- **Release Store Hygiene:** Включён строгий release lint (`abortOnError=true`, `checkReleaseBuilds=true`), отключены AAB language splits для in-app language switcher, добавлены backup/data extraction rules без восстановления локальных user data.
- **System Bars Styling:** Добавлен общий `SystemBarStyleHelper`; основные экраны (`Splash`, `StartPage`, `ChooseGame`, `CategoryPage`, `Play`, `Versus`, `Profile`, `Privacy`, `Terms`) теперь синхронно выставляют `status bar` и `navigation bar` под свой фон. Для экранов с изображением цвет берётся из верхней/нижней полосы drawable, для однотонных экранов используется целевой ресурс цвета.
- **Questions Firestore Update:** Коллекция Firestore `questions` обновлена из актуального `app/questions_audit.json`. Записано 1938 документов (`questions/0..1937`) в порядке `en, ru, hy`, пользовательский прогресс (`users/{uid}`) не затрагивался.
- **Firestore Updater Tool:** Добавлен локальный инструмент `tools/update_firestore_questions.py` для `inspect`, `dry-run` и batch-обновления вопросов в Firestore. Service account хранится локально и игнорируется git-ом.
- **Очистка:** Основной flow очищен перед релизом, но legacy/debug файлы (`TestActivity`, `TouchActivity`, `TestFragment`, `TouchFragment`, `VSFragment`, `FQ_Timer`) оставлены в проекте, чтобы не терять старый код до отдельного решения.
- **Offline:** Реализована логика офлайн-игры (загрузка из Firestore -> сохранение в Realm).
- **Debug Logging:** Добавлены точечные логи с тегом `FQ_Log` для сценариев `New Game`, загрузки вопросов из Firestore и cloud sync/recovery, чтобы упростить анализ `logcat`.
- **Koin DI (Phase 1):** Подключён Koin и проверен на debug/release сборках. В контейнер вынесены `FirebaseAuth`, `FirebaseFirestore`, `AuthManager`, `FirestoreQuestionService`, `UserStatsService`, а также обычный и параметризованный `DB_Helper`. Основные экраны (`StartPage`, `Profile`, `Splash`, `ChooseGame`, `CategoryPage`, `Play`, `VS`) и versus score presenters переведены на DI вместо прямого ручного создания зависимостей.
- **MVVM Start Feature (Phase 2 / Step 1):** Созданы `data/domain/presentation` слои для первой feature-миграции. Добавлены `AuthRepository`, `QuestionsRepository`, `SettingsRepository`, `SyncRepository`, use cases для sign-in / localization sync / cloud sync и первый `StartPageViewModel`. `StartPageFragment` переведён на `UiState + UiEffect` flow: логика sign-in, загрузки вопросов и решений по cloud sync больше не живёт напрямую в UI-слое.

---

## 🚀 Текущий план модернизации (Backlog)

### Phase A. MVVM Foundation
- [x] Подключить Koin и перевести первый слой сервисов/SDK на DI.
- [x] Ввести слои `data`, `domain`, `presentation`.
- [x] Создать отдельные Koin-модули: `firebaseModule`, `repositoryModule`, `useCaseModule`, `viewModelModule`.
- [ ] Выделить `dataSourceModule` после распила `DB_Helper` и Firebase-обёрток на более узкие local/remote sources.
- [ ] Зафиксировать единый стиль `UiState / UiAction / UiEffect` для feature-экранов.

### Phase B. Repository Layer
- [~] Ввести интерфейсы репозиториев:
  - `AuthRepository`
  - `QuestionsRepository`
  - `SyncRepository`
  - `UserStatsRepository`
  - `GameProgressRepository`
- [~] Спрятать Firebase/Realm/Preferences за repository contracts.
- [ ] Постепенно выводить legacy-логику из `DB_Helper` в более узкие local data sources.

### Phase C. First MVVM Migration
- [x] Перевести `StartPageFragment` на `StartPageViewModel` как первую опорную feature.
- [ ] Перевести `ProfileFragment` на `ProfileViewModel`.
- [~] Отдельно выделить sync/use-case flow из `CloudSyncManager`.

### Phase D. Gameplay MVVM
- [ ] Перевести `PlayFragment` на `PlaySessionViewModel`.
- [ ] Отделить game session state от ad/UI callbacks.
- [ ] Перевести `CategoryPage` и versus-flow на presentation слой с state-моделями.

### Phase E. Многомодульность (подготовка)
- [ ] Подготовить проект к разбиению на Gradle-модули без резкого переписывания.
- [ ] После стабилизации MVVM выделить возможные будущие модули:
  - `:core:common`
  - `:core:ui`
  - `:core:firebase`
  - `:core:database`
  - `:feature:start`
  - `:feature:profile`
  - `:feature:play`
  - `:feature:versus`
  - `:feature:legal`
- [ ] Следить, чтобы новые repository/viewmodel пакеты уже проектировались с учётом будущего переезда в отдельные модули.

### Ближайший execution plan
1. Обновить `PROJECT_CONTEXT.md` и зафиксировать Koin integration.
2. Зафиксировать первый MVVM-срез (`StartPage`) после compile/release валидации.
3. Перевести `ProfileFragment` на `ProfileViewModel` и `UserStatsRepository`.
4. Начать вынос `DB_Helper` в более узкие local data source / progress repository слои.
5. После стабилизации `Start` + `Profile` подготовить multi-module-friendly границы `core/*` и `feature/*`.

---

## 📚 История этапов

1.  **AdMob: Адаптивные баннеры**
    - **Результат:** Логика `Adaptive Banner` перенесена из версии 1.8.
    - **Статус:** ✅ Завершено.

2.  **Firebase: Localization & Firestore Sync**
    - **Результат:** Реализована автоматическая перекачка вопросов при смене языка с сохранением прогресса.
    - **Статус:** ✅ Завершено.

3.  **Auth: Google Sign-In**
    - **Результат:** Google Sign-In интегрирован через `AuthManager` и `StartPageFragment`.
    - **Статус:** ✅ Завершено.

4.  **UI Modernization: Choose Game Screen**
    - **Результат:** Honeycomb-сетка реконструирована на `ConstraintLayout` с сохранением визуального соответствия оригиналу.
    - **Статус:** ✅ Завершено.

5.  **UI Modernization: Custom Dialogs**
    - **Результат:** Стандартные `AlertDialog` заменены на кастомные диалоги в стиле проекта.
    - **Статус:** ✅ Завершено.

6.  **Контент: Аудит актуальности вопросов**
    - **Результат:** Проверены и экспортированы все **1938 вопросов** в `app/questions_audit.json`. Дополнительно исправлен `categoryType` для всех вопросов `Europa League` (`UEFA`), а повторяющиеся формулировки с разными наборами ответов признаны допустимыми и вынесены в отдельную заметку для ручного контент-аудита.
    - **Статус:** ✅ Завершено.

7.  **UI Modernization: Google Sign-In Button**
    - **Результат:** Перенесён дизайн кнопки входа из версии 1.8 и унифицированы размеры кнопок меню.
    - **Статус:** ✅ Завершено.

8.  **Очистка проекта**
    - **Результат:** Удалены лишние Java-файлы (`TestActivity`, `TouchActivity`), кастомные вью переведены на Kotlin.
    - **Статус:** ✅ Завершено.

9.  **Cloud Sync: Manual -> Automatic**
    - **Результат:** Ручная синхронизация перестала быть основным сценарием. Добавлены автопроверка на `StartPageFragment`, автозагрузка в cloud при выходе из `PlayFragment`, восстановление `Continue` после restore и кастомные recovery-диалоги.
    - **Статус:** ✅ Завершено.

10. **Firestore Questions: Audit -> Production Update**
    - **Результат:** Создан updater `tools/update_firestore_questions.py`, проверена текущая структура Firestore (`answers.a/b/c/d/right`), выполнены `inspect` и `dry-run`, затем реальный `--commit` успешно перезаписал 1938 документов в коллекции `questions`. После записи выполнен контрольный `inspect`.
    - **Статус:** ✅ Завершено.

11. **Release Prep: GDPR, PlayFragment, Ads Disabled**
    - **Результат:** Добавлены удаление user data из профиля и legal links, усилен `PlayFragment` перед релизом, исправлен Realm lifecycle в методах ответа, улучшена анимация goal, AdMob оставлен dormant-кодом и выключен через флаг в версии 1.0.0, `allowBackup=false` и backup/data extraction rules для корректного удаления локальных данных.
    - **Статус:** ✅ В работе / финальная ручная проверка на устройстве.

12. **UI Polish: System Bars Consistency**
    - **Результат:** Введён единый helper для системных панелей, чтобы `status bar` и `navigation bar` на основных экранах совпадали с фоном приложения. `PlayFragment` и `SplashFragment` используют sampled colors из background drawable; остальные ключевые экраны настроены через общий helper.
    - **Статус:** ✅ Завершено.

13. **Architecture Modernization: Koin DI Phase 1**
    - **Результат:** В проект добавлен Koin; основные сервисы и SDK вынесены в DI-контейнер, ручное создание зависимостей в ключевых фрагментах и presenters сокращено, `debug` и `prodRelease` сборки успешно проверены после интеграции.
    - **Статус:** ✅ Завершено.

---

## 📝 Заметки
- **Localization:** Для корректной смены языка без перезагрузки системы используется `attachBaseContext` в `BaseActivity`.
- **Smart Sync:** Если `db_lang` в `SharedPreferences` не совпадает с выбранным языком, приложение автоматически скачивает новые вопросы при входе в игру ("New Game" или "Continue").
- **Firestore Questions:** Коллекция `questions` используется как публичный источник контента для локального кеширования в Realm. Для корректной работы гостевого режима правила Firestore должны разрешать чтение вопросов без авторизации.
- **Cloud Progress:** Пользовательская статистика хранится отдельно и должна оставаться приватной (`users/{uid}` только для владельца). Логика сравнения прогресса вынесена в `CloudSyncManager`.
- **Manual Sync UX:** Если cloud-progress нежелателен, пользователь может отменить авто-восстановление один раз и продолжить играть локально. Позже в `ProfileFragment` доступны compact icon-actions: restore из cloud и overwrite cloud текущим прогрессом устройства.
- **Recovery Flow:** Восстановление прогресса больше не должно оставлять пользователя в состоянии "только New Game" после переустановки. После restore автоматически включается `Continue`.
- **Debugging:** Для анализа проблем приложения использовать `adb logcat -s FQ_Log`. Это отсекает системный шум MIUI/Google Play Services и показывает только ключевой flow приложения.
- **User Data Deletion:** Основной GDPR-сценарий находится в `ProfileFragment`: пользователь может удалить данные из приложения. После успешного удаления локальные и cloud данные очищаются, Auth user удаляется при возможности, пользователь выходит из аккаунта и возвращается на Start Page.
- **Profile Photo:** Custom profile photo хранится локально как path/string preference (`Constants.UserPhotoKey`), не синхронизируется в Firebase Storage и удаляется вместе с локальными user data.
- **Ads Policy for First Release:** На первый релиз реклама не показывается. AdMob SDK, banner placeholders и interstitial/rewarded код сохранены, но все пути защищены `Constant.ADS_ENABLED = false`; `MobileAds.initialize()` не вызывается, ad requests не отправляются, баннеры скрыты, rewarded reset/reload заменены бесплатным действием. Если реклама будет включаться позже, нужно обновить consent/privacy wording и Play Console declarations до релиза.
- **Legal Status:** Актуальным источником Privacy Policy / Terms & Conditions считаются внешние документы в `docs/privacy_policy.md` и `docs/terms_and_conditions.md`, на которые ссылается `StartPageFragment` через `privacy_policy_url` и `terms_and_conditions_url`. Встроенные тексты `fq_privacy_policy` / `fq_terms_conditions` в `app/src/main/res/values*` синхронизированы с текущим поведением приложения: guest mode, Google Sign-In, Firestore cloud sync, ads disabled в `1.0.0`, локальное хранение custom profile photo и in-app delete account из `ProfileFragment`.
- **PlayFragment Release Notes:** `newGame()` больше не использует бесконечный `while(true)`, а выбирает из списка unanswered indexes. `save_question_state` больше не общий ключ, а scoped key по категории и place type. `CountDownTimer`, `goalAnimator` и temporary flying ball очищаются в `onDestroyView()`.
- **Realm Lifecycle:** `DB_Helper.setAnswered()` и `DB_Helper.setCategoryScores()` теперь закрывают Realm в `finally`; это важно, потому что методы вызываются на каждом ответе.
- **Question Audit JSON:** После локального анализа `questions_audit.json` пустой `categoryType` для `Europa League` устранён. Повторы формулировок вопроса с разными вариантами ответов не считаются ошибкой автоматически и требуют отдельной редакторской проверки.
- **Firestore Questions Updater:** Для обновления production-вопросов использовать `python3 tools/update_firestore_questions.py update --service-account tools/service-account.local.json --language-order en,ru,hy --dry-run`, затем только после явного подтверждения `--commit`. Инструмент не удаляет коллекцию, а перезаписывает numbered docs `questions/0..1937`; `tools/service-account.local.json` и admin SDK ключи игнорируются git-ом.
- **Firestore Question Shape:** Текущая структура Firestore для ответов использует lower-case ключи `answers.a`, `answers.b`, `answers.c`, `answers.d`, `answers.right`. Updater пишет именно в этом формате, чтобы не ломать существующее чтение вопросов.
- **Release Verification:** После восстановления dormant Ads-кода `./gradlew :app:clean :app:assembleDebug` проходит успешно. Перед финальным релизным commit/push нужно заново прогнать `./gradlew :app:lintRelease :app:bundleRelease` в strict lint mode; сформированный локальный `app-release.aab` будет unsigned, пока не добавлен upload/release keystore и signing config.
- **Koin Verification:** После интеграции Koin успешно проходят `./gradlew :app:compileDevDebugKotlin`, `./gradlew :app:bundleProdRelease` и `./gradlew :app:assembleProdRelease`. На телефоне приложение запускается без fatal crash после qualifier-fix для параметризованного `DB_Helper`.
- **MVVM Verification:** Первый MVVM-срез для `StartPage` и второй чекпоинт для `Profile` успешно проходят `./gradlew :app:compileDevDebugKotlin` и `./gradlew clean :app:bundleProdRelease`. Текущий bridge-подход подтверждён рабочим: `Repository`, `UseCase` и `ViewModel` слои уже обслуживают sign-in, localization sync, start-level cloud sync и profile-level account/progress flows без поломки release build.
- **Profile MVVM Checkpoint:** `ProfileFragment` переведён на state/effect flow через `ProfileViewModel`; добавлены `UserStatsRepository` и `GameProgressRepository`, которые инкапсулируют удаление cloud stats, restore/upload sync, локальный progress snapshot, profile photo storage и очистку локальных user data. Фрагмент больше не orchestrates Firebase/DB напрямую и готов к дальнейшему выносу в feature-layer.
- **Release Build Note:** При первой release-проверке после Profile MVVM всплыл stale generated artifact `app/build/generated/updated_navigation_xml/prodRelease/navigation/nav_graph 2.xml`; проблема не была кодовой и исчезла после `clean`. Для следующих архитектурных миграций release-валидацию лучше сразу запускать как `./gradlew clean :app:bundleProdRelease`.
- **MVVM Direction:** Следующий безопасный шаг — не переписывать всё сразу, а продолжать bridge-архитектуру: после `StartPage` и `Profile` идти в декомпозицию `DB_Helper` на более узкие local data sources, затем переводить игровые сценарии (`Play`, `Versus`). Это снижает риск регрессий и одновременно подготавливает код к многомодульности.
- **Multi-Module Direction:** Пока проект ещё single-module (`settings.gradle` включает только `:app`), но новые пакеты и DI-модули должны проектироваться так, будто они позже переедут в `core/*` и `feature/*` модули. Особенно важно избегать тесной связки UI с `DB_Helper` и Firebase SDK напрямую.
- **UEFA Currentness Review:** Начат поэтапный аудит актуальности вопросов. Для `Europa League` и `Champions League` внесены безопасные правки в `questions_audit.json`; во втором проходе после победы Tottenham `2024/25` обновлён ответ `Juventus, Tottenham` в вопросе among listed UEFA Cup / Europa League winners; детали зафиксированы в `app/questions_uefa_currentness_review.md`.
- **World Championship Review:** Начат следующий этап аудита. Исправлены вопросы про большее число голов на ЧМ среди предложенных игроков (`Lionel Messi`), most appearances (`Lionel Messi`) и прохождение группы действующим чемпионом в XXI веке (`Brazil and France`); детали зафиксированы в `app/questions_world_currentness_review.md`.
- **France Currentness Review:** Исправлены актуальные рекорды сборной Франции в `en/hy` по FFF: крупнейшая победа (`Gibraltar`), most caps (`Hugo Lloris`), top scorer (`Olivier Giroud`) и most matches as coach (`Didier Deschamps`); детали зафиксированы в `app/questions_france_currentness_review.md`.
- **Germany Currentness Review:** Исправлены актуальные и явно ошибочные рекорды Germany/Bundesliga: `Manuel Neuer`, `RB Leipzig`, `Robert Lewandowski`, `Timo Konietzka`, `Charly Korbel`, coach count `12` и полный набор German World Cup Golden Boot winners; детали зафиксированы в `app/questions_germany_currentness_review.md`.
- **Italy Currentness Review:** Исправлены актуальные рекорды Italy/Serie A: `Gianluigi Buffon`, `Juventus` в Supercoppa Italiana, `1968; 2020`, `Ciro Immobile` в рекорде одного сезона и `Dries Mertens` для Napoli; детали зафиксированы в `app/questions_italy_currentness_review.md`.
- **Spain Currentness Review:** Исправлены актуальные рекорды Spain/La Liga: `4` EURO titles, `2` Olympic gold medals, `12` Olympic appearances, `Sergio Ramos`, `Lionel Messi` по Pichichi и два broken `right_answer`/варианта; детали зафиксированы в `app/questions_spain_currentness_review.md`.
- **English Currentness Review:** Исправлены актуальные рекорды English/Premier League: `Liverpool, Manchester United`, `Tottenham`, `Manchester`, `Erling Haaland`, `Manchester United`, `Manchester City` и `Harry Kane`; детали зафиксированы в `app/questions_english_currentness_review.md`.
- **vsRM Currentness Review:** Исправлены устаревшие и несинхронные ответы Ronaldo vs Messi по La Liga, UCL, World Cup, national team goals, Copa America, Club World Cup, Ballon d'Or / Golden Shoe и sponsor questions; последний global broken `right_answer` (`Brazil- Sweden`) приведён к варианту `Brazil - Sweden`; детали зафиксированы в `app/questions_vsrm_currentness_review.md`.
- **vsRB Currentness Review:** Исправлены актуальные сравнения Real Madrid vs Barcelona по UEFA Super Cup `5+` (`Both`), LaLiga defeats `600+` (`Both`) и точному вопросу про `4` финала Club World Cup (`Barcelona`); детали зафиксированы в `app/questions_vsrb_currentness_review.md`.
- **Super Cup Currentness Review:** Исправлены актуальные рекорды UEFA Super Cup после обновлений `2024/2025`: Real Madrid как лидер по титулам, Barcelona/Real Madrid по участиям, PSG/France case, `6` English clubs, `26` clubs total, Ancelotti, Carvajal/Modric, Diego Costa и проигранные Super Cup по Chelsea/Manchester United и Sevilla; детали зафиксированы в `app/questions_super_cup_currentness_review.md`.
- **European Championship Currentness Review:** Исправлены актуальные EURO-рекорды после `2024`: Spain как единоличный лидер по титулам, Cristiano Ronaldo как единоличный top scorer и tie `Germany, Spain` по странам тренеров-победителей; детали зафиксированы в `app/questions_european_championship_currentness_review.md`.

---
*Последнее обновление: 14.05.2026 (Koin DI phase 1 зафиксирован; MVVM checkpoints для StartPage и Profile внедрены и подтверждены debug/release сборками)*
