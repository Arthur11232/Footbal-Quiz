# Аудит и Ревью: TOP5 Spain (es, de, it)

Дата первичного аудита: 2026-09-09 15:05  
Дата повторной проверки: 2026-09-09 15:23  
Файл: `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/top5_spain.json`  
Ветка: `codex/add-es-de-it-localization`  
Статус файла: **APPROVED / ПРИМЕНЁН В WORKBENCH**  
Количество вопросов: 71 (213 локализованных блоков `es`, `de`, `it`)

---

## 1. Итоги повторной проверки

Все замечания из первичного аудита **успешно устранены** в файле `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/top5_spain.json`:

1. ✅ **`spain_037` (Хет-трики в Ла Лиге):**
   * Правильный ответ изменён с `Криштиану Роналду` на `Лионель Месси` (вариант A) во всех 6 языках (`en`, `ru`, `hy`, `es`, `de`, `it`).
   * Теперь факт полностью достоверен: у Месси 36 хет-триков (против 34 у Роналду).

2. ✅ **`spain_058` (Суперкубок Испании):**
   * Вариант D изменён с «Атлетик Бильбао» на «Севилью» (`Sevilla` в EN/ES/DE, `Севилья` в RU, `Սևիլիա` в HY, `Siviglia` в IT).
   * Устранён конфликт двойного правильного ответа (у Атлетика и Депортиво было по 3 победы). Теперь у «Депортиво» (вариант B) единоличное лидерство среди 4 предложенных вариантов (Атлетико — 2, Депортиво — 3, Валенсия — 1, Севилья — 1).

3. ✅ **`spain_048` (Требл вместо «Golden Hat Trick»):**
   * ES: `¿Qué club consiguió el triplete?`
   * DE: `Welcher Verein schaffte das Triple?`
   * IT: `Quale club è riuscito a centrare il Triplete?`
   * Устранены кальки, внедрена аутентичная терминология.

4. ✅ **`spain_049` (Дубль вместо «Golden Double»):**
   * ES: `¿Qué clubes consiguieron más veces el doblete?`
   * DE: `Welche Vereine schafften das Double am häufigsten?`
   * IT: `Quali club hanno realizzato più volte il doblete (campionato e coppa)?`

5. ✅ **`spain_059` (Проигранные финалы Суперкубка):**
   * ES: `¿Qué club ha perdido más finales de la Supercopa de España?`
   * DE: `Welcher Verein verlor die meisten Endspiele im spanischen Supercup?`
   * IT: `Quale club ha perso più finali della Supercoppa di Spagna?`
   * Формулировка звучит естественно для футбольного болельщика.

6. ✅ **`spain_046` (Футбольное имя Исидро Лангары):**
   * В вариантах C и D вместо паспортного `Lángara Galarraga` установлено общепринятое спортивное имя `Isidro Lángara`.

7. ✅ **`spain_022` (Испанский синтаксис):**
   * В ES исправлено на: `¿Qué club terminó tercero más veces en el campeonato de España?`.

---

## 2. Результаты тестов и валидации

* **Соответствие ответов (Alignment):** 71/71 вопросов проверены автоматическим скриптом. Индексы букв (A, B, C, D) для `right_answer` совпадают на 100% во всех 6 языках.
* **Целостность данных:** 0 пустых строк, 0 расхождений вариантов.
* **Тест утилиты Firestore:**
  ```bash
  python3 tools/update_firestore_questions.py apply-translation-batch \
    --workbench app/questions_translation_workbench_es_de_it_2026_09_09.json \
    --batch app/question_translation_batches_es_de_it_2026_09_09_manual_draft/top5_spain.json \
    --target-languages es,de,it \
    --require-complete
  ```
  Команда выполняется успешно: обновлено ровно **213 блоков** (`Updated target blocks: 213`).

---

## 3. Заключение

Файл `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/top5_spain.json` полностью проверен, выверен по смыслу и футбольной точности, и применён в основной рабочий файл `workbench`.

Локальные fact/content corrections для `spain_037` и `spain_058` также перенесены в текущий source audit:

```text
app/questions_audit_source_current_es_de_it_2026_09_09.json
```

Firestore пока не обновлялся. Заливка должна выполняться только на финальном этапе после завершения всех batch-файлов и `--dry-run`.
