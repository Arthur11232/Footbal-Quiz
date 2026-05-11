# France Currentness Review

Дата проверки: `13.04.2026`

## Источники

- FFF, статистика сборной Франции, обновлено `11.04.2026`: https://www.fff.fr/selection/2-equipe-de-france/palmares-et-histoire-des-bleus.html
- FFF, France 14-0 Gibraltar, `18.11.2023`: https://www.fff.fr/article/11404-la-victoire-record-des-bleus-14-0-.html

## Исправлено в `questions_audit.json`

- `Who in their history has the French team defeated with the largest score?`
  - Было: `Azerbaijan`
  - Стало: `Gibraltar`
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Who spent the most games in the French team?`
  - Было: `Lilian Thuram`
  - Стало: `Hugo Lloris`
  - Обоснование: FFF указывает Hugo Lloris первым в списке caps — `145` selections.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Who is France's top scorer?`
  - Было: `Thierry Henry`
  - Стало: `Olivier Giroud`
  - Обоснование: FFF указывает Olivier Giroud первым в списке лучших бомбардиров — `57` goals; Kylian Mbappe на момент проверки — `56`.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Which of the following held the most games as a coach of the French team?`
  - Было: `Raymond Domenech`
  - Стало: `Didier Deschamps`
  - Обоснование: FFF указывает Didier Deschamps первым по числу матчей как selectionneur — `176`.
  - Языки: `en`, `hy`; `ru` уже был актуален.

## Не трогал в этом проходе

- Вопросы с явно исторической формулировкой по конкретному сезону (`2018-2019`, `2023-2024`) не обновлялись автоматически.
- Если решим заменить сезонные вопросы на "последний завершённый сезон", это лучше делать отдельной политикой сразу по всем лигам, иначе языки и категории начнут расходиться.
