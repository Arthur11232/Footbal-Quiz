# Roadmap: Android Stability -> Notifications -> Achievements -> Certificates

Документ фиксирует поэтапный план развития `Football Quiz` с приоритетом на:
- доведение Android-приложения до стабильного состояния;
- постепенный перевод вопросного банка на новую структуру с `questionId`;
- подготовку progression/achievement-модели;
- внедрение notifications;
- внедрение поздравлений, сертификатов и email-доставки.

Текущая дата фиксации плана: **30 июня 2026**

Связанные документы:
- [implementation_plan_android_stability_to_certificates_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/implementation_plan_android_stability_to_certificates_ru.md)
  - [task_phase1_android_stability_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase1_android_stability_ru.md)
- [task_phase2_progression_foundation_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase2_progression_foundation_ru.md)
- [task_phase3_notifications_foundation_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase3_notifications_foundation_ru.md)
- [task_phase4_achievement_foundation_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase4_achievement_foundation_ru.md)
- [task_phase5_congratulations_screen_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase5_congratulations_screen_ru.md)
- [task_phase6_certificate_experience_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase6_certificate_experience_ru.md)
- [task_phase7_email_certificate_delivery_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase7_email_certificate_delivery_ru.md)

---

## 1. Главный принцип

Мы не строим всё одновременно.

Порядок развития должен быть таким:

1. стабилизировать Android;
2. стабилизировать вопросный банк и sync;
3. ввести progression model;
4. добавить notifications;
5. добавить achievements;
6. добавить certificate experience;
7. добавить email delivery сертификатов.

Это позволит:
- не распылиться;
- не сломать текущий продукт;
- строить новые слои поверх стабильной базы.

## 1.1. Актуальный статус на 30 июня 2026

- `Phase 1 — Android Stability 95%+`:
  - считается завершённой по фактическому состоянию проекта;
  - `questionId`, Firestore update path, restore/sync fixes, UEFA Euro catalog repair и release-critical Android fixes уже внедрены.
- `Phase 2 — Progression Foundation`:
  - считается завершённой;
  - stage registry, completion rules, local/cloud persistence и profile presentation layer уже внедрены.
- `Phase 3 — Notifications Foundation`:
  - технический foundation уже внедрён;
  - permission flow, FCM token lifecycle, device binding, auth-only sync, basic preferences и reward-ready local notification уже присутствуют в коде;
  - полноценный product/UI слой notifications осознанно отложен до завершения всех остальных core phase-ов roadmap.
- `Phase 4-6`:
  - реализованы частично раньше плана;
  - achievement trigger, congratulations flow и in-app certificate experience уже присутствуют в кодовой базе;
  - однако roadmap по-прежнему считает их не полностью закрытыми до завершения notification/email части и формального product polishing.

### Практическая интерпретация roadmap

На текущий момент roadmap читается так:

1. `Phase 1` — done
2. `Phase 2` — done
3. `Phase 3A` — technical foundation done
4. `Phase 3B` — notification product/UI layer postponed until all other core phases are finished
5. `Phase 4-6` — partially implemented, require polishing
6. `Phase 7` — not started as a full delivery feature

---

## 2. Целевая progression model

Под `этапом` в продукте считаются следующие завершения:

1. пройти всю `France`
2. пройти всю `Germany`
3. пройти весь блок `TOP5`
4. пройти `UEFA`
5. пройти `World`
6. пройти `Versus`
7. пройти всё приложение

### Базовые правила

- этап считается завершённым только после полного прохождения соответствующего блока;
- completion должен быть детерминированным и одинаково считаться локально и в cloud state;
- этап не должен зависеть от текущего языка интерфейса;
- этап должен опираться на стабильную вопросную идентичность (`questionId`) и корректные aggregate scores.

---

## 3. Phase 1 — Android Stability 95%+

### Цель

Довести Android-приложение до действительно устойчивого состояния перед надстройкой product-фич.

### Что входит

#### 3.1. Вопросный банк и data compatibility
- обновить Firestore новыми вопросами;
- постепенно перевести вопросы на новую структуру с `questionId`;
- не ломать старые app versions до принятия отдельного решения о force update;
- сохранить backward-compatible shape question documents на переходном этапе.

#### 3.2. Локальная и облачная стабильность
- стабилизировать сценарии:
  - first launch
  - restore
  - language switch
  - guest -> auth
  - cloud sync
  - reinstall tolerance
- убедиться, что answered-state не теряется из-за локали;
- убедиться, что restore не ломает Realm state;
- завершить rollout `questionId` end-to-end.

#### 3.3. Release quality
- release build должен стабильно собираться;
- не должно быть критичных regressions в Google Sign-In;
- legal links, release config, target SDK, cloud sync и splash/startup flow должны оставаться здоровыми;
- минимизировать релизные warnings и скрытые runtime risks.

### Definition of done

Phase 1 считается завершённой, когда:
- Android release собирается стабильно;
- `questionId` поддерживается клиентом;
- Firestore dataset обновлён до безопасного совместимого состояния;
- сценарий смены языка больше не сбрасывает answered progress в новой схеме;
- restore/sync flow проходят без crash/regression.

### Статус

`Phase 1` на текущий момент считается завершённой.

---

## 4. Phase 2 — Progression Foundation

### Цель

Подготовить доменную основу для этапов и достижений.

### Что нужно реализовать

#### 4.1. Stage model
- ввести явную модель этапа:
  - `stageId`
  - display name
  - completion criteria
  - order
  - reward type

#### 4.2. Stage completion rules
- определить, по каким данным считается completion каждого этапа;
- completion должен быть вычислим:
  - локально;
  - после sync;
  - после restore.

#### 4.3. Stage progress persistence
- хранить completed stages в локальном состоянии;
- подготовить sync completed stages в cloud state;
- исключить повторное двойное награждение одного и того же этапа.

### Предлагаемые stage IDs

- `top5_france_complete`
- `top5_germany_complete`
- `top5_complete`
- `uefa_complete`
- `world_complete`
- `versus_complete`
- `app_complete`

### Definition of done

- этапы формально описаны;
- completion rules реализованы;
- completed stage можно восстановить после restart/restore;
- один и тот же этап не триггерится повторно бесконтрольно.

### Статус

`Phase 2` на текущий момент считается завершённой.

---

## 5. Phase 3 — Notifications Foundation

### Почему до certificates

Notifications лучше внедрять до сертификатов, потому что они создадут:
- канал возврата пользователя;
- канал оповещения о completed stage;
- основу для будущих reward reminders.

### Что входит

Для актуального состояния проекта Phase 3 лучше делить на два подэтапа:

- `Phase 3A` — technical foundation
- `Phase 3B` — product/UI layer

#### 5.1. Phase 3A — Permission + token flow
- запросить permission на notifications;
- хранить FCM token только для authenticated/logged-in user;
- определить стратегию:
  - что делать с guest users;
  - как вести себя при logout.

#### 5.2. Phase 3A — Notification preferences
- подготовить минимальные настройки:
  - enable/disable
  - marketing/progress-type разделение опционально позже

#### 5.3. Phase 3A — Первые полезные сценарии
- напоминание о незавершённом прогрессе;
- сообщение о новом этапе/обновлении;
- сообщение о том, что доступна награда или сертификат.

#### 5.4. Phase 3B — Product/UI layer
- bell icon только для authenticated user;
- отдельный `Notifications` screen;
- empty/disabled/filled states;
- локальная history/read-model для notification cards;
- более явный reward-first UX поверх уже существующего progression/certificate pipeline.

Этот подэтап не является текущим приоритетом и переносится до завершения остальных core phase-ов roadmap:
- achievement polishing;
- congratulations/certificate polishing;
- email certificate delivery.

### Definition of done

- для `Phase 3A`:
  - permission flow стабилен;
  - токен корректно живёт в lifecycle пользователя;
  - device binding не ломается при login/logout/account switch/delete account;
  - можно безопасно отправить notification зарегистрированному пользователю.
- для `Phase 3B`:
  - у authenticated user есть понятная точка входа в notifications UX;
  - notification center не конфликтует с reward/certificate flow;
  - notification UI не выглядит инородным по отношению к продукту.

### Статус

- `Phase 3A` на текущий момент считается завершённой по foundation-слою.
- `Phase 3B` осознанно отложена до завершения остальных core phase-ов roadmap.

---

## 6. Phase 4 — Achievement Foundation

### Цель

Добавить achievement-событие как отдельный продуктовый слой.

### Что входит

#### 6.1. Stage completion event
- при завершении этапа генерировать доменное событие;
- событие должно содержать:
  - `stageId`
  - user state (`guest` / `authenticated`)
  - timestamp
  - display metadata

#### 6.2. Achievement persistence
- хранить факт вручения achievement;
- защититься от повторной выдачи;
- подготовить основу для future sync.

#### 6.3. Reward pending state
- если guest завершил этап:
  - achievement фиксируется как pending reward opportunity;
  - после authentication reward может быть дооформлен.

### Definition of done

- система умеет надёжно определить завершённый этап;
- achievement событие создаётся один раз;
- можно отличить вручённую награду от pending reward.

### Статус

Часть `Phase 4` уже реализована раньше плана, но фаза не считается формально закрытой как отдельный roadmap milestone.

---

## 7. Phase 5 — Congratulations Screen

### Цель

Сделать отдельный продуктовый момент завершения этапа.

### Flow

1. пользователь завершает этап;
2. показывается отдельный `Congratulations` экран;
3. экран показывает:
   - название этапа;
   - поздравление;
   - визуальное превью сертификата;
   - CTA на продолжение;
4. если пользователь авторизован:
   - показать сообщение, что сертификат будет отправлен на email;
5. если пользователь guest:
   - показать, что для получения персонального сертификата нужно зарегистрироваться.

### Definition of done

- отдельный экран существует;
- guest и authenticated flow различаются;
- UX выглядит как награда, а не просто системный alert.

### Статус

Основной congratulations flow уже существует в коде, но остаётся частью незавершённого общего reward pipeline и требует финального product polishing.

---

## 8. Phase 6 — Certificate Experience

### Цель

Добавить персонализированный сертификат как награду за этап.

### Первая версия (рекомендуется)

### Статус

In-app certificate experience уже частично реализован и используется в текущем flow, включая preview и profile presentation.

Сначала не PDF, а:
- красивый in-app certificate preview;
- персонализация:
  - имя пользователя
  - название этапа
  - дата
  - подпись автора/проекта

### Вторая версия

Позже можно добавить:
- PNG export
- PDF export
- share action

### Definition of done

- пользователь после прохождения этапа видит персональный сертификат;
- guest понимает, что для персонального сертификата нужна регистрация.

---

## 9. Phase 7 — Email Certificate Delivery

### Цель

Отправлять сертификат зарегистрированному пользователю на email.

### Что входит

#### 9.1. Email-ready user state
- отправка только для authenticated users;
- нужен валидный email;
- нужен контроль повторной отправки.

#### 9.2. Delivery flow
- сначала показывается congratulations/certificate screen;
- потом подтверждается отправка на email;
- пользователь получает понятный post-action feedback.

#### 9.3. Guest conversion flow
- если guest завершил этап:
  - показать prompt на регистрацию;
  - после успешного auth вернуть пользователя к pending certificate reward;
  - потом показать, что сертификат отправлен.

### Definition of done

- авторизованный пользователь получает сертификат на email;
- guest может конвертироваться в authenticated и потом получить сертификат.

---

## 10. Что не делать слишком рано

До завершения стабильной Android-фазы не надо одновременно пытаться делать:

- full backend migration
- email attachments + PDF generation
- сложную admin system
- massive rewards catalog
- глубокую achievement gamification
- полный cross-platform sync redesign

Сначала нужна здоровая база.

---

## 11. Рекомендуемый порядок реализации

### Done
1. завершить Android stability work
2. обновить Firestore и rollout `questionId`
3. проверить sync / language / restore
4. реализовать progression model
5. реализовать notifications technical foundation

### Current focus
6. дополировать achievement / congratulations / certificate pipeline
7. укрепить cloud/local behaviour вокруг reward and certificate state
8. удерживать Android release-state в стабильном состоянии

### Postponed after core phases
9. реализовать notification product/UI layer
10. сделать bell icon + notification center + history/read-model

### Later
11. сделать guest -> auth reward flow полностью бесшовным
12. сделать email delivery

---

## 12. Самый важный продуктовый смысл

Сертификаты — это не просто “красивая картинка”.

Они должны:
- усиливать завершённость этапа;
- мотивировать регистрацию;
- формировать эмоциональную награду;
- работать как retention and identity mechanic;
- подготавливать будущий cross-platform product mindset.

---

## 13. Следующий практический шаг

Следующим рабочим шагом после фикса этого roadmap стоит считать:

1. не развивать notification UI дальше в этом цикле;
2. держать `Phase 3A` как завершённый foundation milestone;
3. вернуться к `Phase 3B` только после завершения остальных core phase-ов roadmap;
4. переключить основной фокус на polishing reward/certificate/cloud pipeline и общую Android-стабильность.
