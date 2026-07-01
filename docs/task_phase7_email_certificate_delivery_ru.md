# Task: Phase 7 — Email Certificate Delivery

Связанные документы:
- [android_stability_achievements_roadmap_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/android_stability_achievements_roadmap_ru.md)
- [implementation_plan_android_stability_to_certificates_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/implementation_plan_android_stability_to_certificates_ru.md)

## Цель

Отправлять сертификат зарегистрированному пользователю на email.

## Scope

- email-ready auth flow
- delivery trigger
- success feedback
- guest -> auth -> deliver flow

## Detailed Subtasks

### 1. Delivery prerequisites
- есть authenticated user
- есть email
- есть completed achievement
- есть certificate payload

### 2. Delivery orchestration
- сначала показать congratulations/certificate screen
- потом отправить сертификат
- потом показать user feedback
- удержать идемпотентность, чтобы пользователь не получал повторные письма бесконтрольно

### 3. Guest conversion flow
- guest завершает этап
- видит prompt на регистрацию
- проходит auth
- возвращается к pending reward
- получает сертификат и email

### 4. Delivery state model

Нужно заранее спроектировать отдельный delivery state, чтобы потом это было легко вынести на собственный backend.

Минимально нужно различать:
- `not_eligible`
- `eligible_pending_delivery`
- `delivery_in_progress`
- `delivered`
- `delivery_failed`

### 5. Certificate identity and transport contract

Нужно удерживать:

- стабильный `certificateId`;
- связь `certificateId -> stageId`;
- возможность по `certificateId` понять, что именно было выдано;
- возможность в будущем переехать с Firebase-only логики на собственный backend без ломки продуктовой модели.

## Предлагаемый поэтапный подход

### Step 1. Product contract

- зафиксировать, когда именно отправка должна происходить;
- решить, нужна ли отправка автоматически или по явному trigger;
- определить, какой feedback видит пользователь в app.

### Step 2. Delivery foundation

- ввести модель delivery status;
- хранить delivery metadata отдельно от чистого certificate issuance;
- не смешивать `certificate issued` и `certificate emailed`.

### Step 3. Backend / transport layer

- сначала можно использовать промежуточный Firebase-based orchestration слой;
- проектировать так, чтобы затем без боли перейти на собственный backend.

### Step 4. UX completion

- после успешной отправки показать понятное подтверждение;
- при ошибке дать честкий retry path;
- не ломать guest -> auth reward completion flow.

## Acceptance Criteria

- зарегистрированный пользователь получает сертификат на email
- guest можно безопасно провести через conversion flow к сертификату
- delivery state отделён от issuance state

## Текущий статус

`Not started as a full feature`

Сейчас в проекте:
- есть foundation для reward/certificate issuance;
- есть foundation для notification lifecycle;
- но нет полноценной email delivery feature как отдельного продукта и технического слоя.
