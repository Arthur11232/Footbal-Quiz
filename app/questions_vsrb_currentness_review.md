# vsRB Currentness Review

Дата проверки: `14.04.2026`

## Источники

- UEFA, Super Cup records and statistics: https://es.uefa.com/uefasupercup/news/0250-0c5116ce3025-fa215963bb70-1000--uefa-super-cup-records-and-statistics/
- Transfermarkt, LaLiga eternal table: https://www.transfermarkt.us/laliga/ewigeTabelle/wettbewerb/ES1
- Transfermarkt, FIFA Club World Cup tournament records: https://www.transfermarkt.us/fifa-klub-wm/ewigetorschuetzenliste/pokalwettbewerb/KLUB

## Исправлено в `questions_audit.json`

- `Which club has won the UEFA Super Cup 5 times or more?`
  - Было: `Barcelona`
  - Стало: `Both`
  - Обоснование: UEFA фиксирует `6` UEFA Super Cup wins у Real Madrid и `5` у Barcelona.
  - Языки: `en`, `ru`, `hy`.

- `Which club has 600 or more defeats in the championships of Spain?`
  - Было: `Barcelona`
  - Стало: `Both`
  - Обоснование: в актуальной eternal table LaLiga у Real Madrid уже `609` поражений, у Barcelona `665`; оба клуба выше порога `600`.
  - Языки: `en`, `ru`, `hy`.

- `Which club reached the final of the Club World Cup 4 times?`
  - Было: `Both`
  - Стало: `Barcelona`
  - Обоснование: Real Madrid уже имеет `5` FIFA Club World Cup wins, поэтому формулировка `4 times` как точное число теперь подходит только Barcelona. Если позже захотим трактовать вопрос как `4 or more`, лучше отдельно переписать формулировку.
  - Языки: `en`, `ru`, `hy`.

## Проверки

- `jq empty app/questions_audit.json` проходит.
- Broken `right_answer` среди всех `en/ru/hy` не обнаружены.
- Broken `right_answer` внутри `vsRB` не обнаружены.
- Буквы правильных ответов `vsRB` между `en`, `ru`, `hy` не расходятся.

## Не трогал в этом проходе

- Сезонные вопросы `2006–2007`, `2007–2008`, `2010–2011`, `2011–2012`, `2012–2013`, `2013–2014`, `2016–2017`, `2017–2018`, `2018–2019` оставлены как исторические факты.
- Вопросы про спонсоров, стадионы и fixed historical facts оставлены без изменений, если не было явного устаревания.
