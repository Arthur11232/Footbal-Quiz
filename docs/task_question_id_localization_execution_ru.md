# Задача: внедрить `questionId` для сохранения answered-state при смене языка

## Статус

Предлагаемая задача на согласование.  
Реализация ещё не начата.

## Цель

Устранить потерю answered-state при смене языка за счёт введения стабильного `questionId` и отказа от использования локализованного текста вопроса как идентичности вопроса.

## Почему это важно

Сейчас answered-state завязан на `question`-текст.

Из-за этого:

- при смене языка текст вопроса меняется
- локальная answered-привязка перестаёт совпадать
- уже отвеченные вопросы снова выглядят неотвеченными

Это особенно неприятно для пользователя в реальном everyday flow, в отличие от `reinstall`, который происходит редко.

## Что входит в задачу

### Входит

- добавить стабильный `questionId` в question data flow
- добавить `questionId` в Realm question entities
- сделать Realm migration `1 -> 2`
- перевести answered matching с `question` на `questionId`
- сохранить answered-state при language sync по `questionId`

### Не входит

- full restore answered-state после reinstall
- cloud sync списка answered question IDs
- выделение отдельной таблицы question progress
- смена current primary key стратегии в Realm question tables

## Ожидаемый результат

После выполнения задачи:

- answered-state больше не теряется при смене языка
- вопрос идентифицируется через `questionId`, а не через локализованный текст
- приложение проходит Realm schema update без migration crash
- play flow и localization sync продолжают работать без регрессий

## Детальный scope по системе

### 1. Firestore

Нужно добавить поле `questionId` в каждый question document.

Требование:

- все переводы одного и того же вопроса должны иметь одинаковый `questionId`

Примеры:

- `france_001`
- `germany_012`
- `champions_004`
- `world_007`
- `vsrm_003`

### 2. App models

Нужно добавить `questionId` в:

- `GameObjectSerializable`
- `QuestionModel`

При необходимости обновить промежуточные mapping layers.

### 3. Realm entities

Нужно добавить строковое поле `questionId` во все таблицы вопросов:

- `DB_France`
- `DB_Germany`
- `DB_Italy`
- `DB_England`
- `DB_Spain`
- `DB_Super_Cup`
- `DB_Europa_League`
- `DB_Europ_Championship`
- `DB_Champions_League`
- `DB_World_Championship`
- `DB_VS_Ronaldo_Messi`
- `DB_VS_RealM_Barcelona`

### 4. Realm migration

Нужно:

- поднять `schemaVersion` с `1` до `2`
- добавить migration object
- в migration только добавить поле `questionId`

Migration не должен:

- пытаться вычислять `questionId` из старого текста
- пытаться идеально восстановить старую answered-history

### 5. Local bootstrap

`DB_Helper.setQuestionsToDB()` должен начать сохранять `questionId` в локальные Realm rows вопросов.

### 6. Play persistence

`RealmPlayProgressLocalDataSource.persistAnswer()` должен:

- перестать искать вопрос по `question`
- начать искать вопрос по `questionId`

### 7. Localization sync preservation

`QuestionsRepositoryImpl.ensureLocalizationSync()` должен:

1. собрать локально answered question IDs до обновления вопросов
2. загрузить новый локализованный набор
3. наложить answered-state по `questionId`
4. сохранить новый локализованный набор в Realm

## План реализации

### Этап 1. Подготовка данных

1. Согласовать формат `questionId`
2. Обновить Firestore questions
3. Проверить, что переводы одного вопроса реально используют одинаковый `questionId`

### Этап 2. Подготовка моделей и схемы

1. Добавить `questionId` в app models
2. Добавить `questionId` в Realm entities
3. Подготовить Realm migration `1 -> 2`

### Этап 3. Перевести локальную запись и чтение

1. Сохранять `questionId` при bootstrap questions
2. Передавать `questionId` в play session flow
3. Перевести answer persistence на `questionId`

### Этап 4. Сохранение progress при смене языка

1. Читать локальный answered-set по `questionId`
2. Обновлять локализованный контент
3. Восстанавливать `isAnswered` по `questionId`

### Этап 5. Проверка и стабилизация

1. Проверить migration
2. Проверить language switch
3. Проверить gameplay regression
4. Проверить release build

## Технические подзадачи

### Подзадача A. Firestore readiness

- убедиться, что `questionId` доступен для всех questions
- зафиксировать final naming convention

### Подзадача B. Model propagation

- обновить transport models
- обновить Firestore mapping
- обновить internal mapping в repository layer

### Подзадача C. Realm schema update

- обновить all question entities
- реализовать migration
- поднять schema version

### Подзадача D. Persistence rewrite

- заменить `question`-based lookup на `questionId`-based lookup
- убедиться, что answered-state больше не зависит от локализованного текста

### Подзадача E. Localization sync bridge

- читать текущие answered IDs до перезаписи вопросного контента
- накладывать answered-state на новый localized question set

## Acceptance criteria

Задача считается выполненной, если:

1. Пользователь отвечает на вопросы на одном языке
2. Меняет язык
3. Те же логические вопросы остаются answered

Дополнительно:

- приложение не падает на миграции Realm
- `compileDevDebugKotlin` проходит успешно
- `bundleProdRelease` проходит успешно
- reset и scoring сценарии не ломаются

## Test checklist

### Functional

- ответить на 3-5 вопросов в одной категории
- сменить язык
- открыть ту же категорию
- убедиться, что вопросы остались answered

- повторить то же для разных категорий:
  - Top 5
  - UEFA
  - World Cup
  - Versus

### Persistence

- перезапустить приложение после смены языка
- проверить, что answered-state не пропал

### Migration

- установить старую сборку
- открыть приложение
- обновить до новой сборки
- убедиться, что Realm migration проходит без краша

### Regression

- проверить `Continue`
- проверить `New Game`
- проверить reset place progress
- проверить cloud restore на отсутствие новых побочных эффектов

## Риски

### Риск 1. Firestore подготовлен не полностью

Если часть вопросов не получит `questionId`, система будет работать нестабильно.

### Риск 2. Старый answered-state нельзя идеально перенести

Старые локальные записи не имеют надёжной stable identity, поэтому разовая потеря части historical answered-state у существующих пользователей возможна.

### Риск 3. Скрытые зависимости на `question`-текст

Нужно внимательно проверить, что больше нигде question text не используется как identity.

## Предлагаемый split по коммитам

1. `Add questionId to models and Realm schema`
2. `Persist answered questions by questionId`
3. `Preserve answered state during localization sync`

## Связанные документы

- [question_id_localization_plan_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/question_id_localization_plan_ru.md)
- [question_id_localization_plan.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/question_id_localization_plan.md)
- [PROJECT_CONTEXT.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/PROJECT_CONTEXT.md)

## Решение на согласование

Если задача одобряется, следующий шаг реализации:

1. начать с подготовки app models и Realm schema
2. затем перевести persistence на `questionId`
3. потом добить localization sync preservation

Это позволит внедрять изменение контролируемо и без "большого взрыва".
