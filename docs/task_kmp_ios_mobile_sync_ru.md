# Task: iOS Auth and Mobile Sync

Статус: Checkpoint 0 и код Checkpoint 1 собраны; вход Google на реальном iPhone подтверждён. Код Checkpoint 2 реализован и прошёл Android/iOS common tests и Xcode Simulator build. Проверка повторного запуска, sign-out и совпадения UID с Android ещё ожидается. iOS cloud sync не реализован.

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

Реализовано локально: `AuthSession` создаёт Firebase credential из Google ID/access tokens, слушает Firebase session, обрабатывает sign-out и отмену account picker; SwiftUI экран показывает UID. Успешный вход подтверждён на реальном iPhone. Не подтверждены вручную: повторный запуск, выход и совпадение UID с Android. Firestore read/write отсутствуют.

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

1. Согласовать стратегию iOS local persistence до реализации restore. Варианты: платформенное локальное хранилище за общим интерфейсом или общий KMP storage; не использовать demo progress как реальное состояние.
2. Создать read-only Firestore adapter для user stats и certificates с путями и схемой, совместимыми с Android.
3. Отобразить cloud snapshot/сертификаты отдельно от demo screen; не выполнять автоматический restore или upload.
4. Проверить offline, permission denied, пустой документ, устаревшую/неизвестную схему и смену аккаунта. Логи не должны содержать ID token или персональные данные.

Гейт: пользователь видит только прочитанные данные своего UID; production данные не изменены.

## Checkpoint 4: Safe restore/upload

1. Реализовать ту же матрицу решений: пустой local + cloud progress = auto restore; local progress + пустой cloud = upload только после согласованного условия; оба с прогрессом = явный выбор.
2. Диалог конфликта не закрывается нажатием вне окна/Back; явные Restore и Keep Local. После Keep Local повторно не показывать его для того же UID/состояния без нового основания.
3. Keep Local не должен неявно загружать локальный прогресс поверх cloud и не должен неявно восстанавливать cloud. Описать, когда пользователь сможет позже вручную синхронизироваться.
4. Выполнять restore/upload атомарно насколько позволяет схема; не отмечать успешную синхронизацию до подтверждения cloud write.
5. До первого write сделать backup тестового пользователя и прогнать сценарии на тестовом UID, затем отдельно согласовать production rollout.

Гейт: одинаковые пользовательские решения на Android/iOS, без потери прогресса при cancel, сбоях сети и повторном входе.

## Checkpoint 5: Сертификаты

1. Читать и объединять сертификаты независимо от выбора прогресса, с дедупликацией по стабильному stage/certificate ID.
2. Сохранять ранее выданный сертификат при logout/login, reset и restore; не выдавать его повторно из-за demo или cloud merge.
3. Проверить Firestore rules для `users/{uid}/certificates/{stageId}` и обработку `PERMISSION_DENIED` без ложного сообщения об успехе.
4. На iOS пока показывать read-only список/preview; email delivery и генерацию нового сертификата не смешивать с этой задачей.

Гейт: один и тот же UID видит одинаковый набор уже выданных сертификатов на Android и iOS.

## Следующий шаг для нового чата

1. Не смешивать оставшиеся QA/маркетинговые/IDE-файлы с iOS sync. Коммитить только явно относящиеся к задаче файлы.
2. На iPhone проверить: вход сохраняется после закрытия и повторного запуска, sign-out работает, повторный sign-in успешен, UID при одном Google-аккаунте совпадает с Android. Верхний отступ главного экрана после правки 2026-09-30 тоже проверить визуально.
3. До следующего cloud-шагa подтвердить тестовый UID и Firestore rules. Checkpoint 3 начинать только с read-only user stats/certificates и выбранной стратегией локального iOS-хранилища. Никаких silent restore/upload.
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

1. Используем существующий Firebase-проект с тестовым UID или создаём отдельный dev-проект для iOS sync испытаний? Рекомендация: отдельная безопасная dev-среда; если её сейчас нет, начать с Auth/read-only в текущем проекте и не делать write.
2. Выбор iOS local persistence перед Checkpoint 3: платформенное хранилище за интерфейсом или общий KMP storage. Решить после инвентаризации реального game state/DTO, не сейчас.
3. Отдельный branch создавать после согласования этой задачи; merge foundation пока отложен.
