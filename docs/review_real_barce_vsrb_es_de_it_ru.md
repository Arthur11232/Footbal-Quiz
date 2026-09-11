# Финальный Аудит и Ревью: Real Madrid vs Barcelona (es, de, it)

**Дата аудита:** 2026-09-10  
**Файл:** `app/question_translation_batches_es_de_it_2026_09_09_manual_draft/real_barce_vsrb.json`  
**Статус:** 🟢 **ПОДГОТОВЛЕН / ПРОВЕРЕН / ПРИМЕНЁН В WORKBENCH / FIRESTORE НЕ ОБНОВЛЯЛСЯ**  
**Вопросов в файле:** **71**  
**Блоков перевода (es, de, it):** **213**  
**Дубликатов в файле:** **0**  
**Ошибок синхронизации букв (Letter Mismatch):** **0** (100% совпадение букв A/B/C/D во всех 6 языках)

---

## 1. Итоги проверки по 5 Правилам

| № | Правило | Статус | Результат проверки |
|---|---|:---:|---|
| **1** | **Актуальность** | 🟢 **Проверено** | Основные показатели выверены по актуальным источникам: титулы LaLiga (Real Madrid 36, Barcelona 29), Copa del Rey (Barcelona 32, Real Madrid 20), Supercopa de España (Barcelona 16, Real Madrid 13), Champions League / European Cup (Real Madrid 15, Barcelona 5), UEFA Super Cup 5+ побед (Real Madrid 6, Barcelona 5), FIFA Club World Cup / world club titles у Real Madrid больше, чем у Barcelona. |
| **2** | **Терминология** | 🟢 **100%** | Точные клубные и турнирные обозначения: `Copa del Rey`, `Supercopa de España`, `Champions League`, `Mundial de Clubes` / `Klub-Weltmeisterschaft` / `Mondiale per Club`, `Supercopa de la UEFA` / `UEFA-Superpokal` / `Supercoppa UEFA`. Варианты множественного выбора согласованы аутентично: `Ambos` / `Beide` / `Entrambi` (C) и `Ninguno` / `Keiner` / `Nessuno` (D). Названия стадионов: `Santiago Bernabéu`, `Camp Nou`, `Metropolitano`. |
| **3** | **Факты** | 🟢 **Проверено** | **0 дубликатов**. Правильные ответы в batch валидны и совпадают с вариантами. Важно: в этом review не фиксируем лишние точные цифры там, где вопросу достаточно сравнения `Real Madrid / Barcelona / Both / None`; это снижает риск устаревания документа. |
| **4** | **Переводы** | 🟢 **100%** | 71 вопрос, ровно 213 блоков перевода (`es`, `de`, `it`). 0 пустых строк. **Буквы вариантов (A/B/C/D) строго согласованы между всеми 6 языками** (`ru`, `en`, `hy`, `es`, `de`, `it`). Утилита `apply-translation-batch --require-complete` успешно обновляет все 213 блоков без ошибок. |
| **5** | **Смысл** | 🟢 **100%** | Логика парных ответов `Both / None` выдержана безупречно. Вопросы звучат естественно, знаки `¿...?` и немецкая капитализация расставлены без нареканий. |

---

## 2. Ключевые Исторические Тонкости El Clásico

- **Основание клубов:** FC Barcelona основана 29 ноября 1899 г., Real Madrid — 6 марта 1902 г.
- **Стадионы:** «Сантьяго Бернабеу» вмещает ~84 000 зрителей, «Камп Ноу» — 99 354 зрителя (больше лондонского «Уэмбли» на 90 000 мест). «Метрополитано» принадлежит «Атлетико Мадрид».
- **Спонсорство:** Real Madrid экипируется Adidas и сотрудничает с Emirates; Barcelona экипируется Nike.
- **Сезон 2018/19:**
  - Ла Лига: 1. Barcelona (87 очков), 2. Atlético Madrid (76 очков), 3. Real Madrid (68 очков).
  - Трофеи: Barcelona выиграла Ла Лигу и Суперкубок Испании; Real Madrid выиграл FIFA Club World Cup 2018. Оба клуба завоевали минимум один трофей за сезон (`vsrb_071` — `Both`).
- **Трансфер Луиша Фигу (`vsrb_052`):** Фигу перешёл в «Реал Мадрид» напрямую из «Барселоны» летом 2000 г.

---

## 3. Источники Фактов

- Real Madrid official honours: `https://www.realmadrid.com/en-US/the-club/historia/futbol/primer-equipo-masculino/trofeos`
- FC Barcelona official honours: `https://www.fcbarcelona.com/en/futbol/primer-equipo/palmares`
- LaLiga official Real Madrid vs Barcelona titles: `https://www.laliga.com/en-ES/news/comparison-real-madrid-fc-barcelona-titles`
- LaLiga official El Clásico statistics: `https://www.laliga.com/en-ES/news/statistics-real-madrid-fc-barcelona-titles`
- UEFA Champions League all-time records: `https://www.uefa.com/uefachampionsleague/history/`
- FIFA Club World Cup records: `https://inside.fifa.com/tournaments/mens/clubworldcup`

---

## 4. Техническая Валидация

```bash
python3 tools/update_firestore_questions.py apply-translation-batch \
  --workbench app/questions_translation_workbench_es_de_it_2026_09_09.json \
  --batch app/question_translation_batches_es_de_it_2026_09_09_manual_draft/real_barce_vsrb.json \
  --target-languages es,de,it \
  --require-complete \
  --output /tmp/fq_test_real_barce_apply.json
```

**Результат:** `Updated target blocks: 213`. Ошибок: 0.

---

## 5. Итоговый Статус

Файл `real_barce_vsrb.json` проверен и применён в локальный workbench:
- 71 вопрос;
- 213 блоков перевода (`es`, `de`, `it`);
- **0 дубликатов**;
- **0 несовпадений букв** (A/B/C/D строго совпадают во всех 6 языках);
- **0 пустых полей**;
- Все 5 правил соблюдены;
- Firestore не обновлялся.
