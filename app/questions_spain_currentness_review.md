# Spain Currentness Review

Дата проверки: `14.04.2026`

## Источники

- UEFA, Spain EURO records, обновлено `14.07.2024`: https://www.uefa.com/uefaeuro/history/news/0268-121557ce4e1b-c19f2f977441-1000--spain-euro-records-and-stats/
- RFEF, Sergio Ramos profile, checked `14.04.2026`: https://rfef.es/es/selecciones/jugadores/sergio-ramos-1
- RFEF, Spanish Olympic football history before Paris 2024: https://rfef.es/en/noticias/one-gold-three-silvers-and-104-years-of-spanish-olympic-football
- RFEF, Spain Olympic gold in Paris 2024: https://rfef.es/index.php/en/noticias/match-report-spain-rewrites-their-golden-history-in-paris-3-5
- FIFA, Spain Olympic gold in Paris 2024: https://www.fifa.com/en/tournaments/olympicgames/paris2024/mens/articles/match-report-gold-medal-france-spain
- FC Barcelona, Messi's eighth Pichichi: https://www.fcbarcelona.com/en/news/2153661/eighth-pichichi-for-leo-messi/amp

## Исправлено в `questions_audit.json`

- `How many times did Spain become European champion?`
  - Было: `3`
  - Стало: `4`
  - Обоснование: UEFA указывает Spain winners `1964, 2008, 2012, 2024`.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `How many times did the Spanish national team win the Olympic gold medal?`
  - Было: `1`
  - Стало: `2`
  - Обоснование: Spain выиграла золото в `1992` и повторила успех в Paris `2024`.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Which footballer spent the most matches in Spain?`
  - Было: `Iker Casillas`
  - Стало: `Sergio Ramos`
  - Обоснование: RFEF указывает Sergio Ramos — `180` matches.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `How many times have the Spanish national team participated in the Olympics?`
  - Было: `3`
  - Стало: `12`
  - Обоснование: перед Paris 2024 RFEF указывал турнир как 12-е участие мужской олимпийской команды Испании.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Who was the top scorer in the Spanish Championships the most?`
  - Было: `Lionel Messi, Telmo Zarra`
  - Стало: `Lionel Messi`
  - Обоснование: Messi выиграл Pichichi `8` раз, обойдя Telmo Zarra.
  - Языки: `en`, `hy`; `ru` уже был актуален.

- `Which country’s coaches were the winners of the Spanish Championships the most?`
  - Исправлен broken `right_answer`: `Of Spain` -> `of Spain`.
  - Язык: `en`.

- `Նշվածներից ո՞ր ակումբն է ավելի շատ նվաճել Իսպանիայի առաջնության չեմպիոնի տիտղոսը:`
  - Исправлен broken вариант: `Ռեալ Սոսիեդա` -> `Ռեալ Սոսիեդադ`.
  - Язык: `hy`.

## Не трогал в этом проходе

- Сезонные вопросы `2018-2019` / `2023-2024` не выравнивались между языками.
- Большинство статичных рекордов La Liga остались без изменений: all-time goals (`Lionel Messi`), single-season goals (`Lionel Messi`), appearances among listed options (`Andoni Zubizarreta`), goalkeeper clean sheets (`Andoni Zubizarreta`) и др.
