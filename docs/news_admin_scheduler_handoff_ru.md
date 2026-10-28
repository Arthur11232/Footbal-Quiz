# Football Quiz News — Admin Panel и Scheduler Handoff

Дата фиксации: `2026-10-26`

Статус: `решения согласованы; реализация admin panel и scheduler ещё не начата`

Область работы:

- `/Users/Arthu/Documents/GitHub/Football-Quiz-Backend`;
- `/Users/Arthu/Documents/GitHub/Football Quiz Web`;
- Android-приложение `/Users/Arthu/Documents/GitHub/Footbal-Quiz` в эту фазу не изменять.

Этот документ является стартовой точкой для нового чата по News Admin Panel и News Scheduler. Перед реализацией также прочитать:

1. Backend: `PROJECT_CONTEXT.md` и `docs/news_source_verification_2026_10_21_ru.md`;
2. Web: `PROJECT_CONTEXT.md` и `docs/news_feature_architecture_ru.md`;
3. общий sync: `/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/web_android_chat_sync_ru.md`.

Некоторые старые документы содержат прежние branch names и состояние до staging deployment. Фактическое состояние ниже имеет приоритет для этой фазы.

## 1. Что уже существует

### Backend

Репозиторий:

```text
/Users/Arthu/Documents/GitHub/Football-Quiz-Backend
```

Актуальная ветка:

```text
codex/news-api-staging-deploy
```

Текущий HEAD на момент фиксации:

```text
ba7a54c Configure cPanel for Backend staging
```

Реализованы public read endpoints:

```http
GET /v1/news
GET /v1/news/clubs
GET /v1/news/competitions
GET /v1/news/{slug}
```

Backend/MySQL является единственным источником истины для News. Реализованы статьи, переводы `en/ru/hy`, источники, клубы, категории, соревнования, фильтры и cursor pagination.

Текущий ручной импорт:

```text
bin/import_news.php
database/news_catalog_2026_10_07.json
```

CLI работает безопасно: по умолчанию выполняется dry-run, запись разрешена только через явный `--commit`.

### Web

Репозиторий:

```text
/Users/Arthu/Documents/GitHub/Football Quiz Web
```

Актуальная ветка:

```text
codex/news-web-staging-deploy
```

Текущий HEAD на момент фиксации:

```text
828500e feat(news): expand article visuals and detail pages
```

Этот commit отправлен в origin и развёрнут на staging:

```text
https://staging.footballquiz.club/en/news/
```

Staging использует:

```text
https://api-staging.footballquiz.club
```

Подтверждено:

- News list и article detail работают;
- карточка статьи целиком кликабельна;
- добавлены разные category/league backgrounds и article hero visuals;
- работают EN/RU/HY, категории, пять Top 5 leagues и club filters;
- staging закрыт от индексации через `X-Robots-Tag: noindex, nofollow, noarchive`;
- production deployment News ещё не выполнялся.

### Текущий редакционный каталог

В staging импортированы:

```text
6 статей
18 переводов EN/RU/HY
7 официальных source URLs
6 клубов
5 Top 5 competitions
```

Материалы основаны на официальных сайтах:

- Real Madrid;
- FC Bayern;
- FC Barcelona;
- Arsenal;
- Inter;
- Paris Saint-Germain.

Сейчас новости выбираются, проверяются, пересказываются, переводятся и импортируются вручную. Scheduler ещё не существует.

## 2. Согласованное продуктовое решение

Порядок реализации зафиксирован так:

```text
1. Закрытая News Admin Panel
2. Ручное создание, редактирование, preview и публикация через неё
3. News Scheduler для поиска кандидатов
4. Review найденных кандидатов в той же Admin Panel
5. Только ручное подтверждение публикации
```

Scheduler не должен публиковать материалы автоматически. Он создаёт только кандидатов/черновики.

Старые опубликованные статьи сохраняются. Новый запуск cron:

- не удаляет старые статьи;
- не перезаписывает опубликованный текст;
- не меняет slug уже опубликованной статьи;
- не снимает статью с публикации;
- не обновляет перевод без подтверждения редактора.

## 3. Целевая редакционная схема

```text
Official source
    -> Scheduler discovery
        -> Candidate
            -> Draft
                -> Human review
                    -> Approved
                        -> Published article
```

Базовые статусы кандидата:

```text
new
draft
in_review
approved
rejected
duplicate
imported
```

Базовые статусы публичной статьи:

```text
draft
review
published
unpublished
archived
```

`archived` предпочтительнее физического удаления опубликованной статьи. Permanent URL опубликованного материала должен оставаться стабильным.

## 4. Admin Panel — первая обязательная фаза

### 4.1 Назначение

Админ-панель должна позволять сначала публиковать полностью ручные материалы, а позже — обрабатывать найденные scheduler кандидаты. Scheduler нельзя запускать в write-режиме до готовности этого review workflow.

Целевая закрытая поверхность:

```text
https://admin.footballquiz.club/news
```

Поддомен и deployment ещё не созданы. Для первой реализации допустим локальный `/admin/news`, но production/staging admin UI не должен быть публично доступен без авторизации.

### 4.2 Авторизация

Рекомендуемая основа:

- Firebase ID Token;
- серверная проверка токена;
- отдельный admin claim или строгий UID allowlist;
- запрет доверять роли, присланной браузером;
- audit log для каждого изменения и публикации.

До выбора окончательной схемы нельзя открывать write endpoints наружу.

### 4.3 Экран списка

Администратор должен видеть отдельные вкладки/фильтры:

```text
Кандидаты
Черновики
На проверке
Опубликованные
Отклонённые
Архив
```

Для каждой записи показывать:

- статус;
- исходный publisher;
- прямую source URL;
- дату публикации источника;
- дату обнаружения;
- исходный заголовок;
- предложенную primary category;
- competition и club;
- наличие EN/RU/HY;
- предупреждения validator;
- кто и когда последний раз изменил запись.

### 4.4 Экран редактора

Обязательные поля и действия:

- публичный slug с проверкой уникальности;
- title, summary, body для `en`, `ru`, `hy`;
- news type;
- primary category;
- competition и club links;
- event date и source publication date отдельно;
- один или несколько источников;
- hero image только через разрешённый media workflow;
- alt, credit и internal rights note;
- SEO title/description;
- preview каждой локали до публикации;
- `Сохранить черновик`;
- `Передать на проверку`;
- `Опубликовать`;
- `Отклонить`;
- `Снять с публикации`;
- `Архивировать`;
- `Открыть источник`.

### 4.5 Серверная публикация

Кнопка `Опубликовать` вызывает отдельный backend action. Backend повторно проверяет:

- авторизацию и admin permission;
- допустимый status transition;
- уникальность `article_id` и slug;
- обязательные EN/RU/HY поля для первого публичного запуска;
- минимум один валидный `http/https` источник;
- наличие primary category;
- совместимость category и competition;
- даты;
- Markdown allowlist;
- image rights fields, если используется изображение.

После успешной публикации сохраняются:

```text
published_at
published_by
reviewed_at
reviewed_by
revision number
```

Браузер не может напрямую присвоить статье `published` через произвольный PATCH.

## 5. Категории: новое согласованное правило

Текущая БД поддерживает many-to-many article↔category. Это можно сохранить для дополнительных связей, но UI/API должны получить понятие одной основной категории — `primary_category`.

Правило:

- `top-5` — клубные новости и новости национальных Top 5 leagues;
- `uefa` — только материалы, где турнир/решение/событие UEFA является основной темой;
- `world-cup` — только чемпионат мира и его квалификация;
- простое упоминание Лиги чемпионов в клубном расписании не делает статью UEFA-материалом;
- `All news` является UI state, а не категорией в БД;
- `Versus` остаётся отложенным.

Нужно выбрать и реализовать один из совместимых вариантов:

1. добавить `primary_category_id` в `news_articles` и `news_candidates`, сохранив текущую many-to-many таблицу для secondary associations;
2. добавить role `primary/secondary` в article↔category relation.

Для MVP предпочтителен явный `primary_category_id`: проще валидировать, фильтровать и объяснять в Admin Panel.

Важно: это правило согласовано после первого staging import и ещё не внедрено. Перед production нужно пересмотреть текущие mixed Top 5/UEFA связи у schedule-материалов Real Madrid, Bayern и Inter.

## 6. Scheduler — только после Admin Panel

### 6.1 Что делает scheduler

```text
cPanel Cron
    -> запускает PHP CLI
        -> читает активный registry официальных источников
            -> получает новые ссылки и metadata
                -> нормализует URL
                    -> удаляет дубли
                        -> классифицирует кандидата
                            -> записывает news_candidate
                                -> показывает его в Admin Panel
```

Scheduler не копирует полный текст чужой статьи и не hotlink-ит изображения. Для discovery достаточно хранить:

- publisher;
- source URL;
- source published time;
- исходный title;
- доступный официальный excerpt/metadata;
- content fingerprint;
- HTTP metadata (`ETag`, `Last-Modified`), если источник их предоставляет;
- время обнаружения;
- результат классификации;
- причину warnings/errors.

### 6.2 Разрешённые источники

Стартовать только с проверяемых официальных источников:

- официальные news pages клубов;
- официальные сайты Premier League, La Liga, Serie A, Bundesliga и Ligue 1;
- UEFA для UEFA category;
- FIFA для World Cup category.

Предпочтение:

```text
официальный RSS/Atom/API
    -> официальный sitemap/feed
        -> ограниченный adapter официальной news page
```

Не использовать на старте:

- сомнительные агрегаторы;
- social reposts как единственный источник;
- слухи;
- автоматическое копирование body;
- изображения без подтверждённых прав;
- автоматическую AI-публикацию.

### 6.3 Предлагаемые таблицы

#### `news_source_registry`

```text
id
source_key
publisher_name
source_type
feed_url
base_url
default_primary_category_id
default_competition_id NULL
default_club_id NULL
trust_level
fetch_interval_minutes
is_active
last_checked_at
last_success_at
last_error_at
created_at
updated_at
```

#### `news_candidates`

```text
id
candidate_id
source_registry_id
source_url
normalized_source_url
source_published_at
source_updated_at NULL
raw_title
raw_excerpt NULL
content_fingerprint
primary_category_id NULL
competition_id NULL
club_id NULL
status
classification_confidence NULL
warning_text NULL
first_seen_at
last_seen_at
reviewed_at NULL
reviewed_by NULL
imported_article_id NULL
created_at
updated_at
```

Обязательные уникальные ограничения:

```text
UNIQUE(normalized_source_url)
UNIQUE(source_registry_id, content_fingerprint)
```

#### `news_fetch_runs`

```text
id
run_id
started_at
finished_at NULL
status
sources_checked
items_found
candidates_created
duplicates_skipped
items_updated
errors_count
error_summary NULL
```

#### `news_article_revisions`

Хранит immutable snapshot важных редакционных полей при публикации и существенном обновлении:

```text
id
article_db_id
revision_number
snapshot_json
change_note
created_by
created_at
```

### 6.4 Дедупликация и сохранение истории

При каждом cron run:

1. URL нормализуется: scheme/host case, tracking query, fragment и безопасно определённые лишние параметры удаляются;
2. сначала проверяется `normalized_source_url`;
3. затем проверяется fingerprint по publisher/title/date и доступным metadata;
4. существующий кандидат получает только `last_seen_at` и технические source metadata;
5. новая source publication создаёт новый candidate;
6. опубликованная статья никогда не перезаписывается cron-процессом.

Если официальный источник изменился:

```text
source changed -> candidate/source alert -> manual comparison -> editor decision
```

Доступные решения редактора:

- оставить опубликованную статью без изменений;
- обновить draft;
- создать новую revision опубликованной статьи;
- добавить correction note;
- проигнорировать несущественное изменение.

### 6.5 CLI и cron

Предлагаемая команда:

```bash
php bin/discover_news.php
```

Без аргумента запись не выполняется. Для создания кандидатов:

```bash
php bin/discover_news.php --commit
```

Даже с `--commit` команда имеет право записывать только candidates/fetch runs, но не `published` статьи.

После local/staging проверки cPanel Cron может запускать команду каждые `30–60` минут. Финальный интервал задаётся отдельно для каждого источника в registry.

Обязательные operational safeguards:

- DB/advisory lock против параллельных запусков;
- HTTP timeout;
- ограниченные retries с backoff;
- понятный User-Agent Football Quiz;
- rate limiting;
- `If-None-Match` / `If-Modified-Since`, если поддерживаются;
- fail-closed при ошибке parser/validator;
- лог без secrets и полного HTML body;
- отдельный fetch run summary;
- одна ошибка источника не должна останавливать остальные источники.

## 7. Предлагаемый Admin API

Существующий public API остаётся read-only и не требует auth.

Минимальный защищённый API:

```http
GET    /v1/admin/news/candidates
GET    /v1/admin/news/candidates/{candidate_id}
POST   /v1/admin/news/candidates/{candidate_id}/create-draft
POST   /v1/admin/news/candidates/{candidate_id}/reject

GET    /v1/admin/news/articles
POST   /v1/admin/news/articles
GET    /v1/admin/news/articles/{article_id}
PATCH  /v1/admin/news/articles/{article_id}
PUT    /v1/admin/news/articles/{article_id}/translations/{locale}
POST   /v1/admin/news/articles/{article_id}/submit-review
POST   /v1/admin/news/articles/{article_id}/publish
POST   /v1/admin/news/articles/{article_id}/unpublish
POST   /v1/admin/news/articles/{article_id}/archive
GET    /v1/admin/news/articles/{article_id}/revisions

GET    /v1/admin/news/sources
POST   /v1/admin/news/sources
PATCH  /v1/admin/news/sources/{source_id}
GET    /v1/admin/news/fetch-runs
```

Не обязательно реализовывать всё одним commit. Первый slice должен обслуживать ручную публикацию без scheduler.

## 8. Рекомендуемый пошаговый план

### Phase A — Admin foundation

1. Зафиксировать auth/admin permission model.
2. Добавить additive migrations для audit/revisions и primary category.
3. Реализовать status transition policy и server-side validator.
4. Реализовать ручной CRUD draft + EN/RU/HY translations.
5. Реализовать preview и explicit publish action.
6. Покрыть unauthorized/forbidden, validation, publish и revision tests.
7. Локально опубликовать тестовую статью через Admin Panel.

### Phase B — Admin staging

1. Подготовить отдельный staging admin surface.
2. Включить noindex и строгую auth boundary.
3. Проверить publish/unpublish/archive на staging.
4. Убедиться, что public API показывает только валидные published translations.
5. Проверить, что questions/scoring/legal не затронуты.

### Phase C — Scheduler discovery

1. Добавить source registry, candidates и fetch runs migrations.
2. Реализовать один официальный source adapter.
3. Реализовать URL normalization, fingerprint и idempotency.
4. Подключить candidates list/detail в Admin Panel.
5. Проверить повторный запуск: `0` новых дублей.
6. Добавить остальные источники по одному.
7. Только после staging soak включить cPanel Cron.

### Phase D — Draft assistance

Позже можно добавить подготовку собственного summary и переводов как draft assistance. Любой автоматически созданный текст остаётся `draft` и требует human review. Автоматическая публикация внешних футбольных новостей не входит в план.

## 9. Тесты и критерии готовности

### Admin Panel MVP готов, если

- без auth невозможно прочитать или изменить admin data;
- не-admin получает `403`;
- draft можно создать вручную;
- EN/RU/HY можно сохранить и preview-ить;
- publish невозможен без источника и обязательных полей;
- после publish статья видна в public API и Web;
- unpublish/archive не удаляет revision history;
- audit содержит actor и timestamp;
- повторное нажатие publish не создаёт дубликат.

### Scheduler MVP готов, если

- повторный запуск не создаёт повторных candidates;
- старые статьи не изменяются;
- изменённый источник создаёт review alert, а не silent overwrite;
- scheduler failure не влияет на public News API;
- одна ошибка источника не ломает весь run;
- cron не может публиковать статью;
- admin видит source, дату, category/competition/club suggestion и warnings;
- все записи используют UTC внутри backend и ISO 8601 в API.

## 10. Hosting/staging ограничения

На текущем cPanel-тарифе доступна только одна MySQL database (`1/1`). Staging временно использует production DB credentials, но разрешены только additive `news_*` migrations.

До отдельного решения запрещено:

- менять или удалять questions/scoring/legal tables;
- выполнять destructive migration;
- включать scheduler сразу в production;
- помещать secrets в repo/docs/log output;
- разворачивать Admin Panel без auth;
- выполнять production deployment без отдельного подтверждения владельца.

При расширении тарифа staging нужно перенести на отдельные database и DB user.

## 11. Что не делать в первом slice

- не начинать scheduler раньше ручной Admin Panel;
- не публиковать автоматически;
- не копировать чужие статьи полностью;
- не скачивать и не публиковать чужие изображения без rights workflow;
- не строить сложную AI-agent систему;
- не добавлять комментарии, рейтинги и персональную ленту;
- не менять Android app;
- не смешивать Admin Panel работу с production deployment.

## 12. Первый следующий шаг для нового чата

Начать с Backend Phase A и небольшого implementation plan:

```text
Admin auth boundary
    -> additive schema
        -> manual article draft API
            -> EN/RU/HY editor
                -> preview
                    -> explicit publish
                        -> audit/revision
```

Перед изменением файлов проверить актуальные `git status`, branch и HEAD обоих репозиториев. Не делать commit, push, migration на сервере или deployment без отдельного указания владельца.

## 13. Актуальный implementation checkpoint — 2026-10-28

Этот раздел имеет приоритет над исходным статусом документа от `2026-10-26`.

### Backend

- ветка: `codex/news-api-staging-deploy`;
- актуальный HEAD: `3e8593e Add safe News primary category backfill`;
- реализована Firebase ID Token проверка и строгий `NEWS_ADMIN_UIDS` allowlist;
- применена additive admin migration с `primary_category_id`, audit log и revisions;
- работают защищённые session, list, create, read, update и delete-draft endpoints;
- работают actions `submit-review`, `return-draft`, `publish`, `unpublish`, `archive`;
- publish и republish создают immutable revision, изменения пишутся в audit log;
- добавлен защищённый taxonomy endpoint для активных competitions и clubs;
- draft update сохраняет article↔competition и article↔club links и проверяет совместимость competition с primary category;
- добавлена dry-run-first CLI-команда для backfill основной категории с транзакцией и audit log;
- PHPUnit: `85` tests / `168` assertions.

### Web Admin

- ветка: `codex/news-web-staging-deploy`;
- актуальный HEAD: `0b68e71 Add competition and club selectors to News admin`;
- staging surface: `https://staging.footballquiz.club/admin/news/`;
- включены `noindex`, Firebase login и backend permission check;
- работают создание/редактирование/удаление draft, EN/RU/HY editor, preview, review и explicit publish;
- native browser confirm заменён на reusable `DialogManager`;
- работают unpublish, return-to-draft, republish и archive controls;
- редактор умеет выбирать competition и несколько clubs.

### Проверенный staging workflow

Через Admin Panel создана и опубликована статья:

```text
real-madrid-wins-clasico-barcelona-2-1
```

Проверено:

- EN/RU/HY доступны по стабильному URL;
- статья была снята с публикации, возвращена в draft, обновлена и опубликована повторно;
- к статье привязаны `La Liga`, `Real Madrid` и `FC Barcelona`;
- public News показывает competition/club tags;
- club counts для Real Madrid и Barcelona увеличились с `1` до `2`;
- карточка получила отдельный web visual `2:1`;
- browser console errors отсутствуют.

### Что ещё не завершено

- production News/Admin deployment не выполнялся;
- scheduler discovery ещё не начат;
- cPanel Cron не настроен;
- archive action реализован и развёрнут, но destructive staging-проверка на реальной опубликованной статье сознательно не выполнялась;
- `2026-10-28` backfill завершён: шесть legacy-статей получили `primary_category_id` категории `top-5`, созданы audit-записи; контрольный dry-run вернул `candidate_count: 0`, `unresolved: []`;
- отдельный admin subdomain пока не создан: используется закрытый staging path.

Следующий безопасный шаг: переходить к Phase C — source registry, candidates, fetch runs и одному официальному discovery adapter без автопубликации.
