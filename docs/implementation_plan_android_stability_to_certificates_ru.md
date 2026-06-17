# Detailed Implementation Plan: Android Stability -> Notifications -> Achievements -> Certificates

Этот документ переводит roadmap в исполнимый порядок работ.

Связанный roadmap:
- [android_stability_achievements_roadmap_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/android_stability_achievements_roadmap_ru.md)

Связанные task-документы:
- [task_phase1_android_stability_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase1_android_stability_ru.md)
- [task_phase2_progression_foundation_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase2_progression_foundation_ru.md)
- [task_phase3_notifications_foundation_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase3_notifications_foundation_ru.md)
- [task_phase4_achievement_foundation_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase4_achievement_foundation_ru.md)
- [task_phase5_congratulations_screen_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase5_congratulations_screen_ru.md)
- [task_phase6_certificate_experience_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase6_certificate_experience_ru.md)
- [task_phase7_email_certificate_delivery_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase7_email_certificate_delivery_ru.md)

---

## 1. Strategy

Мы двигаемся строго по фазам.

Правило:
- новая фаза не начинается как полноценная реализация, пока предыдущая не доведена до рабочего milestone;
- допускается только подготовительное проектирование следующей фазы, но не смешивание production-реализации;
- каждый phase milestone должен завершаться проверяемым состоянием и отдельным release decision.

---

## 2. Execution Order

### Phase 1
Android Stability 95%+

Цель:
- довести Android-приложение и question data flow до стабильного релизного состояния.

Выход:
- кандидат на `2.0.3`.

### Phase 2
Progression Foundation

Цель:
- ввести доменную модель этапов и completion logic.

Выход:
- система умеет понимать, что именно пользователь завершил.

### Phase 3
Notifications Foundation

Цель:
- подготовить канал возврата и оповещения пользователей.

Выход:
- можно безопасно слать notifications зарегистрированным пользователям.

### Phase 4
Achievement Foundation

Цель:
- добавить achievement event и persistence layer.

Выход:
- completion этапа превращается в rewardable domain event.

### Phase 5
Congratulations Screen

Цель:
- создать отдельный reward moment внутри приложения.

Выход:
- пользователь получает экран поздравления за завершённый этап.

### Phase 6
Certificate Experience

Цель:
- показать персонализированный сертификат в приложении.

Выход:
- есть визуальный certificate preview и guest/auth branching.

### Phase 7
Email Certificate Delivery

Цель:
- отправлять сертификат на email зарегистрированным пользователям.

Выход:
- полный completion -> congratulations -> certificate -> email flow.

---

## 3. Rules For Strict Adherence

### 3.1. Что нельзя

- нельзя реализовывать сертификаты до завершения achievement foundation;
- нельзя реализовывать email delivery до завершения certificate experience;
- нельзя строить progression поверх нестабильного question/progress слоя;
- нельзя смешивать rollout `questionId` с большими новыми product-фичами в одном релизном milestone;
- нельзя считать phase complete без validation и acceptance criteria.

### 3.2. Что можно

- можно проектировать следующую фазу заранее;
- можно готовить docs и task breakdown;
- можно готовить UI-концепты до backend/product rollout, если они не вмешиваются в основную стабильность;
- можно вести параллельную аналитику и контентную подготовку, если это не сдвигает приоритет основной фазы.

---

## 4. Phase Gate Model

Каждая фаза проходит через одинаковые ворота:

1. `Planning`
- есть phase task document;
- есть scope;
- есть out-of-scope;
- есть acceptance criteria.

2. `Implementation`
- изменения внесены;
- нет нерешённых блокеров внутри фазы;
- нет скрытого смешения с соседней фазой.

3. `Validation`
- сборка/ручная проверка/flow verification выполнены;
- regressions не обнаружены;
- acceptance criteria подтверждены.

4. `Release Decision`
- либо фаза уходит в release;
- либо остаётся как foundation для следующей фазы.

---

## 5. Release Mapping

### Target mapping

- `2.0.3`:
  - Android Stability 95%+
  - `questionId` rollout
  - Firestore update
  - sync/language/restore stabilization

- `2.0.4`:
  - Progression Foundation

- `2.0.5`:
  - Notifications Foundation

- `2.0.6`:
  - Achievement Foundation
  - optional part of Congratulations Screen if scope remains controlled

- `2.0.7+`:
  - Certificate Experience
  - Email Certificate Delivery

Версионная карта ориентировочная, но Phase 1 как `2.0.3` считается базовым решением.

---

## 6. Current Recommended Focus

На текущий момент главным фокусом остаётся:
- завершить [task_phase1_android_stability_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase1_android_stability_ru.md)

Пока эта задача не закрыта, остальные task-фазы не переходят в активную реализацию.

---

## 7. Success Criteria By Program

Программа в целом считается успешной, если:
- Android stable release больше не разваливается на question/progress/sync flows;
- этапы считаются детерминированно;
- achievement reward работает для auth и guest paths;
- guest registration incentive реально встроен в продукт;
- notifications и certificate delivery усиливают retention, а не усложняют систему хаотично.

