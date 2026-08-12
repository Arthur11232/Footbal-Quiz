# Web / Android Chat Sync

Статус: updated после backend/cPanel deploy, Android questions alignment audit и правила обязательной синхронизации.

Дата обновления: 2026-08-07.

Цель документа: держать общий контекст между Android/backend архитектурным чатом и отдельным web-чатом по `footballquiz.club`, чтобы landing, backend API и Android app не расходились в решениях.

Этот документ не является Android implementation plan. Это рабочая синхронизация для web/backend процесса.

## Правило Обновления Sync Doc

Каждый раз, когда в web/backend/Android чатах меняется что-то из перечисленного ниже, нужно обновлять этот документ в том же рабочем проходе:

- question catalog / question model;
- файлы вокруг вопросов: `questions_audit`, `questions_structured`, `questions_structured_aligned_candidate`, будущий canonical production import;
- backend API contract;
- content version/import/switch plan;
- web/backend/Android границы ответственности;
- production/deploy факты, которые могут повлиять на дальнейшую разработку.

Цель правила: этот файл должен оставаться чистой синхронизированной памятью между чатами, а не устаревшей заметкой.

## Текущая Картина

Проект разделён на три направления:

- Android app: основной mobile client, сейчас постепенно готовится к backend/KMP/Compose.
- Web landing: публичный сайт `https://footballquiz.club/`.
- Backend API: отдельный PHP/MySQL backend на `https://api.footballquiz.club/`.

Главное правило: web landing не должен превращаться в backend, а Android не должен напрямую подстраиваться под cPanel детали. Общий контракт должен жить в backend API.

## Основной Сайт

- Основной домен: `https://footballquiz.club/`.
- Сайт публично доступен.
- Сайт используется как landing/info page для приложения `Football Quiz`.
- На сайте уже есть ссылки на Privacy Policy / Terms вместо старых GitHub links.
- На сайте есть блок/новость про сертификаты.
- Новость про сертификаты была поднята выше, чтобы пользователь быстрее видел новую product-фичу.
- На сайте добавлялся пример сертификата.
- На сайт добавлялся QR / Play Store linking.
- На вкладку браузера был добавлен favicon/logo.

Play Store link:

```text
https://play.google.com/store/apps/details?id=com.arthuralexandryan.footballquiz
```

## Web Repository

- Web часть живёт в отдельном repository: `Football-Quiz-Web`.
- GitHub URL: `https://github.com/Arthur11232/Football-Quiz-Web`.
- Локальный sibling path: `/Users/Arthu/Documents/GitHub/Football Quiz Web`.
- Web repo отделён от Android repo. Это правильно: Android, landing и backend будут жить разными темпами.
- В cPanel подключён Git deployment для web repo.
- Для cPanel deploy был добавлен `.cpanel.yml`.
- После deploy изменения сначала могли быть не видны из-за browser/cache, но incognito показывал актуальную версию.

Известные файлы web repo:

- `index.html`
- `style.css`
- `main.js`
- `translations.js`
- `privacy-policy.html`
- `privacy_policy.md`
- `terms-and-conditions.html`
- `terms_and_conditions.md`
- `data-deletion.html`
- `data_deletion.md`
- `README.md`
- `football_quiz_icon.png`
- `football_quiz_play_qr.png`

Эти файлы относятся к landing части, а не к backend API.

## Hosting / cPanel

- Hosting provider: HayHost.
- Используется cPanel.
- Account home path в cPanel: `/home/ixvldavx`.
- Основной web root: `/home/ixvldavx/public_html`.
- API document root: `/home/ixvldavx/public_html/api`.
- Web repo на сервере: `/home/ixvldavx/repositories/Football-Quiz-Web`.
- Backend repo на сервере: `/home/ixvldavx/repositories/Football-Quiz-Backend`.
- `footballquiz.club` и `api.footballquiz.club` обслуживаются через Apache.
- cPanel поддерживает Terminal/SSH.
- HayHost поддерживает PHP 8.3 и Composer через cPanel Terminal/SSH.

## API / Backend Foundation

### API subdomain

- API URL: `https://api.footballquiz.club/`.
- Health endpoint: `https://api.footballquiz.club/health`.
- DNS работает: `api.footballquiz.club -> 2.56.204.6`.
- HTTPS работает.
- PHP версия на сервере: `8.3.31`.
- Проверка после deploy: `HTTP/2 200`.

Текущий health response:

```json
{
  "status": "ok",
  "service": "Football Quiz API",
  "version": "0.1.0"
}
```

Важно: Chromium navigation однажды показал `net::ERR_BLOCKED_BY_CLIENT`, но server-side `curl` из cPanel успешно вернул `200 OK`. Это похоже на client/browser extension issue, а не на backend failure.

### Legal API

Принято решение добавить публичный read-only Legal API для Android/Web clients.

Подготовлены и deployed endpoints:

```text
GET /v1/legal/documents
GET /v1/legal/privacy-policy
GET /v1/legal/privacy-policy?locale=ru
GET /v1/legal/terms
```

Правила:

- публичные HTML страницы на `footballquiz.club` остаются для Play Store, SEO и обычных пользователей;
- backend Legal API нужен для клиентов, чтобы получать актуальные Privacy/Terms через единый API contract;
- первый этап read-only и без auth;
- user acceptance/status endpoints пока не добавляются;
- legal markdown sources добавлены в backend repo в `content/legal/`;
- localization file-based: `privacy-policy.{locale}.md` и `terms.{locale}.md`;
- сейчас доступен только `en`, запросы `ru/hy` должны возвращать fallback на `en`;
- когда будут добавлены `privacy-policy.ru.md`, `privacy-policy.hy.md`, `terms.ru.md`, `terms.hy.md`, backend должен начать отдавать эти locale без изменения API contract;
- deployed на сервер `2026-08-11`;
- backend commit: `246765e Add canonical questions and legal API`;
- live checks:
  - `/v1/legal/documents?locale=hy` -> `success=true`, `documents=2`, fallback на `en`;
  - `/v1/legal/terms?locale=ru` -> `success=true`, `locale=en`, `fallback_locale=en`;
  - `/v1/legal/privacy-policy?locale=ru` -> `success=true`, `locale=en`, `fallback_locale=en`;
  - `/v1/legal/terms-and-conditions` как API alias не используется и возвращает `404`.

### Scoring Rules API

Принято решение сохранить главный смысл игры как offline-first/offline gaming.

Backend не должен быть обязательным online-судьёй каждого ответа. Android/Web могут считать score локально, а backend должен отдавать только стабильные scoring rules и score boundaries для web application / будущих клиентов.

Локально подготовлен endpoint:

```text
GET /v1/scoring/rules
```

Назначение:

- публичный read-only endpoint;
- без auth;
- не сохраняет user score;
- не проверяет отдельные ответы пользователя;
- не ломает Android offline-first gameplay;
- отдаёт `content_version`, `rules_version`, scoring model, max score по категориям и unlock boundaries;
- `max_score` считается от active backend question catalog;
- `points_per_correct_answer = 1`;
- `points_per_wrong_answer = 0`.

Текущие unlock boundaries перенесены из Android logic:

```text
TOP5       -> initially unlocked
UEFA       -> TOP5 score >= 150
WORLD_CUP  -> TOP5 + UEFA score >= 210
RON_MES    -> overall score >= 240
REAL_BARCE -> overall score >= 310
```

Важно: это rules/catalog endpoint для web, а не backend-owned score/progress endpoint.

### Backend repository

Backend теперь отдельный private repository:

```text
Football-Quiz-Backend
```

GitHub URL:

```text
https://github.com/Arthur11232/Football-Quiz-Backend
```

Почему отдельно:

- landing site остаётся чистым web project;
- Android app остаётся Android project;
- backend/API/admin живут отдельно;
- проще деплоить на cPanel;
- проще позже мигрировать на другой stack;
- легче контролировать secrets, `.env`, DB credentials и Firebase service account.

Текущая backend структура:

```text
Football-Quiz-Backend
├── public/
│   ├── index.php
│   └── .htaccess
├── src/
│   ├── Application.php
│   ├── Auth/
│   ├── Config/
│   ├── Content/
│   ├── Database/
│   ├── Http/
│   ├── Questions/
│   └── Support/
├── database/
│   ├── schema.sql
│   ├── seed_initial.sql
│   └── seed_questions_2026_08_001.sql
├── composer.json
├── .cpanel.yml
├── .env.example
├── .gitignore
└── README.md
```

Текущий `.cpanel.yml` копирует только public-файлы:

```yaml
---
deployment:
  tasks:
    - /bin/cp public/index.php /home/ixvldavx/public_html/api/index.php
    - /bin/cp public/.htaccess /home/ixvldavx/public_html/api/.htaccess
```

Это важно: `.env`, source code, private config и service account не должны попадать в `public_html`.

### cPanel Git deployment

- Backend repo клонирован на сервер в `/home/ixvldavx/repositories/Football-Quiz-Backend`.
- cPanel Git Version Control зарегистрировал backend repo.
- Обычный deploy-path может быть через cPanel Git interface: `Update from Remote`, затем `Deploy HEAD Commit`.
- Последний deploy `2026-08-11` был сделан через cPanel Terminal archive transfer, потому что локальный GitHub push не был авторизован из CLI.
- Deployed backend content source: local commit `246765e Add canonical questions and legal API`.
- Важно: из-за отсутствия GitHub CLI auth этот deploy обновил server working tree через archive transfer, но GitHub remote/server `.git` history может оставаться позади до отдельного GitHub Desktop push + server Git reconciliation.
- После deploy `/health` возвращает `200 OK`.
- Последний проверенный server-side API state включает `/health`, `/v1/content/active`, `/v1/questions`, `/v1/legal/privacy-policy`, `/v1/legal/terms`.
- Перед следующим deploy нужно сверить локальный backend commit, GitHub remote и server repo, потому что часть работы теперь выполняется локально, а не напрямую через GitHub connector.

Осталось отдельно почистить старые/ошибочные read-only deploy keys и временный public-key файл на сервере, если пользователь явно подтвердит cleanup. Это не блокирует текущий backend deploy.

## MySQL Foundation

В cPanel создана MySQL база и пользователь:

```text
Database: ixvldavx_fq_db
User: ixvldavx_fq_api
```

Правила:

- пароль не хранить в Git;
- пароль не вставлять в docs;
- реальные credentials должны жить только в server-side `.env`;
- `.env.example` должен показывать форму переменных без secret values;
- DB user должен иметь privileges только на нужную DB.

MySQL уже подключён к backend через server-side private env.

Уже сделано:

- schema импортирована в `ixvldavx_fq_db`;
- active content seed `2026.08.001` импортирован;
- `/v1/content/active` возвращает DB-backed active content version;
- `/v1/questions` существует и возвращает questions catalog envelope, но текущая DB schema ещё использует старую модель `answer_key`/translations и не готова для полного canonical import.

Следующий DB шаг: обновить schema под canonical question/options model с `correct_option_key`.

## Backend Stack Decision

Для первого backend MVP выбрано:

- PHP 8.x;
- MySQL;
- cPanel/HayHost;
- Composer;
- Firebase Auth как identity provider на старте.

Причина:

- VPS/Ktor пока не нужен для MVP;
- PHP + MySQL на текущем hosting достаточно для первого production backend;
- позже можно мигрировать backend на Ktor/VPS, если проект вырастет.

## Firebase Auth На Backend

План:

- Android/Web/iOS клиент логинится через Firebase Auth;
- клиент получает Firebase ID Token;
- клиент отправляет token в PHP backend:

```http
Authorization: Bearer <firebase_id_token>
```

- PHP backend проверяет token через Composer dependency, предпочтительно `kreait/firebase-php`;
- backend использует Firebase `uid` как стабильный `user_id`;
- backend не хранит user passwords на первом этапе;
- Firebase service account JSON должен храниться вне `public_html`.

Пример безопасного расположения:

```text
/home/ixvldavx/private/firebase-service-account.json
```

## Admin / Content Panel

Admin/content panel нужен, но не первым шагом backend MVP.

Назначение:

- добавлять вопросы;
- редактировать вопросы;
- отключать вопросы;
- проверять `question_id`;
- управлять EN/RU/HY переводами;
- импортировать JSON/CSV;
- валидировать дубликаты;
- управлять `content_version`;
- в будущем управлять новостями сайта, feature flags, policy/terms content.

Безопасность:

- admin не должен быть просто скрытой ссылкой;
- нужен login;
- доступ только по whitelist Firebase UID/email;
- admin routes не должны открываться до появления auth guard.

## Questions Catalog / Content Version

Принято решение: backend должен хранить canonical question catalog.

Важно:

- Firestore может временно оставаться production source для Android;
- backend catalog готовится параллельно;
- реальный switch Android на backend questions будет отдельной migration phase;
- перед настоящим switch нужен force update / minimum supported version.

Минимальный `content_version` формат:

```json
{
  "content_version": "2026.08.001",
  "min_app_version_code": 55,
  "is_active": true,
  "published_at": "2026-08-01T00:00:00Z"
}
```

Правило:

- app version отвечает за код клиента;
- `content_version` отвечает за набор вопросов;
- клиент должен брать активную content version, совместимую с текущей app version.

### Текущие Android question artifacts

В Android repo сейчас есть три важных файла вокруг questions catalog:

```text
app/questions_audit.json
app/questions_audit_fixed_2026_08_001.json
app/questions_structured.json
app/questions_structured_aligned_candidate.json
app/questions_canonical_2026_08_001.json
```

Роли файлов:

- `app/questions_audit.json` - raw export / audit source из текущего Firestore/legacy buckets. Это источник для проверки, но не финальный backend import-файл.
- `app/questions_audit_fixed_2026_08_001.json` - fixed audit в той же legacy Firestore-структуре, но с исправлениями из `app/questions_structured_aligned_candidate.json`. Это основной кандидат для безопасного обновления текущего Firestore без перехода production app на новую canonical model.
- `app/questions_structured.json` - первый structured baseline на schema version 3. В нём 689 вопросов, но только 578 `import_ready`, а 111 остаются `needs_review`. Его нельзя использовать как production import без alignment fixes.
- `app/questions_structured_aligned_candidate.json` - текущий рабочий import-candidate на schema version 4. В нём 689/689 вопросов имеют `import_ready`, missing EN/RU/HY = 0, ошибок `correct_option_key` = 0. Этот файл создан после ручной alignment-работы и является главным чистым синхронизированным источником для дальнейшей backend/web работы.
- `app/questions_canonical_2026_08_001.json` - clean canonical production artifact для backend import. Создан из `app/questions_structured_aligned_candidate.json`; удаляет legacy/review/alignment поля; содержит только контрактную модель вопросов и стабильные смысловые `option_key` / `correct_option_key`.

Текущее фактическое состояние `app/questions_structured_aligned_candidate.json`:

```text
questions: 689
import_ready: 689
spot_check_not_modified: 539
manually_aligned_from_shifted_locales: 54
manually_completed_missing_locale: 96
missing locales: 0
bad correct_option_key: 0
```

Текущее фактическое состояние `app/questions_canonical_2026_08_001.json`:

```text
schema_version: 1
questions: 689
question_translations: 2067
options: 2756
option_translations: 8268
validation errors: 0
option_a/option_b style keys: 0
difficulty: medium for 689 questions
```

Текущее фактическое состояние `app/questions_audit_fixed_2026_08_001.json`:

```text
locales: hy, en, ru
questions per locale: 689
same questionId set across locales: true
right_answer exists in answer_A/B/C/D: true
empty required fields: 0
candidate mapping errors: 0
```

Текущий Firestore backup перед fixed update:

```text
app/questions_audit_backup_2026_08_07_before_fixed_update.json
```

Контрольный Firestore backup после fixed update:

```text
app/questions_audit_backup_2026_08_07_after_fixed_update.json
```

Статус backup/update:

```text
backup before update created: yes
backup before update validation errors: 0
update dry-run prepared docs: 2067
update commit wrote to Firestore: yes
update commit wrote docs: 2067
backup after update created: yes
backup after update matches fixed audit: true
changed records:
  en: 116
  ru: 45
  hy: 22
```

Что было исправлено:

- закрыты 111 старых `needs_review` из `app/questions_structured.json`;
- восстановлены недостающие EN/RU/HY тексты;
- исправлены скрытые сдвиги, где EN вопрос/варианты были от другого вопроса, а RU/HY уже были синхронизированной парой;
- исправлены точечные option-text ошибки вроде неправильного варианта в `world_cup_002`, `italy_059`, `spain_013`, `spain_068`, `germany_067`, `vsrb_025`.

Остаточный content-quality риск:

- есть точные дубли вопросов внутри некоторых тем, например `france_010/france_064`, `france_036/france_041`, `france_054/france_058`, `germany_031/germany_061`, `germany_032/germany_062`, `germany_033/germany_063`, `germany_034/germany_064`, `germany_036/germany_066`, `spain_020/spain_050`;
- это уже не language-alignment ошибка, а решение по качеству каталога: оставить legacy-дубли или заменить один из дублей новым вопросом.

Правило для backend/web чата:

- для backend dry-run/import ориентироваться на `app/questions_structured_aligned_candidate.json`;
- для clean backend import использовать `app/questions_canonical_2026_08_001.json`;
- для прямого Firestore legacy update ориентироваться на `app/questions_audit_fixed_2026_08_001.json`;
- `app/questions_structured.json` считать baseline/audit artifact;
- production artifact должен содержать только контрактные поля: `question_id`, `content_version`, `category`, `topic`, `difficulty`, `question`, `options`, `correct_option_key`, `explanation`;
- перед production switch сделать dry-run import и отдельно решить судьбу точных дублей;
- текущий Firestore production source уже обновлён fixed legacy audit-файлом, но это не является backend/canonical switch.

Firestore backup/update правило:

- backup вопросов не должен выполняться внутри Android app при старте приложения;
- production app не должен иметь прав читать весь question catalog и писать backup-документы;
- backup/update делается только admin/local tool через service account;
- текущий tool: `tools/update_firestore_questions.py`;
- перед любым update запускать `backup`, затем `update --dry-run`, и только потом `update --commit`.

Команды:

```bash
python3 tools/update_firestore_questions.py backup \
  --service-account tools/service-account.local.json \
  --output app/questions_audit_backup_YYYY_MM_DD.json \
  --language-order en,ru,hy \
  --require-question-id

python3 tools/update_firestore_questions.py update \
  --service-account tools/service-account.local.json \
  --audit app/questions_audit_fixed_2026_08_001.json \
  --language-order en,ru,hy \
  --require-question-id \
  --dry-run

python3 tools/update_firestore_questions.py update \
  --service-account tools/service-account.local.json \
  --audit app/questions_audit_fixed_2026_08_001.json \
  --language-order en,ru,hy \
  --require-question-id \
  --commit
```

Принятая canonical question model:

```json
{
  "question_id": "france_001",
  "content_version": "2026.08.001",
  "category": "TOP5",
  "topic": "France",
  "difficulty": "medium",
  "question": {
    "en": "...",
    "ru": "...",
    "hy": "..."
  },
  "options": [
    {
      "option_key": "psg",
      "text": {
        "en": "...",
        "ru": "...",
        "hy": "..."
      }
    }
  ],
  "correct_option_key": "psg",
  "explanation": {}
}
```

Важно:

- backend не должен сравнивать правильный ответ по тексту;
- backend не должен считать `A/B/C/D` из legacy Firestore стабильной истиной;
- клиент может перемешивать `options`, потому что правильность привязана к `correct_option_key`;
- `sort_order` и `is_active` не входят в canonical JSON model для вопросов;
- `difficulty` остаётся в модели, даже если пока всем вопросам временно присвоено `medium`.

## Android Связь С Web / Backend

В Android repo уже началась подготовка к backend:

- создана domain-модель `AuthenticatedUser`;
- `FirebaseUser` убран из основных repository/usecase/ViewModel contracts;
- создан future-only `QuestionCatalogRepository`;
- создан `ContentVersion`;
- начат sync boundary cleanup через `CloudProgressSnapshot`;
- `UserStatsDTO` убирается из presentation/domain sync contracts.

Это важно для Web/backend:

- backend будет говорить в терминах `uid`, progress, certificates, question catalog, content version;
- Android постепенно перестаёт зависеть от Firebase DTO в верхних слоях;
- будущий Web app сможет использовать тот же backend contract;
- web process не должен проектировать отдельную модель progress/certificates, несовместимую с Android.

## Web Chat Должен Знать

- Android сейчас готовится к backend/KMP/Compose маленькими шагами.
- Backend уже создан как отдельный private repo, не внутри web landing repo.
- Web landing не должен смешиваться с backend API.
- `api.footballquiz.club` уже работает как PHP endpoint.
- Future Web app должен использовать backend API, а не напрямую Firestore.
- Любой новый web feature, который требует user data, progress, certificates, questions или legal documents, должен сначала получить backend contract.

## Android Chat Должен Знать

- Web landing уже живёт на `footballquiz.club`.
- cPanel/Git deploy используется для web repo.
- API subdomain уже поднят.
- PHP 8.3 работает.
- Composer доступен на hosting.
- Backend MVP будет PHP + MySQL + Firebase token verification.
- MySQL база и user уже созданы, но реальные secrets остаются вне Git/docs.
- Backend deploy уже работает через cPanel Git deployment.

## Backend First Priority

Первый backend foundation уже частично сделан:

- минимальный router и единый JSON response/error format;
- `/health`;
- Composer/Firebase verification foundation;
- server-side private env;
- MySQL connection;
- базовая schema;
- `/v1/content/active`;
- первый `/v1/questions` endpoint.
- deployed read-only `/v1/legal/...` API для Privacy/Terms.
- локально подготовлен read-only `/v1/scoring/rules` API для offline-first scoring rules / score boundaries.

Новая ближайшая backend priority:

1. Backend canonical schema под `correct_option_key` и отдельные option keys/translations подготовлена и deployed.
2. Backend copy `database/questions_canonical_2026_08_001.json` добавлен и deployed.
3. Importer/dry-run validator `bin/import_questions.php` добавлен и deployed.
4. `GET /v1/questions` repository обновлён под canonical response model и deployed.
5. Read-only Legal API для Privacy/Terms добавлен и deployed.
6. Локально подготовлен read-only Scoring Rules API: `GET /v1/scoring/rules`.
7. Server migration/import выполнены без DB backup по отдельному решению владельца проекта.
8. Следующий backend шаг: либо deploy scoring rules, либо progress/certificates sync endpoints.

Локальные backend проверки после canonical preparation:

```text
questions import dry-run: OK
questions: 689
question_translations: 2067
options: 2756
option_translations: 8268
PHP lint: OK
legal API local route lint: OK
backend content source commit: 246765e Add canonical questions and legal API
deploy/server import: OK, 2026-08-11
live /v1/questions?locale=ru: OK, questions=689, content_version=2026.08.001
live /v1/content/active: OK, content_version=2026.08.001
live /v1/legal/terms?locale=ru: OK, fallback en
live /v1/legal/privacy-policy?locale=ru: OK, fallback en
local /v1/scoring/rules PHP lint: OK
```

Admin panel и полноценный content management нужны, но после стабильного import/API contract.

## Ближайшие Логичные Следующие Шаги

### Для web chat

1. Проверить текущую структуру `Football-Quiz-Web` локально.
2. Убедиться, что landing deploy остаётся отдельным от backend deploy.
3. Добавить developer note или internal README section про API URL, если это нужно web-разработке.
4. Не добавлять Firestore calls в web landing.
5. Любой интерактивный web feature проектировать через backend API.

### Для backend foundation

1. Backend canonical schema/importer/questions API/legal API уже закоммичены локально и deployed на сервер.
2. Server migration выполнена.
3. Server importer dry-run и `--commit` выполнены.
4. Live checks выполнены: `/health`, `/v1/content/active`, `/v1/questions`, `/v1/legal/privacy-policy`, `/v1/legal/terms`.
5. Локально подготовленный `GET /v1/scoring/rules` можно закоммитить/deploy отдельным шагом.
6. Следующий backend этап после scoring rules: progress/certificates sync endpoints.
7. Не делать production switch Android на backend questions без отдельного подтверждения.

### Для Android chat

1. Доделать и закоммитить `Sync boundary cleanup`.
2. Затем перейти к next boundary: profile photo или question catalog mapper.
3. Не подключать Android к backend до отдельного backend MVP и force update plan.
4. Когда backend contract будет готов, добавить API client как отдельный data source, не ломая текущий Firestore production path.

## Важные Ограничения

- Этот документ не должен содержать passwords, private keys, Firebase service account JSON или реальные secret values.
- Web repo, backend repo и Android repo должны оставаться отдельными.
- cPanel details нужны только как operational context, не как часть client architecture.
- Если в web-чате появляются новые решения, их нужно добавлять сюда, чтобы документ оставался единым источником правды.
