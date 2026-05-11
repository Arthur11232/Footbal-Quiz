# Italy Currentness Review

Дата проверки: `14.04.2026`

## Источники

- UEFA, Italy EURO records, обновлено `29.06.2024`: https://www.uefa.com/uefaeuro/history/news/0269-1237d83e7b42-38a7a9ff1ff0-1000--italy-all-their-euro-records-and-stats/
- AC Milan, Supercoppa Italiana 2024/25 facts, `06.01.2025`: https://www.acmilan.com/en/news/articles/supercoppa-italiana/2025-01-06/inter-v-ac-milan-all-the-numbers-from-the-match
- Guinness World Records, Serie A single-season scoring record: https://www.guinnessworldrecords.com/world-records/457514-most-football-soccer-serie-a-goals-scored-in-a-single-season-by-an-individual
- Transfermarkt, Napoli all-time top goalscorers, checked `14.04.2026`: https://www.transfermarkt.com/ssc-neapel/rekordspieler/verein/6195/sort/tore.desc
- Secondary cross-check for Buffon Serie A appearance record: https://www.skysports.com/football/news/11854/12021194/football

## Исправлено в `questions_audit.json`

- `Who spent the most matches in the championship of Italy?`
  - Было: `Paolo Maldini`
  - Стало: `Gianluigi Buffon`
  - Обоснование: Buffon обошёл Maldini и завершил с рекордом Serie A appearances.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Of these, which club won the Italian Super Cup the most?`
  - Было: `Milan`
  - Стало: `Juventus`
  - Обоснование: Juventus остаётся лидером Supercoppa Italiana с `9` титулами; Milan и Inter — по `8`.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `In what years did Italy become European champion?`
  - Было: только `1968`
  - Стало: `1968; 2020`
  - Обоснование: UEFA указывает Italy как winners `1968, 2020`.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Which players scored the most goals in one season of the Italian championship?`
  - Было: `Gino Rossetti, Gonzalo Higuain`
  - Стало: `Gino Rossetti, Gonzalo Higuain, Ciro Immobile`
  - Обоснование: Immobile в сезоне `2019-2020` сравнялся с рекордом `36` голов.
  - Языки: `en`, `ru`, `hy`.

- `Who is the best scorer of Napoli of all time?`
  - Было: `Marek Hamsik`
  - Стало: `Dries Mertens`
  - Обоснование: Transfermarkt показывает Dries Mertens первым в all-time top goalscorers Napoli — `148` goals.
  - Языки: `en`, `hy`; `ru` уже был актуален.

## Не трогал в этом проходе

- Вопросы с явно исторической формулировкой `2018-2019` / `2023-2024` не выравнивались между языками. Это лучше делать отдельной политикой по сезонным вопросам.
- Несколько старых рекордов Serie A выглядят стабильными и остались без изменений: `Silvio Piola`, `Gunnar Nordahl`, `Juventus`, `Torino`, `Inter`, `Genoa`, `Gianluigi Buffon` по clean-sheet minutes.
