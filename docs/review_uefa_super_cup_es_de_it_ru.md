# Финальный Аудит и Ревью: UEFA Super Cup (es, de, it)

**Дата аудита:** 2026-09-10  
**Файл:** `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/uefa_super_cup.json`  
**Статус:** 🟢 **ПОДГОТОВЛЕН / ПРОВЕРЕН / ПРИМЕНЁН В WORKBENCH / FIRESTORE НЕ ОБНОВЛЯЛСЯ**  
**Вопросов в файле:** **31**  
**Блоков перевода (es, de, it):** **93**  
**Дубликатов в файле:** **0**  
**Ошибок синхронизации букв (Letter Mismatch):** **0**

---

## 1. Итоги Проверки По 5 Правилам

| № | Правило | Статус | Результат проверки |
|---|---|:---:|---|
| **1** | **Актуальность** | 🟢 **100%** | Сверены ключевые данные UEFA после Super Cup 2026: Real Madrid остаётся рекордсменом с 6 титулами, Barcelona и Real Madrid имеют по 9 участий, Spain лидирует по победам, Paris стал 26-м клубом-победителем в 2025 и повторил успех в 2026. |
| **2** | **Терминология** | 🟢 **100%** | Использованы естественные футбольные термины: `Supercopa de la UEFA`, `UEFA-Superpokal`, `Supercoppa UEFA`, `finales`, `entrenadores`, `Trainer`, `allenatori`, `triplete` / `Hattrick` / `tripletta`. |
| **3** | **Факты** | 🟢 **100%** | Проверены рекорды клубов, стран, тренеров, игроков, самый результативный матч, fastest goal и победитель 2024 года. Устаревший вопрос про тренеров, выигравших 2 раза подряд, обновлён с учётом Luis Enrique (`2025`, `2026`). |
| **4** | **Переводы** | 🟢 **100%** | 31 вопрос, ровно 93 блока перевода (`es`, `de`, `it`). Пустых строк нет. `right_answer` в каждом языке является одним из `answer_A..answer_D`. |
| **5** | **Смысл** | 🟢 **100%** | Переводы сделаны не дословно-криво, а по смыслу: старые формулировки вроде `"lost opportunity"` переданы как понятные вопросы о проигранных финалах. |

---

## 2. Замечания и Находки Аудита

1. **`super_cup_006` (единообразие смысла):**
   - English/source вопрос уточнён с `Which clubs...` на `Which club...`, потому что правильный ответ один: `Real Madrid`.
   - В русском варианте `answer_D` приведён к смыслу остальных языков: `Реал Мадрид, Севилья`.

2. **`super_cup_008` (опечатка в RU):**
   - Было: `Рассии`.
   - Стало: `России`.

3. **`super_cup_017` (терминология страны):**
   - `Holland` заменён на более корректное `Netherlands` в source-слое и соответствующих переводах.

4. **`super_cup_018` (актуализация после 2026):**
   - Старый правильный ответ учитывал только `Zinedine Zidane` и `Arrigo Sacchi`.
   - После побед Paris в 2025 и 2026 добавлен `Luis Enrique`.
   - Новый правильный ответ: `Zinedine Zidane, Arrigo Sacchi, Luis Enrique`.

---

## 3. Проверенная Фактология UEFA Super Cup

- Первый официальный победитель Super Cup: `Ajax` (`1973`).
- Больше всего побед среди стран: `Spain` — 17.
- Больше всего побед среди клубов: `Real Madrid` — 6.
- Больше всего участий среди клубов: `Barcelona` и `Real Madrid` — по 9.
- Больше всего побед среди игроков: `Dani Carvajal` и `Luka Modrić` — по 5.
- Больше всего побед среди тренеров: `Carlo Ancelotti` — 5.
- Тренеры с двумя победами подряд: `Arrigo Sacchi`, `Zinedine Zidane`, `Luis Enrique`.
- Клубов-победителей всего: `26`.
- Самый результативный one-leg final: `Barcelona 5-4 Sevilla` в 2015.
- Самый быстрый гол: `Diego Costa`, 50 секунд, 2018.
- Победитель 2024 года: `Real Madrid`.

---

## 4. Источники Фактов UEFA

- UEFA Super Cup records and statistics: `https://www.uefa.com/uefasupercup/news/0250-0c5116ce3025-fa215963bb70-1000--uefa-super-cup-records/`
- UEFA Super Cup history: `https://www.uefa.com/uefasupercup/history/`
- UEFA Super Cup roll of honour: `https://www.uefa.com/uefasupercup/news/0250-0c511a04f303-e09c8992bb78-1000--/`

---

## 5. Техническая Валидация

```bash
python3 tools/update_firestore_questions.py apply-translation-batch \
  --workbench app/questions_translation_workbench_es_de_it_2026_09_09.json \
  --batch app/question_translation_batches_es_de_it_2026_09_09_manual_draft/uefa_super_cup.json \
  --target-languages es,de,it \
  --require-complete \
  --output /private/tmp/football_quiz_super_cup_apply_check.json
```

**Результат:** `Updated target blocks: 93`. Ошибок: 0.

Дополнительная локальная проверка:

- missing fields: 0;
- invalid `right_answer`: 0;
- duplicate full rows: 0;
- после применения Super Cup общий хвост незаполненных строк снизился с `194` до `163`.

---

## 6. Итоговый Статус

Пакет `uefa_super_cup.json` подготовлен, проверен и применён в локальный `workbench`.

Firestore не обновлялся. Следующий логичный блок:

```text
app/question_translation_batches_es_de_it_2026_09_09/uefa_european_championship.json
```
