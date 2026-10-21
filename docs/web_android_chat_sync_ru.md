# Web / Android Chat Sync

Дата обновления: 2026-10-21.

Этот файл нужен, чтобы разные чаты по Football Quiz не расходились в контексте. Здесь фиксируем только синхронизационную информацию между Android app, web/landing и backend-направлением.

## Текущее состояние Android

- Ветка для следующей работы: `codex/multiplatform-foundation`.
- ES/DE/IT localization уже находится в production.
- Текущая production/release версия: `2.2.0`, `versionCode 58`.
- Готовый AAB: `app/prod/release/Football_Quiz_prod_release_2.2.0_58.aab`.
- SHA-256 AAB: `b6519a7e10397bb842777459af5ad05def1f6d3552dbcc76e859d48940fa351c`.
- Проверки перед release:
  - `./gradlew :app:bundleProdRelease` — успешно;
  - `./gradlew :app:testProdDebugUnitTest` — успешно.
- Play Console developer verification для `com.arthuralexandryan.footballquiz` показывает статус `Registered`.

## Firestore Questions

- Production Firestore `questions` обновлён до 6 языков:
  - `en`;
  - `ru`;
  - `hy`;
  - `es`;
  - `de`;
  - `it`.
- В каждом языке по `689` вопросов.
- Всего в Firestore после update: `4134` документов.
- Диапазоны документов:
  - `en`: `0..688`;
  - `ru`: `689..1377`;
  - `hy`: `1378..2066`;
  - `es`: `2067..2755`;
  - `de`: `2756..3444`;
  - `it`: `3445..4133`.
- Финальный upload-файл:
  - `app/questions_audit_firestore_upload_es_de_it_2026_09_11.json`.
- Backup до update:
  - `app/questions_audit_backup_BEFORE_es_de_it_firestore_update_2026_09_11.json`.
- Backup после update:
  - `app/questions_audit_backup_AFTER_es_de_it_firestore_update_2026_09_11.json`.
- Post-update backup полностью совпадает с final upload-файлом.

## ES / DE / IT Localization

- Spanish, German и Italian добавлены как полноценный localization release, а не маленький patch.
- UI resources добавлены:
  - `app/src/main/res/values-es/strings.xml`;
  - `app/src/main/res/values-de/strings.xml`;
  - `app/src/main/res/values-it/strings.xml`.
- Language picker поддерживает 6 языков: `en`, `hy`, `ru`, `es`, `de`, `it`.
- Все 12 question batches прошли human review.
- Основные review-файлы:
  - `docs/review_all_12_batches_human_review_es_de_it_ru.md`;
  - `docs/es_de_it_translation_draft_review_ru.md`;
  - `docs/task_add_es_de_it_localization_ru.md`.

## Важные правила для следующих чатов

- Не использовать старый `questions_audit.json` как источник истины. Он должен рассматриваться как архив/legacy snapshot.
- Для текущего Firestore состояния ориентироваться на post-update backup от `2026_09_11`.
- Любое следующее изменение вопросов делать через backup -> local edit -> validate-alignment -> dry-run -> explicit commit.
- Не заливать Firestore без свежего backup.
- Не смешивать Android app release и web/backend work в один commit без явной причины.
- Не коммитить секреты и временные файлы:
  - `tools/service-account.local.json`;
  - `upload_certificate.pem`;
  - keystore/secrets;
  - `.DS_Store`;
  - IDE local state.

## Web / Landing

- Основной сайт: `https://footballquiz.club/`.
- На web/landing уже есть legal pages, QR/Play Store flow и news-блок про certificates.
- Отдельная web-работа может вестись в другом чате; Android-чат должен учитывать только опубликованные URL и release-facing тексты.
- Web project context создан:
  - `/Users/Arthu/Documents/GitHub/Football Quiz Web/PROJECT_CONTEXT.md`.
- Landing P0 conversion pass начат на `2026-09-11` по marketing handoff:
  - support email `support@footballquiz.club` создан как mailbox и настроен forwarder;
  - публичные landing/legal contacts переключены на `support@footballquiz.club`;
  - из landing copy убраны устаревшие mentions `2.1.0`;
  - добавлены реальные screenshots/video assets из Android `2.2.0` UI;
  - embedded video player убран из product proof block после visual QA, screenshots открываются в preview/lightbox;
  - hero question screenshot заменён на более короткий вопрос: `Which club won the German Cup the most?`;
  - добавлен OG image `assets/og/football-quiz-1200x630.png`;
  - добавлены SEO/social preview meta и funnel analytics events без PII.
- Локально обновлена типографика hero без изменений Android app:
  - demo `Garlic Sauce` отклонён из-за personal-use-only лицензии и отсутствия RU/HY glyph coverage;
  - EN/RU hero использует self-hosted `Rubik` weight `900` под SIL OFL 1.1;
  - HY hero использует self-hosted `Mardoto Black` под Apache 2.0;
  - font-файлы и лицензии добавлены в Web `assets/fonts/rubik/` и `assets/fonts/mardoto/`;
  - desktop EN/RU/HY и mobile `390×667` проверены: horizontal overflow отсутствует, primary CTA полностью видна;
  - typography checkpoint пока не закоммичен, не отправлен и не задеплоен.
  - эта же языковая схема применяется к главным заголовкам News list/detail: `Rubik 900` для EN/RU и `Mardoto Black` для HY;
  - армянский News на mobile `390×667` дополнительно проверен: заголовок переносится без одиночной последней буквы, horizontal overflow отсутствует.

## Social / Marketing Accounts

- Отдельный статус-док по соцсетям:
  - `docs/marketing/social_accounts_setup_status_ru.md`.
- Facebook Page создана и базово настроена:
  - page name: `Football Quiz`;
  - category: `Video game`;
  - page URL / username: `https://www.facebook.com/footballquiz.club`;
  - website: `https://footballquiz.club/`;
  - support email: `support@footballquiz.club`;
  - profile photo и cover photo загружены.
- Первый Facebook launch post опубликован и закреплён:
  - `https://www.facebook.com/footballquiz.club/posts/pfbid026Ak6CdwJeRW8CC2M8GZevdWKHtJw88DnHmm1CVUA7UYxHgWsCtVWrGBomVf6w8yAl`.
- Первый Facebook gameplay Reel опубликован:
  - `https://www.facebook.com/reel/1837721100930548/`;
  - source MP4: `docs/marketing/tiktok_challenge_02_gameplay/football_quiz_gameplay_reel_v2.mp4`;
  - audience `Public`, ads/boost не включались.
- Instagram `@footballquiz.club` проверен:
  - bio обновлён и содержит `footballquiz.club`;
  - clickable website link добавлен через мобильное приложение Instagram и визуально проверен в web;
  - link target использует `https://footballquiz.club/` с UTM `utm_source=ig&utm_medium=social&utm_content=link_in_bio`.
- Первый Instagram intro post опубликован:
  - `https://www.instagram.com/footballquiz.club/p/DdRYsHMDBT6/`.
- Первый Instagram gameplay Reel опубликован:
  - `https://www.instagram.com/footballquiz.club/reel/DdTl2A_OUDJ/`;
  - source MP4: `docs/marketing/tiktok_challenge_02_gameplay/football_quiz_gameplay_reel_v2.mp4`;
  - Facebook cross-post/share был выключен.
- TikTok `@footballquiz11232` проверен:
  - bio обновлён и подтверждён в mobile app;
  - web может временно показывать старое `Нет описания`;
  - подготовлен первый TikTok/Reel challenge asset pack: `docs/marketing/tiktok_challenge_01/`;
  - финальный MP4 собран через `ffmpeg`: `docs/marketing/tiktok_challenge_01/football_quiz_tiktok_challenge_01.mp4`;
  - MP4 параметры: `1080 x 1920`, `30fps`, `12.0s`, H.264/AAC;
  - подготовлен gameplay-based TikTok/Reel MP4 из screen recording: `docs/marketing/tiktok_challenge_02_gameplay/football_quiz_gameplay_reel_v2.mp4`;
  - gameplay MP4 параметры: `1080 x 1920`, `30fps`, `13.45s`, H.264/AAC;
  - gameplay MP4 опубликован в TikTok: `https://www.tiktok.com/@footballquiz11232/video/7685494419276958984`;
  - privacy: `Все`, ads/boost не включались.
- Любые публичные посты, reels, TikTok videos, LinkedIn posts, boosts/ads или массовые invites делать только после отдельного финального подтверждения.

## Backend Direction

- Глобальное направление: постепенно перейти к собственному backend.
- Предпочтение на первом этапе: PHP + MySQL на текущем hosting/cPanel, потому что это дешевле и проще для старта.
- Firebase Auth пока остаётся identity provider.
- Backend уже выделен в отдельное направление/repository: `Football-Quiz-Backend`.
- API subdomain уже использовался как целевая точка: `https://api.footballquiz.club/`.
- Health endpoint: `https://api.footballquiz.club/health`.
- Backend должен оставаться отдельным от Android app repo и web landing repo.
- Backend в будущем должен хранить:
  - canonical question catalog;
  - content version;
  - sync/progress;
  - certificates;
  - позже admin/content page.
- API лучше держать отдельно от web repo, когда начнётся полноценная backend-разработка.

## News Local Integration — 2026-10-21

- News развивается только в отдельных Web и Backend repositories; Android app не изменялся.
- Backend staging branch: `codex/news-api-staging-deploy`.
- Web staging branch: `codex/news-web-staging-deploy`.
- MySQL/backend остаётся единственным источником истины для новостей.
- Локально реализованы public read routes:
  - `/v1/news`;
  - `/v1/news/clubs`;
  - `/v1/news/competitions`;
  - `/v1/news/{slug}`.
- Локально реализованы PHP-rendered Web News list/detail, club filter, sources, cursor pagination и landing preview.
- Локально реализована category taxonomy `All / Top 5 / UEFA / World Cup`; `Versus` отложен до появления сравнительных материалов.
- `All` не хранится в БД; статьи связаны с `top-5`, `uefa`, `world-cup` через many-to-many и могут находиться сразу в нескольких разделах.
- Внутри `Top 5` локально добавлены Premier League, La Liga, Serie A, Bundesliga и Ligue 1; теперь в каждой лиге есть минимум один реальный материал.
- Article↔competition реализовано many-to-many; каталог содержит материалы Arsenal, Real Madrid, Barcelona, Inter, Bayern и Paris Saint-Germain.
- Category, competition и club filters комбинируются в API и Web; clean routes поддерживают `/{locale}/news/top-5/{competition}/club/{club}/`.
- Выбраны public clean URLs: `/en/news/...`, `/ru/news/...`, `/hy/news/...`.
- На landing локально добавлена видимая кнопка News и блок трёх последних материалов.
- В hero локально добавлен EN/RU/AM анонс нового News feature со ссылкой на соответствующий новостной раздел.
- News links используют рабочий PHP query fallback, затем переключаются на clean routes в production; landing CSS/JS получили version query против старого browser cache.
- Header-кнопка News локально получила контрастный зелёно-золотой стиль; адаптивность проверена на `320`, `360` и `390` px без обрезания и horizontal overflow.
- Технический local fixture сохранён для тестов, но его три записи переведены в `archived` в `football_quiz_local`.
- Подготовлен и импортирован только в локальную БД расширенный реальный каталог: `6` статей, `18` переводов EN/RU/HY, `7` официальных источников и `6` клубов.
- Все шесть материалов перепроверены по официальным сайтам клубов; verification report: Backend `docs/news_source_verification_2026_10_21_ru.md`.
- Тексты написаны специально для Football Quiz; сторонние изображения не используются, UI показывает собственный FQ placeholder.
- Fixture технически запрещено импортировать в database без суффикса `_local`.
- PHPUnit: `48` tests / `108` assertions; catalog dry-run, local API/Web smoke, category + competition + club filters, source links и visual QA пройдены на каталоге из шести материалов.
- На `390×844` все пять лиг видны без horizontal overflow; keyboard Tab navigation между league links проверена.
- На mobile viewport `390×844` список загрузил все `6` карточек без horizontal overflow и без console errors.
- `World Cup` пока корректно показывает пустое состояние; наличие World Cup material не блокирует технический News staging, но раздел нельзя считать контентно готовым до появления reviewed материала.
- Для Web и Backend подготовлены отдельные staging-конфигурации; production deployment не выполнялся.
- `2026-10-21` в cPanel созданы `staging.footballquiz.club` → `/home/ixvldavx/public_html/staging` и `api-staging.footballquiz.club` → `/home/ixvldavx/public_html/api-staging`.
- Оба DNS `A` указывают на `2.56.204.6`, HTTPS отвечает; до deployment пустые docroots ожидаемо возвращают `403`.
- `2026-10-21` создан отдельный `/home/ixvldavx/private/football-quiz-api-staging.env`: права `600`, `APP_ENV=staging`, `APP_DEBUG=false`, CORS только для `https://staging.footballquiz.club`; секреты в вывод и Git не попадали.
- Лимит MySQL тарифа — `1/1`, поэтому владелец подтвердил временное использование production DB для staging. Разрешены только добавочные `news_*` migrations; questions/scoring/legal tables нельзя менять. При расширении тарифа staging нужно вынести в отдельную database/user.
- В cPanel зарегистрированы отдельные server checkouts `Football-Quiz-Backend-Staging` и `Football-Quiz-Web-Staging`.
- Backend staging развернут из commit `ba7a54c`; Web staging после DNS-resolver fix развернут из commit `f225bd9`. Оба commit отправлены в origin.
- На сервере через Composer установлены production dependencies backend; применены только три добавочные News migrations: foundation, categories и competitions.
- Реальный каталог импортирован в staging: `6` статей, `18` переводов EN/RU/HY, `7` официальных источников, `6` клубов и `5` соревнований Top 5.
- Staging smoke пройден: `/health`, News list/detail, clubs, competitions, CORS и фильтры всех пяти лиг отвечают корректно.
- Web smoke пройден на `/`, `/en/news/`, `/ru/news/`, `/hy/news/`, `/en/news/top-5/` и отдельной статье; landing-кнопка `News` открывает раздел, карточки и источники отображаются.
- Для hosting DNS self-resolution Web staging использует точечный cURL resolve `api-staging.footballquiz.club:443:2.56.204.6` с сохранённой TLS-проверкой сертификата и hostname; stream transport остаётся fallback.
- Staging Web отдаёт `X-Robots-Tag: noindex, nofollow, noarchive`.
- Production API/Web deployment не выполнялся. Существующие questions/scoring/legal данные и таблицы не изменялись; общая production DB затронута только новыми `news_*` таблицами из разрешённых additive migrations.
- Перед production нужны финальный human review, отдельное подтверждение production deployment и сохранение подхода без сторонних изображений до утверждения media rights workflow.

## Multiplatform / KMP Context

- Старая подготовительная ветка: `codex/phase1-backend-kmp-compose-readiness`.
- Новая рабочая ветка после ES/DE/IT production merge: `codex/multiplatform-foundation`.
- Актуально на 2026-09-25: `:shared` уже содержит progression model/contracts, Android adapter и тесты; iOS SwiftUI shell и read-only progress screen проверены в Simulator. Подробности: `docs/task_kmp_shared_ios_foundation_ru.md`.
- Отдельное Apple app зарегистрировано в Firebase `footbal-quiz-1f62a` с bundle ID `com.arthuralexandryan.footballquiz`. Локальный `GoogleService-Info.plist` включён в iOS bundle и игнорируется Git. iOS Auth-код с Google Sign-In/Firebase уже собирается и запускается в Simulator; интерактивный вход и совпадение UID с Android ещё ждут ручного теста. Cloud sync не реализован. План: `docs/task_kmp_ios_mobile_sync_ru.md`.
- Старую ветку нельзя blindly merge-ить целиком, потому что она создана до ES/DE/IT production rollout и содержит устаревшие question-audit changes.
- Полезные материалы из старой ветки нужно переносить selectively:
  - backend/KMP/Compose readiness plan;
  - repository contract audit;
  - auth/sync boundary ideas;
  - backend/cPanel/API context.
- Code changes из старой ветки переносить только маленькими отдельными slices после проверки на текущей `2.2.0` базе.

## Следующий безопасный шаг

1. Подтвердить Google provider в Firebase Console и вручную проверить iOS Google Sign-In, повторный запуск, выход и совпадение UID с Android. Код и конфигурация готовы; cloud write отсутствует.
2. После ручной приёмки согласовать дальнейшие mobile sync checkpoints по одному.
3. Backend/content-version и Compose UI вести отдельными маленькими задачами; не смешивать их с первым iOS Auth шагом.
