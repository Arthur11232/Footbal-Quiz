# План по `questionId` и сохранению answered-state при смене языка

## Контекст

Сейчас в проекте есть продуктовая проблема:

- при смене языка уже отвеченные вопросы могут снова становиться неотвеченными
- после reinstall точное состояние answered-вопросов тоже может теряться

На текущем этапе главным приоритетом считаем именно **смену языка**.

Reinstall:

- редкий сценарий
- не требует полного решения в первой итерации

## Корневая причина

Сейчас идентичность вопроса фактически привязана к его локализованному тексту.

Что происходит сейчас:

- в локальной Realm-базе у вопроса хранится `question`
- answered-state проставляется через поиск по `question`
- при language sync локализованные вопросы перезаписываются
- текст вопроса на другом языке уже другой
- старый `isAnswered` больше не может надёжно сопоставиться с новым контентом

Итог:

- локализованный текст используется как identity
- это архитектурно ненадёжно

## Рекомендуемое решение

Ввести для каждого вопроса стабильный `questionId`.

Свойства `questionId`:

- одинаковый для всех языков
- не зависит от текста вопроса
- не зависит от порядка выдачи Firestore
- не зависит от будущих правок формулировки вопроса

Примеры:

- `france_001`
- `france_002`
- `germany_001`
- `champions_014`
- `world_007`
- `vsrm_003`

## Границы первой итерации

В первой итерации решаем именно **потерю answered-state после смены языка**.

Первая итерация должна:

- сохранить answered-state после language switch
- убрать зависимость answered-state от текста вопроса
- добавить Realm migration для нового поля `questionId`

Первая итерация не обязана:

- восстанавливать точный answered-state после reinstall
- синхронизировать answered question IDs в облако
- выносить progress вопросов в отдельную таблицу

## Почему нужен Realm migration

`Realm migration` нужен обязательно, потому что схема вопросных сущностей изменится:

- появится новое поле `questionId`

Сейчас в проекте:

- `schemaVersion(1)`

После изменений нужно будет:

- поднять схему до `schemaVersion(2)`

Если не сделать migration:

- старые установки после обновления словят `RealmMigrationNeededException`

## Каким должен быть migration

Нужен **простой schema migration**, а не сложная data recovery миграция.

Migration должен:

- добавить поле `questionId` во все Realm question entities
- перевести схему с версии `1` на `2`

Migration не должен:

- пытаться вычислять `questionId` по старому тексту вопроса
- пытаться идеально восстанавливать старую историю answered-state
- делать "умную" привязку старых локализованных текстов к новым вопросам

Это должно быть технически безопасное обновление схемы.

## Какие изменения нужны в данных

### Firestore

Каждый документ вопроса должен содержать:

- `questionId`
- `translation`
- `place`
- `question`
- `answers`

Ключевое правило:

- все переводы одного и того же логического вопроса должны иметь один и тот же `questionId`

### Модели приложения

Нужно добавить `questionId` в:

- `GameObjectSerializable`
- `QuestionModel`

Опционально:

- вынести это и в общий контракт, если это улучшит единообразие

### Realm question entities

Нужно добавить строковое поле `questionId` в:

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

Важно:

- в этой фазе не меняем primary key
- текущую PK-схему Realm оставляем как есть

## Какие изменения нужны в логике

### Firestore mapping

`FirestoreQuestionService` должен:

- читать `questionId` из Firestore
- прокидывать его дальше в app-модели

### Локальная запись вопросов

`DB_Helper.setQuestionsToDB()` должен:

- сохранять `questionId` в локальные Realm question rows

### Сохранение ответа в игре

`RealmPlayProgressLocalDataSource.persistAnswer()` должен перестать матчить вопрос по:

- `question`

и перейти на матчинг по:

- `questionId`

### Сохранение answered-state при смене языка

`QuestionsRepositoryImpl.ensureLocalizationSync()` должен сохранять answered-state по `questionId`.

Ожидаемый flow:

1. перед перезаписью локализованных вопросов прочитать локально answered question IDs
2. загрузить новый локализованный набор вопросов
3. для каждого нового вопроса:
   - если его `questionId` уже был answered, выставить `answered = true`
   - иначе `answered = false`
4. сохранить обновлённый локализованный набор в Realm

Именно это и будет основным фиксом для language switch.

## План внедрения

### Этап 1. Ввести стабильную идентичность вопросов

- добавить `questionId` в Firestore
- добавить `questionId` в app-модели
- добавить `questionId` в Realm question entities
- добавить Realm migration `1 -> 2`

### Этап 2. Перевести answered persistence на `questionId`

- протащить `questionId` через play session flow
- обновить сохранение ответа так, чтобы оно использовало `questionId`
- перестать использовать текст вопроса как identity в write-path

### Этап 3. Сохранять answered-state при language sync

- собирать answered question IDs перед language refresh
- накладывать answered-state на новые локализованные вопросы по `questionId`
- прогнать end-to-end сценарий смены языка

### Этап 4. Будущие улучшения

Это уже не часть первой итерации, но хороший следующий шаг:

- вынести прогресс вопросов в отдельную таблицу
- синхронизировать answered question IDs в облако
- восстановить точный answered-state после reinstall

## Риски

### Риск 1. Неконсистентные данные в Firestore

Если один и тот же логический вопрос в разных переводах не получит одинаковый `questionId`, answered-state начнёт расходиться.

Что делать:

- заранее договориться о naming convention
- проверить Firestore перед релизом

### Риск 2. Неполная готовность данных до релиза приложения

Если код уже ожидает `questionId`, а Firestore ещё заполнен не полностью, language sync будет работать нестабильно.

Что делать:

- сначала обновить Firestore-данные
- потом выпускать app-side изменения

### Риск 3. Старая answered-history не восстановится идеально

Старые локальные записи не содержат надёжного `questionId`, поэтому старую историю нельзя гарантированно идеально перенести.

Что делать:

- принять это как разовый transition cost
- оптимизироваться под корректность после релиза новой схемы

## Что обязательно протестировать

### Основные сценарии

- ответить на несколько вопросов на одном языке
- сменить язык
- убедиться, что те же логические вопросы остались answered

- ответить на вопросы в нескольких категориях
- сменить язык
- проверить, что place/category counts не разъехались

- перезапустить приложение после смены языка
- проверить, что answered-state остался стабильным

### Regression-проверки

- reset place progress должен корректно сбрасывать answered-state
- restore cloud stats не должен падать
- play flow должен продолжать корректно обновлять:
  - category score
  - place score
  - answered counts

### Migration-проверки

- установить старую сборку со схемой `1`
- ответить на вопросы
- обновить приложение до схемы `2`
- проверить, что приложение не падает на старте из-за Realm migration

## Детальная задача на реализацию

### Название задачи

Внедрить стабильный `questionId` для сохранения answered-state при смене языка

### Цель задачи

Перестать использовать локализованный текст вопроса как идентичность вопроса и обеспечить сохранение answered-state при language switch.

### Ожидаемый результат

После внедрения:

- answered-state не теряется при смене языка
- answered-state сохраняется по `questionId`
- локальные вопросные сущности поддерживают стабильную идентичность
- Realm schema обновлена безопасно через migration

### Детальный breakdown

#### Подзадача 1. Подготовить Firestore

- выбрать финальный формат `questionId`
- проставить `questionId` во все question documents
- проверить, что переводы одного вопроса используют одинаковый `questionId`

#### Подзадача 2. Обновить транспортные модели

- добавить `questionId` в `GameObjectSerializable`
- добавить `questionId` в `QuestionModel`
- при необходимости обновить вспомогательные маппинги

#### Подзадача 3. Обновить Firestore mapping

- в `FirestoreQuestionService` читать `questionId`
- прокидывать `questionId` в локальные модели

#### Подзадача 4. Обновить Realm question entities

- добавить поле `questionId` во все question tables
- добавить getter/setter в Java-модели

#### Подзадача 5. Добавить Realm migration

- поднять `schemaVersion` с `1` до `2`
- добавить migration object
- в migration только добавить поле `questionId`

#### Подзадача 6. Обновить локальный bootstrap

- в `DB_Helper.setQuestionsToDB()` сохранять `questionId`
- убедиться, что локальная запись не теряет это поле

#### Подзадача 7. Протащить `questionId` через gameplay flow

- обновить `PlaySessionArgs`
- обновить `PlayUiState`
- обновить `PlayViewModel`
- убедиться, что текущий вопрос передаётся вместе с `questionId`

#### Подзадача 8. Перевести answered persistence на `questionId`

- обновить `RealmPlayProgressLocalDataSource.persistAnswer()`
- заменить поиск по `question` на поиск по `questionId`

#### Подзадача 9. Сохранить answered-state при language sync

- перед sync собрать локальный набор answered `questionId`
- после загрузки локализованных вопросов наложить answered-state по `questionId`
- сохранить результат в Realm

#### Подзадача 10. Протестировать end-to-end

- проверить migration
- проверить language switch
- проверить reset flows
- проверить play scoring

## Предлагаемый split по коммитам

1. `Add questionId to models and Realm schema`
2. `Persist answered questions by questionId`
3. `Preserve answered state during localization sync`

## Практическая рекомендация

Это самый здоровый следующий шаг, если мы хотим:

- решить реальную проблему со сменой языка
- не раздувать задачу до полного redesign cloud progress
- заложить сильную базу для будущего улучшения reinstall-истории

То есть это не временный костыль, а хорошая промежуточная архитектурная точка.
