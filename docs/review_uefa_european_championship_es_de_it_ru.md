# Финальный Аудит и Ревью: UEFA European Championship (es, de, it)

**Дата аудита:** 2026-09-10  
**Файл:** `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/uefa_european_championship.json`  
**Статус:** 🟢 **ПОДГОТОВЛЕН / ПРОВЕРЕН / ПРИМЕНЁН В WORKBENCH / FIRESTORE НЕ ОБНОВЛЯЛСЯ**  
**Вопросов в файле:** **21**  
**Блоков перевода (es, de, it):** **63**  
**Дубликатов в файле:** **0**  
**Ошибок синхронизации букв (Letter Mismatch):** **0**

---

## 1. Итоги Проверки По 5 Правилам

| № | Правило | Статус | Результат проверки |
|---|---|:---:|---|
| **1** | **Актуальность** | 🟢 **100%** | Учтён EURO 2024: Spain стала четырёхкратным чемпионом, Germany остаётся лидером по участиям, Cristiano Ronaldo остаётся лучшим бомбардиром турнира, Spain выиграла EURO 2024. |
| **2** | **Терминология** | 🟢 **100%** | Использованы естественные названия турнира: `Eurocopa`, `Europameisterschaft`, `Europei` / `Campionato Europeo`. Термины `final`, `hat-trick`, `máximo goleador`, `Torschütze`, `marcatore` выровнены по смыслу. |
| **3** | **Факты** | 🟢 **100%** | Проверены исторические факты: первый турнир 1960, первая страна-хозяйка France, первый чемпион USSR, 10 стран-чемпионов, Spain лидирует по титулам, Germany лидирует по участиям и финалам. |
| **4** | **Переводы** | 🟢 **100%** | 21 вопрос, ровно 63 блока перевода (`es`, `de`, `it`). Пустых строк нет. `right_answer` в каждом языке является одним из `answer_A..answer_D`. |
| **5** | **Смысл** | 🟢 **100%** | Сложные старые формулировки очищены: вопросы про “страны/тренеров” приведены к единственному или множественному числу там, где это важно для правильного ответа. |

---

## 2. Замечания и Находки Аудита

1. **`european_championship_005` (формулировка):**
   - Вопрос уточнён с множественного `Which countries...` на одиночный `Which country...`, потому что после EURO 2024 Spain единолично лидирует с 4 титулами.

2. **`european_championship_006` и `018` (терминология):**
   - `Holland` заменён на более корректное `Netherlands` в source-слое и переводах.

3. **`european_championship_007` (точность формулировки):**
   - Вопрос уточнён как самый результативный именно финальный матч EURO.
   - Правильный ответ остаётся `Spain - Italy` (EURO 2012, 4:0).

4. **`european_championship_009` и `015` (формулировки):**
   - Вопросы приведены к более естественной форме: лучший бомбардир один (`Cristiano Ronaldo`), рекорд по hat-tricks (`Michel Platini`).

5. **`european_championship_020` (актуализация после EURO 2024):**
   - Было: `Germany, Spain`.
   - Стало: `Spain`.
   - Причина: после победы Spain на EURO 2024 тренеры Испании имеют 4 титула, больше остальных стран.

---

## 3. Проверенная Фактология UEFA European Championship

- EURO проводится с 1960 года.
- Первый финальный турнир прошёл во France.
- Первым чемпионом стала USSR.
- Всего чемпионат Европы выигрывали 10 стран.
- Spain является рекордсменом по титулам: 4 победы (`1964`, `2008`, `2012`, `2024`).
- Germany / West Germany остаётся рекордсменом по участиям: 14 финальных турниров.
- Cristiano Ronaldo остаётся лучшим бомбардиром EURO: 14 голов.
- Michel Platini держит рекорд голов на одном турнире: 9 голов в 1984.
- Spain выиграла EURO 2024.

---

## 4. Источники Фактов UEFA

- UEFA EURO records and stats: `https://www.uefa.com/uefaeuro/history/news/0257-0e0605d428b4-25e24d26a092-1000--euro-records-and-stats/`
- Cristiano Ronaldo EURO records: `https://www.uefa.com/uefaeuro/history/news/026a-126ca1cf8380-48e6f1f55119-1000--ronaldo-becomes-all-time-top-scorer/`
- Spain EURO records and stats: `https://www.uefa.com/uefaeuro/history/news/0268-121557ce4e1b-c19f2f977441-1000--spain-all-their-euro-records-and-stats/`
- Germany EURO records and stats: `https://www.uefa.com/uefaeuro/history/news/0269-1237d7dc8eae-b44111479b2e-1000--germany-all-their-euro-records-and-stats/`

---

## 5. Техническая Валидация

```bash
python3 tools/update_firestore_questions.py apply-translation-batch \
  --workbench app/questions_translation_workbench_es_de_it_2026_09_09.json \
  --batch app/question_translation_batches_es_de_it_2026_09_09_manual_draft/uefa_european_championship.json \
  --target-languages es,de,it \
  --require-complete \
  --output /private/tmp/football_quiz_euro_apply_check.json
```

**Результат:** `Updated target blocks: 63`. Ошибок: 0.

Дополнительная локальная проверка:

- missing fields: 0;
- invalid `right_answer`: 0;
- duplicate questions: 0;
- после применения Euro общий хвост незаполненных строк снизился с `163` до `142`.

---

## 6. Итоговый Статус

Пакет `uefa_european_championship.json` подготовлен, проверен и применён в локальный `workbench`.

Firestore не обновлялся. Следующие логичные блоки:

```text
app/question_translation_batches_es_de_it_2026_09_09/ron_mes_vsrm.json
app/question_translation_batches_es_de_it_2026_09_09/real_barce_vsrb.json
```
