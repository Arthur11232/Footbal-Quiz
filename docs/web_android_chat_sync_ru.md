# Web / Android Chat Sync

Дата обновления: 2026-09-14.

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

## Multiplatform / KMP Context

- Старая подготовительная ветка: `codex/phase1-backend-kmp-compose-readiness`.
- Новая рабочая ветка после ES/DE/IT production merge: `codex/multiplatform-foundation`.
- Старую ветку нельзя blindly merge-ить целиком, потому что она создана до ES/DE/IT production rollout и содержит устаревшие question-audit changes.
- Полезные материалы из старой ветки нужно переносить selectively:
  - backend/KMP/Compose readiness plan;
  - repository contract audit;
  - auth/sync boundary ideas;
  - backend/cPanel/API context.
- Code changes из старой ветки переносить только маленькими отдельными slices после проверки на текущей `2.2.0` базе.

## Следующий безопасный шаг

После production rollout `2.2.0 (58)`:

1. Объединить полезную архитектурную память из `codex/phase1-backend-kmp-compose-readiness` без отката ES/DE/IT файлов.
2. Зафиксировать Phase 1 plan для `codex/multiplatform-foundation`.
3. Начать с безопасного Android-side cleanup: repository/domain contracts, auth boundary, sync boundary, content-version boundary.
4. Не создавать KMP module и не внедрять Compose до завершения boundary cleanup.
5. Backend/content-version work вести отдельными маленькими задачами.
