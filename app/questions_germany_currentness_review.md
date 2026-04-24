# Germany Currentness Review

Дата проверки: `13.04.2026`

## Источники

- Bundesliga records, актуально на `13.04.2026`: https://www.bundesliga.com/en/bundesliga/news/bundesliga-records-goals-titles-attendances-for-players-and-clubs-10555
- Bundesliga, Bayer Leverkusen title, `16.04.2024`: https://www.bundesliga.com/en/bundesliga/news/bayer-leverkusen-are-2023-24-bundesliga-champions-26930
- DFB, Manuel Neuer as Germany record goalkeeper, `2024`: https://www.dfb.de/news/ticker-undav-fehlt-gegen-die-niederlande
- FIFA, German World Cup Golden Boot winners: https://inside.fifa.com/tournaments/mens/worldcup/2010south-africa/news/worldcupathome-germany-england-south-africa-2010-3070277
- Germany national team manager count, secondary reference: https://en.wikipedia.org/wiki/Germany_national_football_team_manager

## Исправлено в `questions_audit.json`

- `Which goalkeeper spent the most matches in the German national team?`
  - Было: `Josef Maier` / `Զեպպ Մայեր`
  - Стало: `Manuel Neuer` / `Մանուել Նոյեր`
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Which club has never won a German championship?`
  - Было: `Bayer` / `Բայեր`
  - Стало: `RB Leipzig` / `ՌԲ Լայպցիգ`
  - Обоснование: Bayer Leverkusen выиграл первый титул Bundesliga в сезоне `2023-2024`.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Who became the top scorer of the world championship?`
  - Было: неполный набор `Thomas Muller, Gerd Muller`.
  - Стало: `Gerhard Muller, Miroslav Klose, Thomas Muller`.
  - Обоснование: FIFA подтверждает, что Thomas Muller в `2010` повторил путь Gerd Muller (`1970`) и Klose (`2006`) как German Golden Boot winner.
  - Языки: `en`, `ru`, `hy`.

- `Who scored the most goals in one season of the German championship?`
  - Было: `Gerd Muller`
  - Стало: `Robert Lewandowski`
  - Обоснование: Bundesliga records указывает `41` goal в сезоне `2020-2021`.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Which football player was the top scorer of the German championship the most?`
  - Было: `Gerd Muller`
  - Стало: `Both listed footballers`
  - Обоснование: при инклюзивном подсчёте top-scorer crowns Gerd Muller и Robert Lewandowski оба имеют по `7`; `ru` уже использовал этот вариант. Отдельно Bundesliga records формулирует Lewandowski как лидера по `outright` crowns.
  - Языки: `en`, `hy`; в `ru` исправлен битый distractor `Убампе (Клаус)` -> `Ульф Кирстен`.

- `Who is the author of the first goal of the Bundesliga?`
  - Было: `Uwe Seeler`
  - Стало: `Timo Konietzka`
  - Обоснование: Bundesliga records указывает Friedhelm "Timo" Konietzka как автора первого гола Bundesliga.
  - Языки: `en`, `ru`, `hy`.

- `How many coaches did the German team have?`
  - Было: `10`
  - Стало: `12`
  - Обоснование: текущий список Bundestrainer до Julian Nagelsmann даёт `12` человек.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Who is the record holder for the number of matches played in the Bundesliga?`
  - Было: `Karl-Heinz Rummenigge`
  - Стало: `Charly Korbel`
  - Обоснование: Bundesliga records указывает Charly Korbel — `602` Bundesliga appearances.
  - Языки: `en`, `ru`, `hy`.

## Остаточный хвост

- В этом проходе не менял сезонные вопросы `2018-2019` / `2023-2024`, чтобы не смешивать factual-audit с общей политикой сезонных обновлений.
- Глобальная проверка `right_answer` среди `answer_A..D` после правок всё ещё показывает 3 старых не-Germany случая в `Spain` / `World Championship`; их лучше разобрать отдельным маленьким проходом.
