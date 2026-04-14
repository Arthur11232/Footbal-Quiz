# vsRM Currentness Review

Дата проверки: `14.04.2026`

## Источники

- UEFA, Champions League all-time top scorers / hat-tricks / 100 goals / titles: https://www.uefa.com/uefachampionsleague/news/0257-0e910cf2494a-5185150de9d4-1000--champions-league-all-time-top-scorers-cristiano-ronaldo-/
- UEFA, Champions League hat-tricks lowdown: https://www.uefa.com/uefachampionsleague/news/0257-0e99f0d0d91b-eb0f4ba7a8f7-1000--champions-league-hattricks-the-full-lowdown/
- FIFA, Lionel Messi 2026 profile / World Cup records / trophies: https://www.fifa.com/en/tournaments/mens/worldcup/canadamexicousa2026/articles/lionel-messi-argentina-quotes-records
- FIFA, Cristiano Ronaldo 2026 profile / World Cup records: https://www.fifa.com/en/tournaments/mens/worldcup/articles/26-superstars-cristiano-ronaldo
- FIFA, Messi World Cup records: https://www.fifa.com/en/tournaments/mens/worldcup/canadamexicousa2026/articles/lionel-messi-fifa-world-cup-records-milestones-argentina-appearances-assists-goals
- FC Barcelona, Messi 2020/21 Pichichi: https://www.fcbarcelona.com/en/football/first-team/noticias/2377471/leo-messi-recibe-el-premio-pichichi-202021
- FC Barcelona, Messi record collection / La Liga and El Clasico records: https://www.fcbarcelona.com/en/news/2070529/leo-messi-fc-barcelonas-historic-record-breaker
- Ballon d'Or, men's winners list: https://ballondor.com/en/winners
- Transfermarkt, current international goals/caps snapshot: https://www.transfermarkt.com/spieler/rekordnationalspieler/statistik
- FIFA, Club World Cup all-time top goalscorers baseline: https://www.fifa.com/en/tournaments/mens/fifa-club-world-cup/saudi-arabia-2023/articles/top-goalscorers-leading-marksmen-goals-all-time
- Transfermarkt, current FIFA Club World Cup all-time top goalscorers after 2025: https://www.transfermarkt.us/fifa-klub-wm/ewigetorschuetzenliste/pokalwettbewerb/KLUB

## Исправлено в `questions_audit.json`

- Выравнены `vsRM` ответы между `en`, `ru`, `hy`: после правок все вопросы этого блока дают одинаковую букву правильного ответа во всех трёх языках.
- Исправлены устаревшие национальные достижения Messi/Ronaldo: `85+` и `97+` голов за сборную теперь `Both`, World Champion и Copa America winner теперь `Lionel Messi`.
- Исправлены World Cup вопросы: `7+` голов на ЧМ теперь `Both`; World Cup Golden Ball/best player prize, silver medal and World Champion — `Lionel Messi`; World Cup hat-trick and scoring in every participated World Cup — `Cristiano Ronaldo`.
- Исправлены Champions League вопросы в Armenian: all-time goals/top scorer and first 100 goals — `Cristiano Ronaldo`; 4+ goals in one match, 100 goals for one club and hat-tricks record — `Both`; no player scored in every match of one UCL season.
- Исправлены club trophy вопросы в Armenian: UCL 5+ titles and 3 consecutive UCL titles — `Cristiano Ronaldo`; UCL 4+ titles — `Both`; Club World Cup 3 in a row — `None`; Club World Cup 3+ titles — `Both`; Club World Cup 4+ titles — `Cristiano Ronaldo`.
- Исправлены Golden Shoe / Ballon d'Or вопросы в Armenian: first 3x Golden Shoe — `Lionel Messi`, first 4x — `Cristiano Ronaldo`, 5x — `Lionel Messi`, Ballon d'Or 4+ — `Both`.
- Исправлены La Liga / El Clasico вопросы: most La Liga hat-tricks теперь `Lionel Messi`; single-season La Liga hat-tricks `Both`; La Liga penalties `Cristiano Ronaldo`; El Clasico hat-trick and all-time El Clasico top scorer `Lionel Messi`.
- Исправлены sponsorship/seasonal вопросы в Armenian: `ADIDAS` — `Lionel Messi`; `50+ goals in 2018–2019` — `Lionel Messi`.
- Дополнительно устранён старый global broken answer в `World Championship`: `Brazil- Sweden` -> `Brazil - Sweden`.

## Проверки

- `jq empty app/questions_audit.json` проходит.
- Broken `right_answer` среди всех `en/ru/hy` не обнаружены.
- Broken `right_answer` внутри `vsRM` не обнаружены.
- Буквы правильных ответов `vsRM` между `en`, `ru`, `hy` больше не расходятся.

## Не трогал в этом проходе

- Варианты ответов и формулировки вопросов не переписывались, кроме точечного пробела в `Brazil - Sweden`.
- Блок `vsRB` ещё не проходили: он следующий по плану.
