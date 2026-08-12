# Repository Contract Audit For KMP Readiness

Дата: 2026-08-12.

Ветка: `codex/phase1-backend-kmp-compose-readiness`.

## Итог

Repository layer уже стал заметно чище после `AuthenticatedUser` и `CloudProgressSnapshot`, но он ещё не полностью KMP/backend-ready.

Главный вывод: сейчас не нужно создавать KMP module. Сначала надо продолжать маленькими шагами убирать Android/Firebase/Realm/DTO протечки из public contracts и ViewModel/use-case boundaries.

## Audit Table

| Repository | Current boundary | KMP readiness | Backend replaceability | Risk | Next action |
|---|---|---:|---:|---:|---|
| `AuthRepository` | Domain user + Android `Intent` для Google Sign-In | `mostly_ready` | `mostly_ready` | medium | Позже отделить sign-in launcher/intent в Android-only auth gateway |
| `SyncRepository` | Domain `AuthenticatedUser` + `CloudProgressSnapshot` | `mostly_ready` | `mostly_ready` | medium | Добавить delete cloud progress boundary и держать `UserStatsDTO` внутри implementation |
| `UserStatsRepository` | Firebase/data DTO boundary через `UserStatsDTO` | `legacy_boundary` | low | high | Не использовать напрямую из ViewModel; оставить как data implementation detail |
| `QuestionsRepository` | Legacy Firestore/bootstrap sync | `android_only` | low | medium | Оставить до backend switch; не смешивать с canonical catalog |
| `QuestionCatalogRepository` | Pure domain future catalog contract | `ready` | `ready` | low | Использовать как будущий backend questions contract |
| `GameProgressRepository` | Domain progress + Android `Uri` для profile photo | `needs_domain_model` | medium | medium | Позже отделить profile photo storage в Android-only repository |
| `GameCatalogRepository` | Legacy scoreboard/game launch models | `legacy_boundary` | medium | medium | Позже заменить UI/data models на domain selection models |
| `ProgressionRepository` | Domain progression records/snapshot | `mostly_ready` | medium | low | Оставить; persistence implementation остаётся platform-specific |
| `PlaySessionRepository` | Gameplay persistence + legacy question/score models | `high_risk` | low | high | Не трогать большим куском; сначала выделять pure game rules |
| `CertificateRepository` | Domain issued certificate records | `mostly_ready` | `ready` | low | Хороший кандидат для backend implementation |
| `PendingRewardRepository` | Domain pending reward records | `mostly_ready` | `ready` | low | Хороший кандидат для backend-backed claim flow |
| `NotificationRepository` | Android/Firebase device/session preferences | `android_only` | medium | high | Не тащить в KMP; позже выделить domain notification preferences отдельно |
| `SettingsRepository` | SharedPreferences/platform settings | `android_only` | low | medium | Оставить Android-specific, domain settings выделять отдельно при необходимости |

## Главные Протечки

- `AuthRepository.getSignInIntent()` возвращает Android `Intent`.
- `GameProgressRepository.saveProfilePhoto()` принимает Android `Uri`.
- `UserStatsRepository` возвращает и принимает `UserStatsDTO`.
- `GameCatalogRepository` и `PlaySessionRepository` возвращают legacy `models.*` types.
- `NotificationRepository` смешивает device token/session, permission prompt и preferences.

## Что Уже Хорошо

- `SyncRepository.SyncDecision` больше не отдаёт `UserStatsDTO` наружу.
- `StartPageEffect` и `ProfileEffect` уже работают с `CloudProgressSnapshot`.
- `QuestionCatalogRepository` уже спроектирован ближе к backend/KMP contract.
- Certificate/pending reward contracts уже почти полностью domain-oriented.
- `ProgressionRepository` не протаскивает Firebase/Realm types.

## Первый Безопасный Code Slice

Подтверждённый code slice:

1. Добавить в `SyncRepository` метод удаления cloud progress, например `deleteCloudProgress(userId: String): Result<Unit>`. Status: done.
2. В `SyncRepositoryImpl` делегировать его в `UserStatsRepository.deleteStats(userId)`. Status: done.
3. Убрать прямую зависимость `ProfileViewModel` от `UserStatsRepository`. Status: done.
4. Обновить Koin `ViewModelModule`, если constructor изменится. Status: done.
5. Прогнать `./gradlew :app:compileDevDebugKotlin`. Status: done.

Почему это безопасно:

- поведение delete-account flow не меняется;
- `UserStatsRepository` остаётся data/Firebase boundary;
- presentation layer становится чище;
- это продолжает уже начатый sync-boundary cleanup;
- не затрагивает gameplay, Firestore questions, Realm migration или backend switch.

## Следующий Code Slice После Этого

После успешного compile следующий безопасный кандидат:

- отделить Android `Intent` из `AuthRepository` contract;
- либо выделить profile photo storage из `GameProgressRepository`, потому что сейчас он принимает Android `Uri`.

Из двух вариантов более безопасный следующий шаг - `AuthRepository`, потому что он уже почти готов после `AuthenticatedUser`, а `getSignInIntent()` явно Android-only.
