# World Championship Currentness Review

Дата анализа: `13.04.2026`

Покрытый блок:

- `World Championship`

## Безопасно исправлено в `questions_audit.json`

Эти исправления внесены сразу, потому что актуальный ответ уже был среди вариантов `answer_A..D`.

### World Championship

- Вопрос: `Which of the following players scored more goals in the World Championships?`
- Старый ответ: `Thomas Müller`
- Новый ответ: `Lionel Messi`
- Позиция правильного варианта: `D`
- Причина: после ЧМ-2022 Lionel Messi имеет больше голов на чемпионатах мира, чем Thomas Müller, Cristiano Ronaldo и James Rodríguez.
- Источник: FIFA all-time World Cup top scorers article.

### Most World Cup appearances

- Вопрос: `Who played the most matches in World Championships?`
- Старый ответ: `Lothar Matthäus`
- Новый ответ: `Lionel Messi`
- Позиция правильного варианта: `D`
- Изменение: вариант `D` заменён на `Lionel Messi` во всех языках.
- Источник: FIFA, март 2026: Matthäus держал рекорд до того, как его превзошёл Messi в Qatar 2022.

### Current champion passing group stage in XXI century

- Вопрос: `Which country in the XXI century was able to pass the group stage of the World Cup, being the current world champion?`
- Старый ответ: `Brazil`
- Новый ответ: `Brazil and France`
- Позиция правильного варианта: `B`
- Изменение: вариант `A` заменён на `Germany`, вариант `B` заменён на `Brazil and France`, чтобы не оставлять два частично правильных варианта.
- Причина: после ЧМ-2022 `France` также прошла групповой этап как действующий чемпион мира.

## Пока выглядит актуальным

- `Brazil` как страна с наибольшим количеством титулов чемпионата мира.
- `Germany` как страна с наибольшим количеством поражений в финалах.
- `Brazil` как страна с наибольшим числом участий.
- `Miroslav Klose` как all-time top scorer World Cup.
- `Just Fontaine` как рекордсмен по голам на одном чемпионате мира.
- `Oleg Salenko` как рекордсмен по голам в одном матче чемпионата мира.
- `Cristiano Ronaldo` как старейший автор хет-трика на чемпионатах мира.
- `2018` как год с рекордным количеством автоголов.

## Data-quality замечания

- В английском блоке встречаются ответы на кириллице, например `Португалия`, `Мексика`, `Всем`. Это не всегда фактологическая ошибка, но EN-локализацию лучше вычитать отдельно.

## Источники

- FIFA World Cup top scorers: https://www.fifa.com/es/tournaments/mens/worldcup/articles/maximos-goleadores-historia-mundial
- FIFA World Cup appearances note, март 2026: https://www.fifa.com/en/articles/klose-maldini-matthaus-appearances
