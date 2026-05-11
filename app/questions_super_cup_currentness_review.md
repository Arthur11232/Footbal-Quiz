# Super Cup Currentness Review

Дата проверки: `14.04.2026`

## Источники

- UEFA, Super Cup records and statistics: https://es.uefa.com/uefasupercup/news/0250-0c5116ce3025-fa215963bb70-1000--uefa-super-cup-records-and-statistics/

## Исправлено в `questions_audit.json`

- `Which club has participated most in UEFA Super Cup matches?`
  - Было: `Barcelona`
  - Стало: `Barcelona, Real Madrid`
  - Обоснование: UEFA фиксирует `9` участий у Barcelona и Real Madrid.
  - Языки: `en`, `hy`; `ru` уже был актуализирован раньше.

- `Which clubs have won the UEFA Super Cup most?`
  - Было: `Barcelona, Milan`
  - Стало: `Real Madrid`
  - Обоснование: после победы `2024` Real Madrid имеет `6` UEFA Super Cups; Barcelona и Milan имеют по `5`.
  - Языки: `en`, `hy`; `ru` уже был актуализирован раньше.

- `Which country’s clubs failed to win the UEFA Super Cup?`
  - Было: `France`
  - Стало: `Serbia`
  - Обоснование: после победы PSG в `2025` Франция больше не подходит; из вариантов заменён устаревший пункт.
  - Языки: `en`, `ru`, `hy`.

- `How many English clubs have won the UEFA Super Cup?`
  - Было: `5`
  - Стало: `6`
  - Обоснование: UEFA перечисляет Liverpool, Chelsea, Aston Villa, Manchester City, Manchester United и Nottingham Forest.
  - Языки: `en`, `ru`, `hy`.

- `Of these, which club won the UEFA Super Cup the most?`
  - Было: `Valencia`
  - Стало: `Bayern, Valencia`
  - Обоснование: среди вариантов Bayern и Valencia имеют по `2` победы.
  - Языки: `en`, `ru`, `hy`.

- `Which coaches won the UEFA Super Cup most?`
  - Было: `Carlo Ancelotti, Josep Guardiola`
  - Стало: `Carlo Ancelotti`
  - Обоснование: Ancelotti имеет `5` побед, Guardiola — `4`.
  - Языки: `en`, `ru`, `hy`.

- `How many clubs have won the UEFA Super Cup?`
  - Было: `24`
  - Стало: `26`
  - Обоснование: UEFA после Super Cup `2025` отмечает `26` команд-победителей.
  - Языки: `en`, `ru`, `hy`.

- `Which English club is the most "lost" opportunity to become the owner of the UEFA Super Cup?`
  - Было: `Manchester United`
  - Стало: `Manchester United, Chelsea`
  - Обоснование: по участиям/победам UEFA у обоих получается по `3` проигранных Super Cup.
  - Языки: `en`, `ru`, `hy`.

- `Which Spanish clubs most of all "lost their hands" the opportunity to become the owner of the UEFA Super Cup?`
  - Было: `Barcelona, Sevilla`
  - Стало: `Sevilla`
  - Обоснование: Sevilla имеет `7` участий и `1` победу, то есть `6` проигранных Super Cup; Barcelona — `9` участий и `5` побед.
  - Языки: `en`, `ru`, `hy`.

- `Who won the UEFA Super Cup the most?`
  - Было: `Dani Alves`
  - Стало: `Dani Carvajal, Luka Modric`
  - Обоснование: UEFA фиксирует по `5` побед у Carvajal и Modric; Dani Alves имеет `4`.
  - Языки: `en`, `ru`, `hy`.

- `Who is the author of the fastest goal in UEFA Super Cup matches?`
  - Было: `Ever Banega`
  - Стало: `Diego Costa`
  - Обоснование: UEFA фиксирует fastest goal за Diego Costa, `50` seconds in `2018`.
  - Языки: `en`, `ru`, `hy`.

## Проверки

- `jq empty app/questions_audit.json` проходит.
- Broken `right_answer` среди всех `en/ru/hy` не обнаружены.
- Broken `right_answer` внутри `Super Cup` не обнаружены.

## Не трогал в этом проходе

- Порядок вопросов `Super Cup` в `ru` отличается от `en/hy`, поэтому индексное сравнение между языками для этого блока не использовалось.
- Исторические вопросы вроде первого розыгрыша, первого победителя, high-scoring final и hat-trick среди имеющихся вариантов оставлены без изменений.
