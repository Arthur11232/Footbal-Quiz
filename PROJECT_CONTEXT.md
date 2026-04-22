# Project Context: Football Quiz (Current)

Этот файл служит основным источником контекста для ИИ-помощника. Здесь фиксируется текущее состояние проекта, выполненные задачи и план по модернизации.

## 📌 Обзор проекта
- **Название:** Football Quiz
- **Версия:** 1.0.0
- **Основной стек:** Kotlin, Android SDK (target API 35/Android 15), Single Activity Architecture (SAA), Jetpack Navigation Component, Firebase (Firestore, Auth, Analytics), Realm DB, Coroutines.
- **Цель:** Полная модернизация приложения, переход на SAA, внедрение Google Sign-In и динамическая загрузка контента из Firestore.

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

---

## 🚀 Текущий план модернизации (Backlog)

1.  **Архитектура: ViewModel & Flow**
    - **Задача:** Отказ от `GlobalScope` и переход на архитектурные компоненты (Coroutines, Flow, ViewModel).
    - **Статус:** ⏳ В планах.

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
*Последнее обновление: 22.04.2026 (system bars helper on main screens, legal status re-checked, external and embedded Privacy/Terms synchronized with current app behavior)*
