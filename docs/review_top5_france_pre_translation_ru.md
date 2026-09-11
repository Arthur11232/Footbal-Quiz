# Аудит и Ревью: TOP5 France (top5_france.json)

**Дата аудита:** 2026-09-09  
**Файл source batch:** `app/question_translation_batches_es_de_it_2026_09_09/top5_france.json`  
**Файл manual draft:** `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/top5_france.json`  
**Статус:** 🟢 **SOURCE CLEANUP СДЕЛАН / ES-DE-IT DRAFT APPROVED И ПРИМЕНЁН В WORKBENCH / FIRESTORE НЕ ОБНОВЛЯЛСЯ**  
**Дубликатов после cleanup:** **0**

---

## 1. Что Было Найдено

Перед переводом France были найдены старые повторяющиеся вопросы в исходных `en/ru/hy` данных.

Изначально проблема выглядела так:

* `en`: 3 пары duplicate-вопросов;
* `ru`: 4 пары duplicate-вопросов;
* `hy`: 5 пар duplicate-вопросов.

Главная причина: разные языки в старом Firestore-архиве местами были рассинхронизированы, поэтому один и тот же `questionId` мог соответствовать разным историческим версиям вопроса.

---

## 2. Что Исправлено

Сохранены стабильные `questionId`, но обновлены тексты и варианты ответов для duplicate/проблемных вопросов:

* `france_041` — заменён дубль про олимпийское золото на вопрос о рекордсмене по матчам за сборную Франции.
* `france_058` — заменён дубль сезона 2018/19 на вопрос о чемпионе Ligue 1 2023/24.
* `france_062` — заменён дубль про лучшего бомбардира PSG на вопрос о 3-м месте Brest в Ligue 1 2023/24.
* `france_063` — заменён дубль про Mickaël Landreau на вопрос о победителе Coupe de France 2023/24.
* `france_064` — заменён дубль сезона 2016/17 на вопрос о последнем победителе Coupe de la Ligue 2020.
* `france_018` — переформулирован вопрос: теперь он спрашивает не “кто больше всех в XXI веке”, а кто выиграл 7 чемпионатов Франции подряд в XXI веке. Правильный ответ остаётся `Lyon`, и факт становится стабильным.
* `france_038` — актуализирован лучший бомбардир сборной Франции: правильный ответ изменён с `Olivier Giroud` на `Kylian Mbappé`.
* `france_051` — исправлена русская опечатка `Белгия` -> `Бельгия`.

---

## 3. Что Обновлено В Файлах

Cleanup применён синхронно в трёх местах:

* `app/questions_audit_source_current_es_de_it_2026_09_09.json`;
* `app/questions_translation_workbench_es_de_it_2026_09_09.json`;
* `app/question_translation_batches_es_de_it_2026_09_09/top5_france.json`.

После cleanup создан новый manual draft:

```text
app/question_translation_batches_es_de_it_2026_09_09_manual_draft/top5_france.json
```

---

## 4. Результаты Проверки

Технические проверки после cleanup и создания draft:

* `en/ru/hy` question duplicates: 0;
* `en/ru/hy` full duplicates: 0;
* invalid `right_answer`: 0;
* `validate-alignment` по `en,ru,hy`: OK;
* `top5_france.json` содержит 66 вопросов;
* `manual_draft/top5_france.json` содержит 198 target-блоков (`es`, `de`, `it`);
* missing target fields: 0;
* Spanish inverted question marks: OK;
* `apply-translation-batch --require-complete`: OK, `Updated target blocks: 198`;
* France batch применён в основной workbench.

---

## 5. Источники Для Ключевых Фактов

Использованные проверки:

* FFF подтверждает, что Hugo Lloris имеет 145 матчей и является рекордсменом сборной Франции.
* Ligue 1 подтверждает серию Lyon из 7 чемпионств подряд в 2002–2008.
* Ligue 1 / L'Équipe / RSSSF подтверждают итоговую таблицу Ligue 1 2023/24: PSG чемпион, Brest 3-е место.
* PSG official подтверждает победу PSG в Coupe de France 2023/24.
* PSG official подтверждает, что PSG выиграл последний Coupe de la Ligue в 2020.

---

## 6. Следующий Шаг

Нужно вручную проверить:

```text
app/question_translation_batches_es_de_it_2026_09_09_manual_draft/top5_france.json
```

France batch уже approved и применён в основной workbench через `apply-translation-batch --require-complete`.

Firestore всё ещё не обновлялся.
