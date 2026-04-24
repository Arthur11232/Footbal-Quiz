# Question Audit Duplicate Prompts

Ниже перечислены случаи, где в `questions_audit.json` повторяется одна и та же формулировка вопроса внутри одного языка и типа, но с разными `right_answer`.

## English

- `English`
  - `Which club has won the English Super Cup the most?`
  - `right_answer`: `Everton`
  - `right_answer`: `Manchester United`

- `Super Cup`
  - `Which club did not manage to become the owner of the UEFA Super Cup?`
  - `right_answer`: `Arsenal`
  - `right_answer`: `Internazionale Milano`

- `Spain`
  - `Of these, which club won the Spanish championship the most?`
  - `right_answer`: `Betis`
  - `right_answer`: `Real Sociedad`

## Russian

- `Spain`
  - `Из перечисленных какой клуб больше всех выиграл чемпионат Испании ?`
  - `right_answer`: `Бетис`
  - `right_answer`: `Реал Сосьедат`

- `Super Cup`
  - `Какому клубу не удалось стать обладателем Суперкубка УЕФА ?`
  - `right_answer`: `Арсенал`
  - `right_answer`: `Интер`

## Armenian

- `Super Cup`
  - `Ո՞ր ակումբին չի հաջողվել նվաճել ՈւԵՖԱ-ի Սուպեր գավաթը:`
  - `right_answer`: `Արսենալ`
  - `right_answer`: `Ինտեր`
