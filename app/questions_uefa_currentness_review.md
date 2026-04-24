# UEFA Currentness Review

Дата анализа: `14.04.2026`

Покрытые блоки:

- `Europa League`
- `Champions League`

## Безопасно исправлено в `questions_audit.json`

Эти исправления внесены сразу, потому что актуальный ответ уже был среди вариантов `answer_A..D`.

### Europa League

- Вопрос: `How many clubs won the UEFA Cup (Europa League)?`
- Старый ответ: `28`
- Новый ответ: `30`
- Позиция правильного варианта: `D`
- Причина: пользователь подтвердил редакторское решение поставить актуальный ответ на вариант `D`.

- Вопрос: `How many times has Sevilla played in the UEFA Cup Final (Europa League)?`
- Старый ответ: `5`
- Новый ответ: `7`
- Причина: у `Sevilla` семь побед/финалов UEFA Cup / Europa League.
- Источник: UEFA Europa League history/all-time stats.

- Вопрос: `Of these, which club won the UEFA Cup (Europa League) the most?`
- Старый ответ: `Juventus`
- Новый ответ: `Juventus, Tottenham`
- Причина: после победы Tottenham в сезоне `2024/25` у Tottenham стало `3` титула UEFA Cup / Europa League; среди вариантов Juventus и Tottenham теперь делят лидерство.
- Источник: UEFA, Tottenham club history page.
- Языки: `en`, `ru`, `hy`.

### Champions League

- Вопрос: `How many clubs have won the Champions League (European Cup)?`
- Старый ответ: `22`
- Новый ответ: `24`
- Причина: после побед `Manchester City` и `Paris` количество разных победителей стало `24`.
- Источник: UEFA Champions League history / 2024/25 season page.

- Вопрос: `Which footballer spent the most matches in the Champions League (after 1992)?`
- Старый ответ: `Iker Casillas`
- Новый ответ: `Cristiano Ronaldo`
- Причина: Cristiano Ronaldo — лидер по матчам в UEFA Champions League.
- Источник: UEFA Champions League all-time appearances.

## Требует ручной правки вариантов ответов

Эти вопросы выглядят устаревшими, но правильного актуального варианта нет в текущем наборе `answer_A..D`, поэтому автоматом менять только `right_answer` нельзя.

### Champions League

- Вопрос: `Which players scored 5 goals in one Champions League game (after 1992)?`
- Исправлено вручную: правильный вариант перенесён в `answer_C`.
- Новый ответ: `Erling Haaland, Lionel Messi, Luiz Adriano`.
- Источник: UEFA Champions League video/records content, февраль 2026.

## Пока выглядит актуальным

### Europa League

- `Sevilla` как клуб с наибольшим числом титулов.
- Испанские клубы как самые успешные по титулам.
- `Henrik Larsson` как all-time top scorer в UEFA Cup / Europa League в официальной UEFA history-статистике.
- `Giuseppe Bergomi` как лидер по appearances в UEFA Cup / Europa League.
- `Radamel Falcao` как рекордсмен по голам в одном сезоне Europa League.
- `Chelsea` как победитель сезона 2018–2019.
- `Olivier Giroud` как лучший бомбардир сезона 2018–2019.

### Champions League

- `Real Madrid` как клуб с наибольшим числом титулов.
- `Cristiano Ronaldo` как all-time top scorer.
- `Real Madrid` как клуб с рекордной серией побед и как клуб, выигравший 3 раза подряд после смены формата.
- `Spain` как страна с наибольшим числом титулов.
- `England` как страна с наибольшим числом разных победителей.
- `Zinedine Zidane` как тренер, выигравший три турнира подряд.
- `Liverpool` как победитель сезона 2018–2019.
- `Lionel Messi` как лучший бомбардир сезона 2018–2019.
- `Dušan Tadić` как лучший ассистент сезона 2018–2019.

## Data-quality замечания

- В английском блоке есть варианты ответа на кириллице, например `Райан Гиггз` в вопросе про appearances Champions League. Это не ломает фактическую правильность, но ухудшает качество EN-локализации.
- Некоторые английские формулировки выглядят машинно или неестественно: `Which footballer spent the most matches...`, `Who is the most author of goals...`. Это отдельная задача редакторской вычитки.

## Источники

- UEFA Europa League history/all-time stats: https://www.uefa.com/uefaeuropaleague/history/
- UEFA Europa League, Tottenham history: https://www.uefa.com/uefaeuropaleague/history/clubs/1652--tottenham/
- UEFA Champions League all-time appearances: https://www.uefa.com/uefachampionsleague/news/025a-0e9f8a3d1e40-d534e8b4a897-1000--champions-league-alltime-appearances-cristiano-ronaldo-out-in-front/
- UEFA Champions League 2024/25 season page: https://www.uefa.com/uefachampionsleague/history/seasons/2025/statistics/
