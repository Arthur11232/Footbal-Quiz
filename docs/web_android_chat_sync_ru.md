# Web / Android Chat Sync

Статус: updated после backend/cPanel deploy.

Дата обновления: 2026-08-04.

Цель документа: держать общий контекст между Android/backend архитектурным чатом и отдельным web-чатом по `footballquiz.club`, чтобы landing, backend API и Android app не расходились в решениях.

Этот документ не является Android implementation plan. Это рабочая синхронизация для web/backend процесса.

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

Текущий backend skeleton:

```text
Football-Quiz-Backend
├── public/
│   ├── index.php
│   └── .htaccess
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
- SSH access cPanel -> GitHub работает.
- Deploy делается через cPanel Git interface: `Update from Remote`, затем `Deploy HEAD Commit`.
- Последний проверенный backend commit: `e473dd334420efb6bc195f501d5d07bf0b46aefb`.
- После deploy `/health` возвращает `200 OK`.

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

Backend пока не использует DB в health endpoint. Подключение MySQL нужно делать следующим этапом, когда появится schema и первый реальный endpoint.

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
- Любой новый web feature, который требует user data, progress, certificates или questions, должен сначала получить backend contract.

## Android Chat Должен Знать

- Web landing уже живёт на `footballquiz.club`.
- cPanel/Git deploy используется для web repo.
- API subdomain уже поднят.
- PHP 8.3 работает.
- Composer доступен на hosting.
- Backend MVP будет PHP + MySQL + Firebase token verification.
- MySQL база и user уже созданы, но реальные secrets остаются вне Git/docs.
- Backend deploy уже работает через cPanel Git deployment.

## Ближайшие Логичные Следующие Шаги

### Для web chat

1. Проверить текущую структуру `Football-Quiz-Web` локально.
2. Убедиться, что landing deploy остаётся отдельным от backend deploy.
3. Добавить developer note или internal README section про API URL, если это нужно web-разработке.
4. Не добавлять Firestore calls в web landing.
5. Любой интерактивный web feature проектировать через backend API.

### Для backend foundation

1. Добавить минимальный router слой для PHP API.
2. Добавить server-side `.env` на cPanel, не в Git.
3. Подготовить Composer dependency для Firebase token verification.
4. Подготовить MySQL schema draft.
5. Добавить первый DB-backed endpoint, например `/v1/content/versions` или `/v1/questions`.
6. Добавить basic error response format.
7. После каждого backend deploy проверять `/health`.

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
