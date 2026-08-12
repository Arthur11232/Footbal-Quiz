# Questions Alignment Candidate Report

Дата: 2026-08-06.

## Что Создано

Создан безопасный candidate-файл:

```text
app/questions_structured_aligned_candidate.json
```

Он создан из:

```text
app/questions_alignment_review.json
app/questions_structured.json
```

Файл является candidate для dry-run/import. Его цель - убрать ложную синхронизацию там, где EN/RU/HY явно описывают разные вопросы.

## Итоговые Счётчики

- Всего вопросов: 689.
- Готовы как `import_ready`: 689.
- Оставлены как `spot_check_not_modified`: 539.
- Ручно выровнены из сдвинутых локалей: 54.
- Ручно дополнены недостающие локали: 96.
- Переведены в безопасный статус `aligned_candidate_translation_needed`: 0.
- Требуют ручного review: 0.

Missing locales среди safe-candidate вопросов:

- EN: 0.
- RU: 0.
- HY: 0.

Финальная внимательная проверка 2026-08-06 дополнительно нашла и исправила скрытые `spot_check`-сдвиги, где `EN` вопрос/варианты были от другого вопроса, а `RU/HY` уже были синхронизированной парой. Исправлены `france_005`, `france_008`, `france_009`, `france_010`, `france_013`, `france_015`, `france_018`, `france_026`, `france_029`, `france_032`, `france_035`, `france_038`, `france_039`, `france_055`, `france_057`, `germany_003`, `germany_007`, `germany_013`, `germany_014`, `germany_025`, `germany_026`, `germany_039`, `germany_043`, `germany_045`, `germany_046`, `germany_054`, `germany_057`.

Также исправлены точечные option-text ошибки: `spain_013`, `spain_068`, `italy_059`, `world_cup_002`, `germany_067`, `vsrb_025`.

Дополнительно восстановлен `france_036`: HY был найден в `app/harcer_hayeren.json`, поэтому вопрос был переведён из `manual_review_required` в safe-кандидат `EN/HY`. Позже RU вопрос `Сколько раз сборная Франции выиграла золотую медаль Олимпийских игр ?` был восстановлен из `app/questions_audit.json` legacy `france_041`, и `france_036` переведён в `import_ready`. Правильный ответ — `1`.

Дополнительно восстановлен `france_052`: HY был найден в `app/harcer_hayeren.json`, numeric-варианты нормализованы под EN option set, вопрос был переведён в safe-кандидат `EN/HY`. Позже RU вопрос `В каких годах сборная Франции стала чемпионом Мира ?` был восстановлен из `app/questions_audit.json` legacy `france_056`, и `france_052` переведён в `import_ready`. Правильный ответ — `1998; 2018`.

Дополнительно вручную выровнены `france_045`, `france_050`, `france_054`: это был сдвиг France-вопросов между локалями. В частности, HY вопрос `Ո՞ր ակումբն է բոլորից շատ նվաճել Լիգայի գավաթը:` был найден в `app/harcer_hayeren.json` и синхронизирован с EN/RU вопросом про самый титулованный клуб League Cup.

Дополнительно вручную выровнен `france_056`: HY вопрос `Ո՞վ է ավելի շատ գոլային փոխանցում կատարել Ֆրանսիայի 2018–2019 թթ.առաջնությունում:` был найден в `app/harcer_hayeren.json`, RU вариант восстановлен из `app/src/main/res/values-ru/strings.xml`, а EN был взят из legacy данных candidate. Вопрос переведён из `manual_review_required` в `import_ready`.

Дополнительно вручную пересобран `france_058`: этот id был смешан из разных вопросов (`RU` про чемпиона Франции 2023-2024, `HY` про чемпиона 2018-2019). Теперь `france_058` синхронизирован как вопрос про чемпиона Франции 2018-2019: EN взят из `app/questions_audit.json` legacy `france_054`, HY из legacy `france_058`, RU из `app/src/main/res/values-ru/strings.xml`. Правильный ответ — `PSG`.

Дополнительно вручную пересобран `france_059`: из этого id снят RU вопрос про лучшего бомбардира чемпионата Франции 2023-2024. Теперь `france_059` синхронизирован как вопрос про лучшего бомбардира чемпионата Франции 2018-2019: EN взят из `app/questions_audit.json` legacy `france_055`, HY из legacy `france_059`, RU из `app/src/main/res/values-ru/strings.xml`. Правильный ответ — `Kylian Mbappé`.

Дополнительно вручную выровнен `france_061`: HY вопрос `Ո՞ր ակումբն է 2018–2019 թթ մրցաշրջանում դարձել Լիգայի գավաթակիր:` был найден в `app/harcer_hayeren.json`, RU вариант восстановлен из `app/src/main/res/values-ru/strings.xml`, а EN был взят из legacy данных candidate. Вопрос переведён из `manual_review_required` в `import_ready`.

Дополнительно вручную выровнен `france_062`: HY вопрос `Ո՞վ է ՊՍԺ-ի լավագույն ռմբարկուն:` был найден в `app/harcer_hayeren.json`, EN был взят из legacy данных candidate, а RU вариант был вручную нормализован под актуальный ответ `Kylian Mbappe`, потому что старый RU resource содержит устаревший ответный набор.

Дополнительно вручную выровнен `france_063`: HY вопрос `Ո՞ր ակումբում (ակումբներում) է հանդես եկել Միքայել Լանդրոն:` был найден в `app/harcer_hayeren.json`, RU вариант найден в `app/questions_audit.json`, а EN был взят из legacy данных candidate. После этого France manual-review считается закрытым.

Дополнительно вручную пересобран `france_064`: HY вопрос `Ո՞ր ակումբն է նվաճել Ֆրանսիայի 2016–2017 թթ. առաջնության չեմպիոնի տիտղոսը:` был найден в `app/questions_audit.json` legacy `france_010`. Теперь `france_064` синхронизирован как вопрос про чемпиона Франции 2016-2017 на EN/RU/HY. Правильный ответ — `Monaco`.

Дополнительно вручную переведены последние 15 RU-locale вопросов из safe-кандидатов: `english_068`, `english_070`, `english_071`, `english_072`, `english_073`, `english_074`, `italy_064`, `italy_065`, `italy_066`, `italy_067`, `spain_065`, `spain_066`, `spain_067`, `spain_069`, `spain_070`. Все они были переведены от уже синхронизированной пары EN/HY и переведены в `import_ready`.

Дополнительно вручную выровнен `germany_062`: правильный HY вопрос для этого id — `Ո՞ր ակումբը երբեք չի նվաճել Գերմանիայի գավաթը:`. Пользовательский HY вопрос про чемпионат Германии относится к `germany_021`, поэтому не был прикреплён к `germany_062`.

Дополнительно вручную выровнен `germany_063`: HY вопрос `Ո՞ր ակումբն է բոլորից շատ պարտվել Գերմանիայի գավաթի եզրափակչում:` был найден в `app/harcer_hayeren.json`, RU вариант найден в `app/questions_audit.json`, а EN был взят из legacy данных candidate.

Дополнительно вручную выровнен `germany_064`: HY вопрос `Գերմանիայի Սուպեր գավաթը բոլորից շատ ո՞ր ակումբն է նվաճել:` был найден в `app/harcer_hayeren.json`, RU вариант найден в `app/questions_audit.json`, а EN был взят из legacy данных candidate. После этого `manual_review_required` в candidate-файле больше не осталось.

Дополнительно вручную выровнен `germany_066`: HY вопрос `Ո՞ր ակումբը երբեք չի նվաճել Գերմանիայի Սուպեր գավաթը:` был найден в `app/harcer_hayeren.json`, RU вариант найден в `app/questions_audit.json`, а EN был взят из legacy данных candidate. Этот вопрос был false safe-pair: старый RU внутри candidate относился к German Cup 2023–2024, а не к German Super Cup.

Дополнительно вручную выровнен `champions_league_028`: HY вопрос `Ո՞ր ֆուտբոլիստն է բոլորից շատ խաղ անցկացրել Չեմպիոնների Լիգայում (1992 թ. հետո):` был найден в `app/harcer_hayeren.json`. Старый HY source имел устаревший/ошибочный right answer `Իկեր Կասիլյաս`; canonical answer нормализован на `Cristiano Ronaldo` по актуальному UEFA all-time appearances leaderboard от 2026-05-30.

Дополнительно вручную выровнен `germany_061`: HY вопрос `Ո՞ր ակումբն է նվաճել Գերմանիայի գավաթը ավելի շատ:` был найден в `app/harcer_hayeren.json`, RU вариант найден в `app/questions_audit.json`, а EN был взят из legacy данных candidate. Этот вопрос был false safe-pair: старый RU внутри candidate относился к Bundesliga 2023–2024 winner, а не к German Cup comparison. После этого HY missing locales в candidate больше не осталось.

Дополнительно вручную дополнен `france_003`: EN вопрос был добавлен по пользовательскому переводу и нормализован в natural English как `Which club did Franck Ribéry play for?`. RU/HY source-пара уже была синхронизирована, правильный ответ — `Marseille`.

Дополнительно вручную дополнен `france_004`: EN вопрос был добавлен из `app/questions_audit.json` / пользовательского сообщения и нормализован в natural English как `Who is the record transfer in French championship history?`. RU/HY source-пара уже была синхронизирована, правильный ответ — `Neymar`.

Дополнительно вручную дополнен `france_006`: EN вопрос был добавлен из `app/questions_audit.json` / пользовательского сообщения и нормализован как `Which club has won the French championship the most?`. RU/HY source-пара уже была синхронизирована, правильный ответ — `PSG`.

Дополнительно вручную дополнен `france_007`: EN вопрос был добавлен из `app/questions_audit.json` / пользовательского сообщения как `How many times in a row did Lyon win the French Championship?`. RU/HY source-пара уже была синхронизирована, правильный ответ — `7`.

Дополнительно вручную дополнен `france_012`: EN вопрос был добавлен из `app/questions_audit.json` / пользовательского сообщения и нормализован как `Who was the top assist provider in the French Championship in the 2017-2018 season?`. RU/HY source-пара уже была синхронизирована, правильный ответ — `Neymar`.

Дополнительно вручную дополнен `france_014`: EN вопрос был добавлен из `app/questions_audit.json` / пользовательского сообщения и нормализован как `Which clubs did Zinedine Zidane play for?`. RU/HY source-пара уже была синхронизирована, правильный ответ — `Bordeaux, Caen`.

Дополнительно вручную дополнен `france_016`: EN вопрос был добавлен из `app/questions_audit.json` / пользовательского сообщения и нормализован как `Which club did Patrick Kluivert play for?`. RU/HY source-пара уже была синхронизирована, правильный ответ — `Lille`.

Дополнительно вручную дополнен `france_017`: EN вопрос был добавлен из `app/questions_audit.json` / пользовательского сообщения и нормализован как `Who was the top scorer in the French Championship in the 2017-2018 season?`. RU/HY source-пара уже была синхронизирована, правильный ответ — `Edinson Cavani`.

Дополнительно вручную выровнена связка `france_011` / `france_019`: EN вопрос `Which club won the French Cup the most?` был ошибочно прикреплён к `france_011`, где RU/HY говорят про Ronaldinho. Для `france_011` восстановлен EN вопрос `Which club did Ronaldinho play for?`, а для `france_019` восстановлен EN вопрос про French Cup. Оба вопроса теперь `import_ready`.

Дополнительно вручную дополнен `france_020`: EN вопрос `Which club has never won the championship of France?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_012` и сопоставлен с RU/HY вопросом про клуб, который никогда не выигрывал чемпионат Франции. Правильный ответ — `Toulouse`.

Дополнительно вручную дополнен `france_021`: EN вопрос `Which club became the silver medalist of the French championship the most?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_014` и сопоставлен с RU/HY вопросом про самого частого серебряного призёра чемпионата Франции. Правильный ответ — `Marseille`; typo `Bodeaux` в EN option нормализован в `Bordeaux`.

Дополнительно вручную дополнен `france_022`: EN вопрос `Which club was the most bronze medalist in France?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_015` и сопоставлен с RU/HY вопросом про самого частого бронзового призёра чемпионата Франции. Правильный ответ — `Monaco`.

Дополнительно вручную дополнен `france_023`: EN вопрос `Of these, which club won the French championship the most?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_016` и сопоставлен с RU/HY вопросом про самый титулованный клуб среди перечисленных. Правильный ответ — `Nantes`; typo `Boreaux` в EN option нормализован в `Bordeaux`.

Дополнительно вручную дополнен `france_024`: EN вопрос `Who is the top scorer of the championship of France of all time?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_017` и сопоставлен с RU/HY вопросом про лучшего бомбардира чемпионата Франции всех времён. Правильный ответ — `Delio Onnis`.

Дополнительно вручную дополнен `france_025`: EN вопрос `Who played the most matches in the championship of France?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_018` и сопоставлен с RU/HY вопросом про игрока с наибольшим числом матчей в чемпионате Франции. Правильный ответ — `Mickaël Landreau`.

Дополнительно вручную дополнен `france_027`: EN вопрос `The Football Player of the Year nomination in France was awarded the most …` был найден в `app/questions_audit.json` как сдвинутый legacy `france_020` и сопоставлен с RU/HY вопросом про самого частого обладателя награды Football Player of the Year во Франции. Правильный ответ — `Thierry Henry`.

Дополнительно вручную дополнен `france_028`: EN вопрос `Who did Zinedine Zidane call the pearl of French football?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_021` и сопоставлен с RU/HY вопросом про “жемчужину” французского футбола. Правильный ответ — `Franck Ribéry`.

Дополнительно вручную дополнен `france_030`: EN вопрос `Who scored the most goals in one season of the French championship?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_023` и сопоставлен с RU/HY вопросом про рекорд голов за один сезон чемпионата Франции. Правильный ответ — `Josip Skoblar`.

Дополнительно вручную дополнен `france_031`: EN вопрос `Since what year the championship of France was named Ligue 1?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_025` и сопоставлен с RU/HY вопросом про год переименования чемпионата Франции в Ligue 1. Правильный ответ — `2002`.

Дополнительно вручную дополнен `france_033`: EN вопрос `How many times did the French team become the champion of Europe?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_027` и сопоставлен с RU/HY вопросом про количество титулов чемпиона Европы у сборной Франции. Правильный ответ — `2`.

Дополнительно вручную дополнен `france_034`: EN вопрос `France won the Confederation Cup …` был найден в `app/questions_audit.json` как сдвинутый legacy `france_028` и сопоставлен с RU/HY вопросом про количество побед сборной Франции в Кубке конфедераций. Правильный ответ — `2 times`; EN option `1 times` нормализован в `1 time`.

Дополнительно вручную дополнен `france_037`: EN вопрос `In what years did the French team win the Confederation Cup?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_031` и сопоставлен с RU/HY вопросом про годы побед сборной Франции в Кубке конфедераций. Правильный ответ — `2001; 2003`.

Дополнительно вручную дополнен `france_040`: EN вопрос `Who with the largest score lost the French national team in its history?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_034` и сопоставлен с RU/HY вопросом про самое крупное поражение сборной Франции. Правильный ответ — `Denmark`.

Дополнительно вручную дополнен `france_041`: EN вопрос `How many times did the French team win the Olympic gold medal?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_036` и сопоставлен с RU/HY вопросом про количество олимпийских золотых медалей сборной Франции. Правильный ответ — `1`.

Дополнительно вручную дополнен `france_042`: EN вопрос `Which of the following 3 times in a row became the owner of the "Golden Ball "?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_037` и сопоставлен с RU/HY вопросом про трёх подряд обладателей “Золотого мяча”. Правильный ответ — `Michel Platini`.

Дополнительно вручную дополнен `france_043`: EN вопрос `Which of the following became the first owner of the "Golden Ball "?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_038` и сопоставлен с RU/HY вопросом про первого французского обладателя “Золотого мяча”. Правильный ответ — `Raymond Kopa`.

Дополнительно вручную дополнен `france_044`: EN вопрос `Who played just 5 seconds in the French national team?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_039` и сопоставлен с RU/HY вопросом про игрока, сыгравшего всего 5 секунд за сборную Франции. Правильный ответ — `Franck Jurietti`.

Дополнительно вручную дополнен `france_046`: EN вопрос `Which of the following held the most games as a coach of the French team?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_041` и сопоставлен с RU/HY вопросом про тренера с наибольшим числом матчей во главе сборной Франции. Правильный ответ — `Didier Deschamps`.

Дополнительно вручную дополнен `france_047`: EN вопрос `Who in the finals of the 2000 European Championships became the author of the winning goal of the French national team?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_042` и сопоставлен с RU/HY вопросом про автора победного гола сборной Франции в финале Евро-2000. Правильный ответ — `David Trezeguet`.

Дополнительно вручную дополнен `france_048`: EN вопрос `A French coach who has won the Champions League 3 times in a row.` был найден в `app/questions_audit.json` как сдвинутый legacy `france_043` и сопоставлен с RU/HY вопросом про французского тренера, выигравшего Лигу чемпионов 3 раза подряд. Правильный ответ — `Zinedine Zidane`.

Дополнительно вручную дополнен `france_049`: EN вопрос `In what year did the French team first participate in the World Cup?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_044` и сопоставлен с RU/HY вопросом про первое участие сборной Франции в чемпионате мира. Правильный ответ — `1930`.

Дополнительно вручную дополнен `france_051`: EN вопрос `Who was the opponent of the French national team in the first international match?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_047` и сопоставлен с RU/HY вопросом про первого соперника сборной Франции в международном матче. Правильный ответ — `Belgium`.

Дополнительно вручную дополнен `france_053`: EN вопрос `Which club won the French Super Cup first?` был найден в `app/questions_audit.json` как сдвинутый legacy `france_049` и сопоставлен с RU/HY вопросом про первый клуб-победитель Суперкубка Франции. Правильный ответ — `Reims`.

Дополнительно вручную дополнен `germany_001`: EN вопрос `Who spent the most matches in the German national team?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_031` и сопоставлен с RU/HY вопросом про футболиста с наибольшим количеством матчей за сборную Германии. Правильный ответ — `Lothar Matthäus`.

Дополнительно вручную дополнен `germany_002`: EN вопрос `Who is Germany's top scorer?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_032` и сопоставлен с RU/HY вопросом про лучшего бомбардира сборной Германии. Правильный ответ — `Miroslav Klose`.

Дополнительно вручную дополнен `germany_004`: EN вопрос `How many times did the German national team win the Confederation Cup?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_034` и сопоставлен с RU/HY вопросом про количество побед сборной Германии в Кубке конфедераций. Правильный ответ — `1`.

Дополнительно вручную дополнен `germany_005`: EN вопрос `How many times has the German national team become European champion?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_035` и сопоставлен с RU/HY вопросом про количество титулов чемпиона Европы у сборной Германии. Правильный ответ — `3`.

Дополнительно вручную дополнен `germany_006`: EN вопрос `How many times has the German national team become World Champion?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_036` и сопоставлен с RU/HY вопросом про количество титулов чемпиона мира у сборной Германии. Правильный ответ — `4`.

Дополнительно вручную дополнен `germany_008`: EN вопрос `Who was the opponent of the German national team in the first international match?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_038` и сопоставлен с RU/HY вопросом про первого соперника сборной Германии в международном матче. Правильный ответ — `Switzerland`.

Дополнительно вручную дополнен `germany_009`: EN вопрос `In what year did the German national team first participate in the World Cup?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_039` и сопоставлен с RU/HY вопросом про первое участие сборной Германии в чемпионате мира. Правильный ответ — `1934`.

Дополнительно вручную дополнен `germany_010`: EN вопрос `In what year did the German national team first participate in the European Championship?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_040` и сопоставлен с RU/HY вопросом про первое участие сборной Германии в чемпионате Европы. Правильный ответ — `1972`.

Дополнительно вручную дополнен `germany_011`: EN вопрос `Which coach of the German national team became the champion of both the World and Europe?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_041` и сопоставлен с RU/HY вопросом про тренера сборной Германии, ставшего чемпионом мира и Европы. Правильный ответ — `Helmut Schön`.

Дополнительно вручную дополнен `germany_012`: EN вопрос `Which player spent the most matches in a row in the German national team?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_042` и сопоставлен с RU/HY вопросом про футболиста с наибольшей серией матчей подряд за сборную Германии. Правильный ответ — `Franz Beckenbauer`.

Дополнительно вручную дополнен `germany_015`: EN вопрос `In what year did the German national team first become the World Champion?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_045` и сопоставлен с RU/HY вопросом про первый титул чемпиона мира сборной Германии. Правильный ответ — `1954`.

Дополнительно вручную дополнен `germany_016`: EN вопрос `In what year did the German national team first become European champion?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_046` и сопоставлен с RU/HY вопросом про первый титул чемпиона Европы сборной Германии. Правильный ответ — `1972`.

Дополнительно вручную дополнен `germany_018`: EN вопрос `Which club won the first championship of Germany?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_048` и сопоставлен с RU/HY вопросом про первый чемпионат Германии. Правильный ответ — `Leipzig`.

Дополнительно вручную дополнен `germany_019`: EN вопрос `How many clubs perform in the Bundesliga?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_049` и сопоставлен с RU/HY вопросом про количество клубов в Бундеслиге. Правильный ответ — `18`.

Дополнительно вручную дополнен `germany_020`: EN вопрос `Of these, which club won the German championship the most?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_050` и сопоставлен с RU/HY вопросом про самый титулованный клуб чемпионата Германии среди перечисленных вариантов. Правильный ответ — `Nürnberg`.

Дополнительно вручную дополнен `germany_021`: EN вопрос `Which club has never won a German championship?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_051` и сопоставлен с RU/HY вопросом про клуб, который никогда не выигрывал чемпионат Германии. Правильный ответ — `RB Leipzig`.

Дополнительно вручную дополнен `germany_022`: EN вопрос `Who is the Bundesliga's top scorer?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_052` и сопоставлен с RU/HY вопросом про лучшего бомбардира Бундеслиги. Правильный ответ — `Gerhard Müller`.

Дополнительно вручную дополнен `germany_023`: EN вопрос `Which clubs did Josef Heynckes perform in?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_053` и сопоставлен с RU/HY вопросом про клубы Юппа Хайнкеса. Правильный ответ — `Hannover 96, Borussia Verein`.

Дополнительно вручную дополнен `germany_024`: EN вопрос `Which club (s) did Gerd Müller perform in?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_054` и сопоставлен с RU/HY вопросом про клуб Герда Мюллера. Правильный ответ — `Bayern`.

Дополнительно вручную дополнен `germany_027`: EN вопрос `Which Bavarian football players managed to become the owner of the "Golden Ball "?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_057` и сопоставлен с RU/HY вопросом про футболистов Баварии, получивших «Золотой мяч». Правильный ответ — `Franz Beckenbauer, Karl-Heinz Rummenigge`.

Дополнительно вручную дополнен `germany_028`: EN вопрос `Which of the Borussia D players won "Golden Ball "?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_058` и сопоставлен с RU/HY вопросом про футболиста Боруссии Дортмунд, получившего «Золотой мяч». Правильный ответ — `Matthias Sammer`.

Дополнительно вручную дополнен `germany_030`: EN вопрос `Which club won the German Cup the most?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_060` и сопоставлен с RU/HY вопросом про клуб, который чаще всех выигрывал Кубок Германии. Правильный ответ — `Bayern`.

Дополнительно вручную дополнен `germany_031`: EN вопрос `Which of the following clubs won the German Cup the most?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_061` и сопоставлен с RU/HY вопросом про клуб из перечисленных, который чаще всех выигрывал Кубок Германии. Правильный ответ — `München 1860`.

Дополнительно вручную дополнен `germany_032`: EN вопрос `Which club has never won the German Cup?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_062` и сопоставлен с RU/HY вопросом про клуб, который никогда не выигрывал Кубок Германии. Правильный ответ — `Hertha`.

Дополнительно вручную дополнен `germany_033`: EN вопрос `Which club lost the most in the German Cup final?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_063` и сопоставлен с RU/HY вопросом про клуб, который чаще всех проигрывал в финале Кубка Германии. Правильный ответ — `Schalke-04`.

Дополнительно вручную дополнен `germany_034`: EN вопрос `Which club won the German Super Cup the most?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_064` и сопоставлен с RU/HY вопросом про клуб, который чаще всех выигрывал Суперкубок Германии. Правильный ответ — `Bayern`.

Дополнительно вручную дополнен `germany_035`: EN вопрос `Which club lost the most in the German Super Cup?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_065` и сопоставлен с RU/HY вопросом про клуб, который чаще всех проигрывал в Суперкубке Германии. Правильный ответ — `Bayern`.

Дополнительно вручную дополнен `germany_036`: EN вопрос `Which club has never won the German Super Cup?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_066` и сопоставлен с RU/HY вопросом про клуб, который никогда не выигрывал Суперкубок Германии. Правильный ответ — `Hamburg`.

Дополнительно вручную дополнен `germany_037`: EN вопрос `Which club won the German League Cup the most?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_001` и сопоставлен с RU/HY вопросом про клуб, который чаще всех выигрывал Кубок немецкой лиги. Правильный ответ — `Bayern`.

Дополнительно вручную дополнен `germany_038`: EN вопрос `Which club has never won the German League Cup?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_002` и сопоставлен с RU/HY вопросом про клуб, который никогда не выигрывал Кубок немецкой лиги. Правильный ответ — `Borussia`.

Дополнительно вручную дополнен `germany_040`: EN вопрос `Who in the season 2017–2018 became the best assistant to the German championship?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_004` и сопоставлен с RU/HY вопросом про лучшего ассистента чемпионата Германии 2017-2018. Правильный ответ — `Thomas Müller`.

Дополнительно вручную дополнен `germany_041`: EN вопрос `Who in the 2017–2018 season became the top scorer of the German championship?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_005` и сопоставлен с RU/HY вопросом про лучшего бомбардира чемпионата Германии 2017-2018. Правильный ответ — `Robert Lewandowski`.

Дополнительно вручную дополнен `germany_042`: EN вопрос `In which club did Dimitar Berbatov perform?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_006` и сопоставлен с RU/HY вопросом про клуб Димитара Бербатова. Правильный ответ — `Bayer`.

Дополнительно вручную дополнен `germany_044`: EN вопрос `Who became the top scorer of the world championship?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_008` и сопоставлен с RU/HY вопросом про немецких футболистов, становившихся лучшими бомбардирами чемпионата мира. Правильный ответ — `Gerhard Müller, Miroslav Klose, Thomas Müller`.

Дополнительно вручную дополнен `germany_047`: EN вопрос `How many times has the German national team participated in the Confederation Cup?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_011` и сопоставлен с RU/HY вопросом про количество участий сборной Германии в Кубке конфедераций. Правильный ответ — `3`.

Дополнительно вручную дополнен `germany_048`: EN вопрос `Which football player was the top scorer of the German championship the most?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_012` и сопоставлен с RU/HY вопросом про футболиста, который чаще всех становился лучшим бомбардиром чемпионата Германии. Правильный ответ — `Both listed footballers`.

Дополнительно вручную дополнен `germany_049`: EN вопрос `Of these clubs, which club's football players became the top scorer of the Bundesliga most?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_013` и сопоставлен с RU/HY вопросом про клуб, чьи футболисты чаще всего становились лучшими бомбардирами Бундеслиги. Правильный ответ — `Köln`.

Дополнительно вручную дополнен `germany_050`: EN вопрос `Representatives of which countries have become the top scorer of the German championship the most?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_014` и сопоставлен с RU/HY вопросом про страны, представители которых чаще всего становились лучшими бомбардирами чемпионата Германии. Правильный ответ — `Poland, Brazil`.

Дополнительно вручную дополнен `germany_051`: EN вопрос `Which club was the first winner of the Bundesliga?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_015` и сопоставлен с RU/HY вопросом про первого победителя Бундеслиги. Правильный ответ — `Köln`.

Дополнительно вручную дополнен `germany_052`: EN вопрос `Who is the author of the first goal of the Bundesliga?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_016` и сопоставлен с RU/HY вопросом про автора первого гола Бундеслиги. Правильный ответ — `Timo Konietzka`.

Дополнительно вручную дополнен `germany_053`: EN вопрос `How many coaches did the German team have?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_017` и сопоставлен с RU/HY вопросом про количество тренеров сборной Германии. Правильный ответ — `12`. Также RU вариант `option_d` нормализован с `16` на `18`, чтобы варианты совпадали с EN/HY.

Дополнительно вручную дополнен `germany_055`: EN вопрос `Who is the best footballer of all time in Germany in BILD?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_019` и сопоставлен с RU/HY вопросом про лучшего немецкого футболиста всех времён по версии BILD. Правильный ответ — `Gerhard Müller`.

Дополнительно вручную дополнен `germany_056`: EN вопрос `Who is the record holder for the number of matches played in the Bundesliga?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_020` и сопоставлен с RU/HY вопросом про рекордсмена по количеству матчей в Бундеслиге. Правильный ответ — `Charly Körbel`.

Дополнительно вручную дополнен `germany_058`: EN вопрос `Which club was the most bronze medalist in the German championship?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_022` и сопоставлен с RU/HY вопросом про клуб, который чаще всех становился бронзовым призёром чемпионата Германии. Правильный ответ — `Borussia Verein`.

Дополнительно вручную дополнен `germany_059`: EN вопрос `Of these, which club took the most prizes in the Bundesliga?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_023` и сопоставлен с RU/HY вопросом про клуб из перечисленных, который чаще всех занимал призовые места в Бундеслиге. Правильный ответ — `Werder`.

Дополнительно вручную дополнен `germany_060`: EN вопрос `How many times did Schalke 04 win the Bundesliga?` был найден в `app/questions_audit.json` как сдвинутый legacy `germany_024` и сопоставлен с RU/HY вопросом про количество побед `Schalke 04` в Бундеслиге. Правильный ответ — `0`.

## Что Исправлено Безопасно

Для high-priority и найденных при финальном spot-check вопросов candidate больше не склеивает разные языки в один ложный canonical вопрос.

Пример:

```text
france_015

Было:
EN: Which club was the most bronze medalist in France?
RU: Из какого клуба Эден Азар перешел в Челси?
HY: Ո՞ր ակումբից է Էդեն Ազարը տեղափոխվել Չելսի:

Стало:
EN/RU/HY синхронизированы как вопрос про переход Эдена Азара из Lille в Chelsea.
Варианты ответа также выровнены по одному смыслу.
```

## Остаточные Замечания

- Структурная проверка проходит: JSON валиден, `correct_option_key` найден среди вариантов, missing `EN/RU/HY` нет.
- Явные semantic mismatch по сдвинутым `EN/RU/HY` исправлены в candidate-файле.
- Остались точные дубли вопросов внутри некоторых тем, например `france_010/france_064`, `france_036/france_041`, `france_054/france_058`, `germany_031/germany_061`, `germany_032/germany_062`, `germany_033/germany_063`, `germany_034/germany_064`, `germany_036/germany_066`, `spain_020/spain_050`. Это не language-alignment ошибка, но перед production-import нужно решить: оставить legacy-дубли или заменить один из дублей новым вопросом.

## Следующий Шаг

Missing locales и явные language-alignment ошибки закрыты. Следующий шаг:

1. Решить судьбу оставшихся точных дублей внутри одной темы.
2. Подготовить dry-run import.
3. После dry-run готовить backend/content import.
