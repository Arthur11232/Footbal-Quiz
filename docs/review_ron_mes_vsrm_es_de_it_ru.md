# Финальный Аудит и Ревью: Ronaldo vs Messi (es, de, it)

**Дата аудита:** 2026-09-10  
**Файл:** `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/ron_mes_vsrm.json`  
**Статус:** 🟢 **ПОДГОТОВЛЕН / ПРОВЕРЕН / ПРИМЕНЁН В WORKBENCH / FIRESTORE НЕ ОБНОВЛЯЛСЯ**  
**Вопросов в файле:** **71**  
**Блоков перевода (es, de, it):** **213**  
**Дубликатов в файле:** **0**  
**Ошибок синхронизации букв (Letter Mismatch):** **0**

---

## 1. Итоги Проверки По 5 Правилам

| № | Правило | Статус | Результат проверки |
|---|---|:---:|---|
| **1** | **Актуальность** | 🟢 **Проверено локально** | Основные рекорды сверены по UEFA/FIFA: Champions League goals/hat-tricks, Club World Cup records, World Cup 2026 stats, international caps и World Cup scoring editions. |
| **2** | **Терминология** | 🟢 **100%** | Убраны кривые формулировки вроде `Golden Cleats` и `soccer`; используется `European Golden Shoe`, `best player award`, `hat-trick`, `Klub-Weltmeisterschaft`, `Mondiale per Club`. |
| **3** | **Факты** | 🟢 **100% для текущего draft** | Исправлены найденные факт-риски: `vsrm_015`, `vsrm_017`. Остальные ответы сохранены по текущей логике source-аудита. |
| **4** | **Переводы** | 🟢 **100%** | 71 вопрос, ровно 213 блоков перевода (`es`, `de`, `it`). Пустых строк нет. `right_answer` в каждом языке является одним из `answer_A..answer_D`. |
| **5** | **Смысл** | 🟢 **100%** | Смысловой дубль `vsrm_024` / `vsrm_026` разведен: один вопрос про общее число голов в Champions League, второй про количество сезонов лучшим бомбардиром. |

---

## 2. Замечания и Находки Аудита

1. **`vsrm_015` (Club World Cup finals goals):**
   - Было: `Both`.
   - Стало: `Cristiano Ronaldo`.
   - Причина: FIFA указывает Cristiano Ronaldo как рекордсмена по голам в финалах Club World Cup.

2. **`vsrm_017` (рекорд матчей за сборную):**
   - Было: `Cristiano Ronaldo`.
   - Стало: `Both`.
   - Причина: Cristiano Ronaldo является рекордсменом Portugal, Lionel Messi является рекордсменом Argentina.

3. **`vsrm_026` (смысловой дубль):**
   - Было почти то же самое, что `vsrm_024`: `Who is the Champions League top scorer?`.
   - Стало: `Who finished as Champions League top scorer in more seasons?`.
   - Ответ остаётся `Cristiano Ronaldo`.

4. **Терминологическая чистка source EN:**
   - `Gerd Muller` -> `Gerd Müller`;
   - `Golden Cleats` -> `European Golden Shoe`;
   - `best soccer player in the World Cup` -> `best player award at the World Cup`.

---

## 3. Проверенная Фактология

- Cristiano Ronaldo остаётся лучшим бомбардиром Champions League и рекордсменом турнира по матчам.
- Cristiano Ronaldo и Lionel Messi делят рекорд по hat-tricks в Champions League: по 8.
- Cristiano Ronaldo держит рекорд Champions League по голам за один сезон: 17.
- Cristiano Ronaldo первым достиг 100 голов в Champions League и 100 голов за один клуб в Champions League.
- Lionel Messi является рекордсменом LaLiga по голам.
- Lionel Messi является рекордсменом El Clásico по голам.
- Lionel Messi выиграл World Cup, Copa América, Olympic gold и дважды получал World Cup best player award.
- Cristiano Ronaldo выиграл EURO и забивал на шести разных World Cup.
- После World Cup 2026 оба игрока имеют 6 участий на чемпионатах мира и оба набрали больше 7 голов на World Cup.

---

## 4. Источники Фактов

- UEFA Champions League top scorers: `https://www.uefa.com/uefachampionsleague/history/rankings/players/goals_scored/`
- UEFA Champions League Ronaldo records: `https://www.uefa.com/uefachampionsleague/news/02a7-2136b8f054ea-76a61b3f9d24-1000--cristiano-ronaldo-in-the-champions-league-records-stats-wh/`
- UEFA Champions League hat-tricks: `https://www.uefa.com/uefachampionsleague/news/0257-0e99f0d0d91b-eb0f4ba7a8f7-1000--champions-league-hattricks-who-has-the-most-when-was-the-fastest-who-has-scored-five-goals-in-a-game/`
- FIFA Club World Cup top scorers: `https://www.fifa.com/en/tournaments/mens/fifa-club-world-cup/saudi-arabia-2023/articles/top-goalscorers-leading-marksmen-goals-all-time`
- FIFA Club World Cup final stats: `https://www.fifa.com/en/tournaments/mens/fifa-club-world-cup/saudi-arabia-2023/articles/the-final-in-stats`
- FIFA World Cup 2026 records: `https://www.fifa.com/en/tournaments/mens/worldcup/canadamexicousa2026/articles/records-broken-set`
- FIFA World Cup all-time leading scorers: `https://www.fifa.com/en/id/mens/worldcup/articles/fifa-world-cup-all-time-leading-scorers`
- UEFA Cristiano Ronaldo international caps: `https://www.uefa.com/european-qualifiers/news/023f-0e9797c3479b-bdb6067609d3-1000--200-caps-for-ronaldo/`
- AFA Lionel Messi national-team legacy: `https://www.afa.com.ar/es/posts/lionel-messi-dos-decadas-de-grandeza-y-un-legado-para-siempre`

---

## 5. Техническая Валидация

```bash
python3 tools/update_firestore_questions.py apply-translation-batch \
  --workbench app/questions_translation_workbench_es_de_it_2026_09_09.json \
  --batch app/question_translation_batches_es_de_it_2026_09_09_manual_draft/ron_mes_vsrm.json \
  --target-languages es,de,it \
  --require-complete \
  --output /private/tmp/football_quiz_vsrm_apply_check.json
```

**Результат:** `Updated target blocks: 213`. Ошибок: 0.

Дополнительная локальная проверка:

- missing fields: 0;
- invalid `right_answer`: 0;
- duplicate questions: 0;
- после применения Ronaldo vs Messi общий хвост незаполненных строк снизился с `142` до `71`.

---

## 6. Итоговый Статус

Пакет `ron_mes_vsrm.json` подготовлен, проверен и применён в локальный `workbench`.

Firestore не обновлялся. Следующий логичный блок:

```text
app/question_translation_batches_es_de_it_2026_09_09/real_barce_vsrb.json
```
