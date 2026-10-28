# Task: iOS Auth and Mobile Sync

Статус: Checkpoint 0–4 собраны. Ручная проверка Checkpoint 3 подтверждена пользователем 2026-10-05; local restore, certificates и backup/export подтверждены 2026-10-07; первый controlled stats upload с transaction/read-back/post-write backup подтверждён 2026-10-11. 2026-10-17 создан отдельный Firebase Dev project `football-quiz-dev`: Google Sign-In включён, Firestore создан в `nam5`, опубликованы rules с доступом только к `users/{own UID}` и его `certificates`. 2026-10-27 пользователь подтвердил device test новой Dev-среды. Cloud write доступен только в Debug/dev и дополнительно проверяет Firebase project ID.

## Цель

Подключить iOS-клиент к тому же Firebase-проекту и аккаунту, что и Android, а затем безопасно синхронизировать прогресс и сертификаты между устройствами. Сохранить существующее поведение Android и не переносить Firebase, Realm или SwiftUI в `:shared/commonMain`.

Это отдельная задача после KMP foundation. Сейчас работа ведётся на `codex/multiplatform-foundation`; merge и создание новой ветки не выполнять автоматически. У репозитория были две локальные копии: актуальная для этой задачи — `/Users/Arthu/Documents/GitHub/Footbal-Quiz`, а не вложенная `/Users/Arthu/Documents/GitHub/FQ App/Footbal-Quiz`.

## Принятое направление

- Использовать настоящий Google Sign-In + Firebase Authentication на iOS, не временный test login.
- На iOS подключать официальные Apple SDK через Swift Package Manager; платформенный adapter преобразует Firebase user в общую модель. Android продолжает использовать текущий `AuthRepository`.
- Оставить Firestore текущим cloud-источником на этом этапе. Отдельный PHP/MySQL backend, Web и миграция вопросов сюда не входят.
- Все новые cloud-write пути включать только после read-only проверки на тестовом UID и резервной копии соответствующих данных.

Официальные инструкции (проверены 2026-09-24): [Firebase Apple setup](https://firebase.google.com/docs/ios/setup), [Firebase Google Sign-In on Apple](https://firebase.google.com/docs/auth/ios/google-signin), [Google Sign-In on iOS](https://developers.google.com/identity/sign-in/ios/sign-in). Перед реализацией сверить их актуальные требования.

## Контекст текущего кода

- `AuthenticatedUser` находится в `:shared/domain/auth`; Android сохраняет прежний package/import, iOS `AuthSession` преобразует Firebase user в эту же модель.
- `SyncRepositoryImpl.resolveSyncDecision` различает `None`, `AutoRestoreCloud`, `PromptConflict` и `UploadLocal`. Сертификаты скачиваются и объединяются отдельно от выбора прогресса.
- В `StartPageViewModel` отказ от restore запоминается по UID. Повторный вход не должен снова открывать тот же конфликтный диалог и не должен молча заменять или загружать прогресс.
- iOS сейчас является SwiftUI shell с demo progress и рабочим Firebase Auth; реального локального игрового состояния и cloud-клиента там нет.
- Android имеет `dev/qa/prod` flavors, но отдельный Firebase-проект для iOS/dev в репозитории не подтверждён. До первого write нужно явно выбрать безопасную тестовую среду или тестовый UID.

## Checkpoint 0: Конфигурация и безопасность

Текущее состояние: Apple app уже зарегистрировано в `footbal-quiz-1f62a`; bundle ID `com.arthuralexandryan.footballquiz` совпадает с Xcode target и `GoogleService-Info.plist`. Plist находится локально в `iosApp/FootballQuizIos/`, включён в Xcode Resources и игнорируется Git. Firebase Auth `12.19.2` и Google Sign-In `9.2.0` подключены через SPM с `Package.resolved`; URL scheme настроен из `REVERSED_CLIENT_ID`. Google-вход на реальном iPhone подтверждён 2026-09-29.

1. [x] Проверить iOS bundle ID `com.arthuralexandryan.footballquiz` и зарегистрировать именно его как отдельное Apple app в том же Firebase-проекте. Совпадение строки с Android package name не объединяет две платформенные регистрации.
2. [x] Получить соответствующий `GoogleService-Info.plist`, а не Android `google-services.json`. Файл получен, а Google provider подтверждён успешным входом на iPhone.
3. [x] Настроить URL scheme из `REVERSED_CLIENT_ID`, Swift Package Manager зависимости `FirebaseAuth`, `FirebaseCore`, `GoogleSignIn`, требуемый `-ObjC` linker flag и Firebase initialization для SwiftUI lifecycle.
4. [~] Клиентский plist игнорируется Git; при его отсутствии запуск даёт явную ошибку. Для будущей CI-сборки ещё нужно выбрать способ безопасной подачи файла. Service-account key, private key и backend credentials не добавлять в репозиторий.
5. Зафиксировать тестовый Google-аккаунт и проверку Firestore rules для путей user stats/certificates. Не менять production rules вслепую.

Гейт: iOS shell по-прежнему собирается, Android не изменён, конфигурация воспроизводима.

## Checkpoint 1: Только iOS Auth

Реализовано локально: `AuthSession` создаёт Firebase credential из Google ID/access tokens, слушает Firebase session, обрабатывает sign-out и отмену account picker; SwiftUI экран показывает UID. Успешный вход, повторный запуск с сохранённой сессией, выход и повторный вход с тем же iOS UID подтверждены на реальном iPhone. Совпадение UID с Android ещё не подтверждено.

1. Создать iOS `AuthAdapter` поверх официальных Google Sign-In и Firebase Auth SDK.
2. Добавить вход, восстановление текущей сессии и выход. На экране показывать UID/email для диагностики, но не токены.
3. Проверить вход тем же Google-аккаунтом на Android и iOS: Firebase UID должен совпасть. Если не совпал, остановиться и проверить Firebase project/provider/client IDs до sync.
4. Обработать отмену выбора аккаунта отдельно от ошибки входа.
5. На этом checkpoint не читать и не писать user stats/certificates.

Ручной тест пользователя перед следующим checkpoint: sign-in, повторный запуск с сохранённой сессией, sign-out, повторный sign-in и совпадение UID с Android.

## Checkpoint 2: Общие auth/sync контракты

Реализовано локально: общая модель аккаунта, чистые summary/decision/resolver, iOS Firebase-user adapter и Android mapping к существующим `SyncRepository.SyncDecision`. Объединение сертификатов осталось независимым и выполняется до решения о прогрессе. Android host, iOS Simulator common tests, Android `prod/qa` unit tests и Xcode Simulator build прошли 2026-09-30. Новый верхний safe-area отступ на iOS ждёт визуальной проверки на iPhone. Cloud read/write на iOS не добавлены.

1. Перенести или адаптировать `AuthenticatedUser` в `:shared` без изменения Android public behavior.
2. Выделить чистую модель `LocalProgressSummary` / `CloudProgressSummary` и `SyncDecision` в `:shared`; не переносить Android `UserStatsDTO`, Realm и Firestore DTO.
3. Перенести существующие четыре решения sync в чистое правило и покрыть common tests: оба пусты, только local, только cloud, одинаковые, конфликт.
4. Привязать Android adapter к этому правилу и прогнать regression tests до iOS cloud-интеграции.
5. Сохранить независимость сертификатов: отказ от restore прогресса не скрывает уже выданные сертификаты.

Гейт: Android UI и его решения о restore/upload не изменились; shared tests проходят на Android и iOS.

Проверено 2026-09-30: `:shared:testAndroidHostTest`, `:shared:iosSimulatorArm64Test`, `:app:testProdDebugUnitTest`, `:app:testQaDebugUnitTest`, `:shared:compileKotlinIosSimulatorArm64` и Debug `xcodebuild` для iOS Simulator прошли. Поля Firebase user на iOS адаптируются к общей модели `AuthenticatedUser`; отдельная локальная `Account` удалена. Это проверка кода, не подтверждение совпадения реального UID с Android.

## Checkpoint 3: iOS local/cloud read-only

Реализовано локально 2026-10-01: Debug/dev (`com.arthuralexandryan.footballquiz.dev`) и Release/prod (`com.arthuralexandryan.footballquiz`) выбирают отдельные игнорируемые plist и URL scheme; обе Simulator-сборки прошли. С 2026-10-17 Debug/dev подключён к отдельному Firebase project `football-quiz-dev`, а Release/prod остаётся на `footbal-quiz-1f62a`. SwiftUI экран читает `users/{uid}` и `users/{uid}/certificates/{stageId}` и показывает server/cache source. Ручная read-only проверка на прежнем backend подтверждена пользователем 2026-10-05; отдельный Dev backend начинается пустым и требует нового device test.

1. [x] Выбрано направление: платформенное локальное хранилище за общим интерфейсом; реализацию отложить до появления реального iOS game state. Demo progress не использовать как реальное состояние.
2. [x] Создан read-only Firestore adapter для user stats и certificates с путями и схемой, совместимыми с Android.
3. [x] Cloud snapshot/сертификаты отображаются отдельно от demo screen; автоматический restore и upload отсутствуют.
4. [~] Код различает cache/server, permission denied, offline/unavailable, пустой документ, неизвестные top-level поля и смену аккаунта; основной ручной сценарий подтверждён пользователем. Отдельные fault-сценарии ещё стоит повторить перед write rollout. ID token и персональные данные в логи не пишутся.

Гейт: пользователь видит только прочитанные данные своего UID; production данные не изменены.

## Checkpoint 4: Safe restore/upload

Статус Checkpoint 4: закрыт для Debug/dev 2026-10-11. iOS сохраняет реальный cloud snapshot отдельно для каждого UID в Application Support с atomic write и file protection; demo progress не используется. Решение вычисляет общий `SyncDecisionResolver` из KMP. Restore изменяет только локальный iOS snapshot. Keep Local ничего не загружает и запоминается по fingerprints конкретных local/cloud snapshot-ов, поэтому новый конфликт снова будет показан. Локальный restore и backup/export проверены пользователем 2026-10-07. Первый строго контролируемый stats-only upload проверен пользователем 2026-10-11: он доступен только в Debug/dev, требует server snapshot и совпадающий backup, повторно сравнивает существование документа и fingerprint внутри Firestore transaction, проверяет текущий Firebase UID и требует отдельного подтверждения пользователя. После commit выполняются server read-back, сохранение подтверждённого snapshot и post-write backup. Certificates этим write-путём не изменяются.

1. [x] Та же матрица решений подключена: empty local + cloud предлагает безопасный local restore; local + empty cloud определяет `UPLOAD_LOCAL`; конфликт требует явного выбора. Controlled dev upload вручную проверен.
2. [x] Конфликт отображается как постоянный action card с явными Restore Cloud и Keep Local. После Keep Local он скрывается только для той же fingerprint-пары и возвращается при изменении любого snapshot.
3. [x] Keep Local не загружает локальный прогресс и не восстанавливает cloud. Controlled upload остаётся отдельным явно подтверждаемым действием.
4. [x] Локальный restore выполняется atomic write и считается успешным только после сохранения файла. Первый upload обновляет `lastSynced`, выполняет stats-only transaction и обновляет локальный payload только после подтверждённого cloud commit и server read-back; ручная проверка на тестовом UID пройдена.
5. [x] Добавлен и вручную проверен pre-write backup текущего user document и certificates: файл защищён локально, содержит hashed UID и fingerprints и доступен для ручного export. После успешного upload создаётся новый post-write backup. Production rollout остаётся отдельным решением.

Гейт: одинаковые пользовательские решения на Android/iOS, без потери прогресса при cancel, сбоях сети и повторном входе.

## Checkpoint 5: Сертификаты

Статус: cloud certificates автоматически объединяются с защищённым локальным хранилищем отдельно для каждого UID. На один stage сохраняется один стабильный сертификат; при несовпадении ID сохраняется самый ранний выданный сертификат и показывается предупреждение. Пустой cloud response, logout, повторный login и progress restore не удаляют локальные сертификаты. Основной сценарий на iPhone подтверждён пользователем; Debug/dev и Release/prod Simulator builds прошли. 2026-10-29 read-only rules probe на реальном iPhone подтвердил `PERMISSION_DENIED` для чужого user document и certificates.

1. [x] Сертификаты читаются и объединяются независимо от выбора progress, с дедупликацией по stage/certificate ID.
2. [x] Ранее выданные сертификаты сохраняются по UID и не удаляются при logout/login или restore; повторный запуск на iPhone проверен.
3. [x] `PERMISSION_DENIED` обрабатывается без ложного успеха. Debug/dev read-only rules probe на реальном iPhone подтвердил, что чужие `users/{uid}` и `certificates` недоступны; записи не выполнялись.
4. [x] iOS показывает отдельные cloud/local read-only списки; email delivery и генерация новых сертификатов отсутствуют.

Гейт: один и тот же UID видит одинаковый набор уже выданных сертификатов на Android и iOS.

## Следующий шаг для нового чата

1. Не смешивать оставшиеся QA/маркетинговые/IDE-файлы с iOS sync. Коммитить только явно относящиеся к задаче файлы.
2. На Android проверить UID того же Google-аккаунта и сравнить с уже подтверждённым iOS UID. На iPhone открыть read-only Cloud snapshot онлайн и offline, проверить данные/сертификаты и permission errors. Верхний отступ главного экрана после правки 2026-09-30 тоже проверить визуально.
3. Device test отдельного Dev Firebase project и UID isolation подтверждёны. Теперь на реальном iPhone проверить offline/unavailable, затем изменение cloud между backup и transaction. Никаких silent restore/upload.
4. Текущий production question source — `app/questions_audit_backup_AFTER_es_de_it_firestore_update_2026_09_11.json`; test-reference для Versus — `docs/ronaldo_vs_messi_questions_answers_en.md` и `docs/real_madrid_vs_barcelona_questions_answers_en.md`.

## Приёмка

- Один Google-аккаунт в одном Firebase-проекте даёт один проверенный UID на Android и iOS.
- Все решения sync совпадают с Android и подтверждены общими тестами.
- iOS умеет читать cloud progress, затем безопасно восстанавливать/загружать его только по согласованным правилам.
- Cancel/Keep Local не приводит к скрытому overwrite; конфликтный диалог не повторяется бесконечно.
- Сертификаты не зависят от restore выбора, не пропадают и не дублируются.
- Android release behavior не меняется; backend/Web/questions остаются вне задачи.
- Каждый checkpoint проверяется пользователем до перехода к следующему и коммитится отдельно после подтверждения.

## Открытые решения перед кодом

1. Отдельный Firebase Dev project `football-quiz-dev` создан и подключён к Debug bundle. Для controlled write сохранены backup, transaction fingerprint check и добавлена проверка project ID. Production write rollout остаётся отдельным будущим решением.
2. Для будущего реального iOS game state выбрано платформенное local storage за общим интерфейсом; конкретную технологию выбрать при реализации game state, не подменяя его demo progress.
3. Отдельный branch создавать после согласования этой задачи; merge foundation пока отложен.
