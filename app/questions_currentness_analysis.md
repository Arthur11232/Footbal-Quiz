# Questions Currentness Analysis

Дата анализа: `14.04.2026`

## Объём базы

- Всего вопросов: `1938`
- Языки: `en`, `ru`, `hy`
- В каждом языке: `646`

## Что было проверено локально

- Структура JSON валидна.
- Для `Europa League` исправлен пустой `categoryType` на `UEFA`.
- Пустых `question`, `type`, `right_answer`, `answer_A..D` не обнаружено.
- Повторы одинаковой формулировки вопроса с разными наборами ответов обнаружены, но **не считаются автоматической ошибкой** без отдельной редакторской проверки.

## Классификация всей базы по риску устаревания

Ниже не "истина по каждому вопросу", а полезное разделение всей базы на классы:

- `911` — `stable_historical`
  - Исторические или фактологические вопросы без явных динамических рекордов.
  - Обычно такие вопросы не устаревают быстро.

- `535` — `historical_specific`
  - Вопросы про конкретный год, сезон, финал, историческое событие.
  - Если ответ уже верен, такие вопросы обычно остаются актуальными как исторический факт.

- `492` — `dynamic_record`
  - Вопросы про "лучший", "самый", "рекордсмен", "больше всех", "всех времён", "текущий лидер" и похожие динамические формулировки.
  - Именно этот класс наиболее подвержен устареванию в 2026 году.

## Где сосредоточен основной риск устаревания

Количество `dynamic_record` вопросов по типам:

- `Italy`: `79`
- `Spain`: `70`
- `Germany`: `60`
- `English`: `59`
- `France`: `46`
- `vsRM`: `38`
- `Champions League`: `30`
- `World Championship`: `26`
- `Super Cup`: `25`
- `vsRB`: `21`
- `Europa League`: `20`
- `European Championship`: `18`

Вывод:

- Наибольший риск устаревания сосредоточен в национальных топ-лигах и вопросах-сравнениях `vsRM` / `vsRB`.
- Вопросы про конкретные сезоны `2018–2019` и прошлые турниры не являются проблемой сами по себе, потому что это фиксированные исторические факты.
- Реально опасный пласт — именно рекорды, "лучшие бомбардиры всех времён", "кто больше всех", "кто первым", "самый дорогой трансфер", "самый титулованный клуб" и аналогичные формулировки.

## Примеры, проверенные по актуальным официальным источникам

Ниже приведены несколько опорных фактов, подтверждённых официальными источниками на апрель 2026 года:

- **UEFA Europa League all-time top scorer**:
  - Актуально: `Pierre-Emerick Aubameyang` — `34` гола в формате Europa League с 2009/10.
  - Следствие: старые вопросы, где правильным ответом был `Radamel Falcao`, могут быть уже устаревшими.
  - Источник: UEFA, январь 2026.

- **UEFA Europa League most titles**:
  - Актуально: `Sevilla` — `7` титулов.
  - Источник: официальный all-time ranking UEFA Europa League.

- **UEFA Champions League all-time top scorer**:
  - Актуально: `Cristiano Ronaldo` — `140` голов (без квалификации).
  - Следствие: вопросы, где Роналду указан как all-time top scorer UCL, по состоянию на март 2026 выглядят актуальными.
  - Источник: UEFA, март 2026.

- **Ligue 1 all-time top scorer**:
  - Актуально: `Delio Onnis` — `299` голов.
  - Следствие: вопросы на этот конкретный рекорд по Ligue 1 выглядят актуальными.
  - Источник: Ligue 1, май 2025.

- **PSG and Ligue 1 top-scorer titles**:
  - Актуально: у `PSG` уже `16` титулов лучшего бомбардира чемпионата Франции, и клуб вышел вперёд относительно `Marseille`.
  - Следствие: старые вопросы на сравнение `PSG` и `OM` по этому показателю могут быть устаревшими.
  - Источник: Ligue 1, май 2025.

## Практический вывод

- База в целом пригодна для использования.
- `1446` вопросов выглядят в целом безопасными как исторические/фиксированные факты.
- `492` вопроса требуют отдельной факт-ревизии, потому что относятся к динамическим рекордам и лидерским статусам.
- Уже подтверждено, что **часть вопросов про Europa League и часть рекордных вопросов Ligue 1 точно требуют обновления**.

## Рекомендуемый следующий шаг

Полный factual-audit всей базы лучше делать поэтапно:

1. `Europa League`
2. `Champions League`
3. `World Championship`
4. `France`
5. `Germany`
6. `Italy`
7. `Spain`
8. `English`
9. `vsRM`
10. `vsRB`

Такой порядок даёт быстрый выигрыш: сначала закрываются самые очевидно устаревающие рекорды, потом национальные лиги, потом сравнительные блоки.

## Прогресс по поэтапному аудиту

- `Europa League`: начат. Безопасно исправлены ответы про количество финалов `Sevilla`, количество клубов-победителей и `Juventus, Tottenham` среди перечисленных клубов после победы Tottenham `2024/25`.
- `Champions League`: начат. Безопасно исправлены ответы про количество клубов-победителей и лидера по appearances после 1992 года; вручную обновлён вариант `answer_C` для вопроса про игроков с 5 голами в одном матче (`Erling Haaland, Lionel Messi, Luiz Adriano`).
- `World Championship`: начат. Исправлены вопросы про большее число голов среди предложенных игроков (`Lionel Messi`), most appearances (`Lionel Messi`) и прохождение группы действующим чемпионом в XXI веке (`Brazil and France`).
- `France`: начат. Исправлены актуальные рекорды сборной Франции: крупнейшая победа (`Gibraltar`), most caps (`Hugo Lloris`), top scorer (`Olivier Giroud`) и most matches as coach (`Didier Deschamps`).
- `Germany`: начат. Исправлены рекорды по сборной и Bundesliga: record goalkeeper (`Manuel Neuer`), клуб без титула (`RB Leipzig` вместо `Bayer`), World Cup Golden Boot winners (`Gerd Muller, Miroslav Klose, Thomas Muller`), single-season goals (`Robert Lewandowski`), top-scorer crowns tie (`Lewandowski` + `Gerd Muller`), first Bundesliga goal (`Timo Konietzka`), coach count (`12`) и Bundesliga appearances (`Charly Korbel`).
- `Italy`: начат. Исправлены most Serie A appearances (`Gianluigi Buffon`), Supercoppa Italiana leader (`Juventus`), Italy EURO winning years (`1968; 2020`), single-season scoring record holders (`Gino Rossetti, Gonzalo Higuain, Ciro Immobile`) и Napoli all-time top scorer (`Dries Mertens`).
- `Spain`: начат. Исправлены EURO titles (`4`), Olympic gold medals (`2`), Olympic appearances (`12`), most caps (`Sergio Ramos`), Pichichi/top-scorer awards (`Lionel Messi`) и два broken `right_answer`/варианта (`of Spain`, `Ռեալ Սոսիեդադ`).
- `English`: начат. Исправлены top-flight titles (`Liverpool, Manchester United`), never won Premier League (`Tottenham`), city titles (`Manchester`), Premier League single-season goals (`Erling Haaland`), away unbeaten run (`Manchester United`), most PL titles among listed clubs (`Manchester City`) и England top scorer (`Harry Kane`).
- `vsRM`: начат. Исправлены устаревшие и несинхронные ответы Ronaldo vs Messi по La Liga, UCL, World Cup, national team goals, Copa America, Club World Cup, Ballon d'Or / Golden Shoe и sponsor questions; также устранён последний global broken `right_answer` (`Brazil - Sweden`).
- `vsRB`: начат. Исправлены актуальные сравнения Real Madrid vs Barcelona по UEFA Super Cup `5+`, LaLiga defeats `600+` и точному вопросу про `4` финала Club World Cup.
- `Super Cup`: начат. Исправлены актуальные рекорды UEFA Super Cup после обновлений `2024/2025`: Real Madrid как лидер по титулам, Barcelona/Real Madrid по участиям, PSG/France case, `6` English clubs, `26` clubs total, Ancelotti, Carvajal/Modric, Diego Costa и проигранные Super Cup по Chelsea/Manchester United и Sevilla.
- `European Championship`: начат. Исправлены актуальные EURO-рекорды после `2024`: Spain как единоличный лидер по титулам, Cristiano Ronaldo как единоличный top scorer и tie `Germany, Spain` по странам тренеров-победителей.
- Подробности сохранены в `app/questions_uefa_currentness_review.md`, `app/questions_world_currentness_review.md`, `app/questions_france_currentness_review.md`, `app/questions_germany_currentness_review.md`, `app/questions_italy_currentness_review.md`, `app/questions_spain_currentness_review.md`, `app/questions_english_currentness_review.md`, `app/questions_vsrm_currentness_review.md`, `app/questions_vsrb_currentness_review.md`, `app/questions_super_cup_currentness_review.md` и `app/questions_european_championship_currentness_review.md`.
