# English Currentness Review

Дата проверки: `14.04.2026`

## Источники

- Premier League, champions list / competition facts: https://www.premierleague.com/en/premier-league-explained
- Premier League, Liverpool 2024/25 title and 20th English top-flight title: https://www.premierleague.com/en/news/4294546
- Premier League, Haaland single-season record: https://www.premierleague.com/news/3449792
- Manchester United, English away unbeaten record: https://www.manutd.com/en/news/detail/how-man-utd-set-new-record-of-28-away-league-games-unbeaten
- Guinness World Records, Premier League away unbeaten record: https://www.guinnessworldrecords.com/world-records/701816-longest-unbeaten-away-run-in-the-football-soccer-english-premier-league
- England Football, Harry Kane profile: https://learn.englandfootball.com/sitecore/content/EnglandFootball/Home/england/mens-senior-team/squad/harry-kane

## Исправлено в `questions_audit.json`

- `Which club has won the championship of England the most?`
  - Было: `Manchester United`
  - Стало: `Liverpool, Manchester United`
  - Обоснование: после титула Liverpool `2024/25` оба клуба имеют по `20` English top-flight titles.
  - Языки: `en`, `ru`, `hy`.

- `Which club has never won the Premier League?`
  - Было: `Liverpool`
  - Стало: `Tottenham`
  - Обоснование: Liverpool выиграл Premier League в `2019/20` и `2024/25`; среди вариантов без титула остался Tottenham.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Which city clubs have won the England championship the most?`
  - Было: `Liverpool`
  - Стало: `Manchester`
  - Обоснование: Manchester United + Manchester City суммарно впереди Liverpool + Everton.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Of those listed, who scored more goals in one Premier League season?`
  - Было: `Mohamed Salah`
  - Стало: `Erling Haaland`
  - Обоснование: Haaland побил Premier League single-season record, забив `36` голов в сезоне `2022/23`.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Which club owns the longest unbeaten run in away games in the England Championship?`
  - Было: `Arsenal`
  - Стало: `Manchester United`
  - Обоснование: Manchester United установил рекорд away unbeaten run; Guinness фиксирует `29` матчей.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Of the following, which club won the Premier League the most?`
  - Было: `Chelsea`
  - Стало: `Manchester City`
  - Обоснование: среди вариантов `Arsenal / Manchester City / Chelsea / Liverpool` больше всего Premier League titles у Manchester City.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Who is the top scorer of all time for England?`
  - Было: `Wayne Rooney`
  - Стало: `Harry Kane`
  - Обоснование: England Football указывает Kane как all-time record goalscorer.
  - Языки: `en`, `hy`; `ru` уже был актуален.

## Не трогал в этом проходе

- Сезонные вопросы `2018-2019` / `2023-2024` не выравнивались между языками.
- Исторические/устойчивые рекорды вроде `Dixie Dean`, `Gareth Barry`, `Alan Shearer`, `Ryan Giggs`, `Petr Cech`, `Arsenal` в FA Cup остались без изменений.
