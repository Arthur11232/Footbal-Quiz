# European Championship Currentness Review

Дата проверки: `14.04.2026`

## Источники

- UEFA, EURO records / appearances / top scorers / team records: https://www.uefa.com/uefaeuro/history/news/0257-0e0605d428b4-25e24d26a092-1000--euro-records-most-appearances-top-scorers-key-stats/
- UEFA, EURO coaches records / winners list: https://www.uefa.com/uefaeuro/history/news/025f-0fd28e1a592d-c389509c8b2d-1000--euro-coaches-the-records/

## Исправлено в `questions_audit.json`

- `Which countries have won the European Championships most?`
  - Было: `Germany, Spain`
  - Стало: `Spain`
  - Обоснование: после EURO `2024` Spain имеет рекордные `4` титула, Germany/West Germany — `3`.
  - Языки: `en`, `ru`, `hy`.

- `Which footballer(s) is the top scorer of the European Championship?`
  - Было: `Michel Platini, Cristiano Ronaldo`
  - Стало: `Cristiano Ronaldo`
  - Обоснование: UEFA фиксирует Ronaldo как EURO finals top scorer с `14` голами; Michel Platini имеет `9`.
  - Языки: `en`, `ru`, `hy`.

- `Which country's coaches won the European Championship the most?`
  - Было: `Germany`
  - Стало: `Germany, Spain`
  - Обоснование: по списку UEFA тренеров-победителей German coaches имеют `4` титула (Schön, Derwall, Vogts, Rehhagel), Spanish coaches тоже `4` (Villalonga, Aragonés, Del Bosque, De la Fuente).
  - Языки: `en`, `ru`, `hy`.

## Проверки

- `jq empty app/questions_audit.json` проходит.
- Broken `right_answer` среди всех `en/ru/hy` не обнаружены.
- Broken `right_answer` внутри `European Championship` не обнаружены.

## Не трогал в этом проходе

- `How many countries have become European champions?` осталось `10`: Spain добавила титул, но не новую страну-победителя.
- `Which country has participated ... most?` осталось `Germany`, что совпадает с UEFA (`14` final tournaments).
- `Which country reached the European Championship finals the most?` осталось `Germany`.
- `Which country(s) have won twice in a row?` осталось `Spain`, потому что UEFA отдельно отмечает only Spain successfully defended the title.
- Сезонно-исторические вопросы про первый турнир, первый co-hosted EURO, single-tournament record Michel Platini и oldest coach Giovanni Trapattoni оставлены без изменений.
