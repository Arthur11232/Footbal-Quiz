# Notification Design Flow

Документ описывает рекомендованный UX-flow для notification-секции в `Football Quiz` до начала полноценной реализации `Phase 3`.

Дата фиксации: **26 июня 2026**

Связанные документы:
- [android_stability_achievements_roadmap_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/android_stability_achievements_roadmap_ru.md)
- [task_phase3_notifications_foundation_ru.md](/Users/Arthu/Documents/GitHub/Footbal-Quiz/docs/task_phase3_notifications_foundation_ru.md)

## 1. Главная идея

- notification icon появляется только у авторизованного пользователя;
- permission на push notifications запрашивается только после login;
- по клику на icon открывается отдельный экран `Notifications`;
- notifications должны усиливать progression / reward flow, а не выглядеть как отвлекающий шум.

## 2. Рекомендуемый MVP

### 2.1. Где показываем icon

Рекомендуемый вариант:
- показывать bell icon в правом верхнем углу основных authenticated screen-ов;
- в первую очередь:
  - стартовый экран;
  - профиль;
- у guest пользователя icon не показывать вообще.

### 2.2. Badge / индикатор

- если новых notifications нет:
  - обычная иконка bell;
- если есть unread:
  - маленькая золотистая badge-dot;
- если есть reward-related notification:
  - badge может быть с числом или более ярким акцентом.

## 3. Permission Flow

### 3.1. Когда спрашивать

Не сразу после открытия приложения.

Рекомендуемый flow:
1. пользователь логинится;
2. в ближайшем устойчивом authenticated состоянии видит мягкий in-app prompt;
3. только после подтверждения in-app prompt показываем system permission dialog.

### 3.2. Почему так

- не ломаем early UX;
- не просим permission у guest;
- пользователь понимает, зачем это нужно:
  - прогресс;
  - завершённые этапы;
  - награды;
  - будущие сертификаты.

### 3.3. Текст soft prompt

Заголовок:
- `Не пропускай свой прогресс`

Текст:
- `Получай уведомления о завершённых этапах, наградах и важных обновлениях Football Quiz.`

Кнопки:
- `Включить`
- `Позже`

### 3.4. Если permission denied

Не спамить повторно system dialog.

Вместо этого:
- на экране `Notifications` показать explain-card:
  - `Уведомления выключены`
  - `Чтобы получать награды и напоминания, включи уведомления в настройках устройства.`
  - CTA: `Открыть настройки`

## 4. Notification Center Screen

### 4.1. Назначение

Это отдельный screen, а не popup.

Экран нужен для:
- истории уведомлений;
- reward-related сообщений;
- точки входа в notification preferences;
- fallback UX, если push permission выключен.

### 4.2. Структура экрана

Рекомендуемая структура:
- top app bar:
  - back
  - title `Notifications`
- optional action:
  - `Mark all as read`
- body:
  - список карточек уведомлений
  - или empty state
  - или disabled-permission state

### 4.3. Типы карточек

#### Progress
- `Ты близко к следующему этапу`
- пример:
  - `До разблокировки Ronaldo vs Messi осталось 8 правильных ответов.`

#### Reward
- `Этап завершён`
- пример:
  - `France завершён. Сертификат уже доступен в профиле.`

#### Reminder
- `Продолжи с того места, где остановился`
- пример:
  - `UEFA ждёт тебя. Вернись и доведи этап до конца.`

#### Update
- `Новые вопросы уже в игре`
- пример:
  - `Мы обновили банк вопросов и добавили свежий футбольный контент.`

## 5. Empty / Disabled / Filled States

### 5.1. Empty state

Когда notifications пока нет:
- title: `Пока пусто`
- text:
  - `Здесь появятся награды, напоминания и важные обновления Football Quiz.`

### 5.2. Permission disabled state

- title: `Уведомления выключены`
- helper text:
  - `Включи уведомления, чтобы не пропустить завершённые этапы и награды.`
- CTA:
  - `Открыть настройки`

### 5.3. Filled state

Карточки сортируются:
- сначала unread;
- затем reward/high-value;
- затем reminder/update.

## 6. Recommended First Scenarios

Для MVP я бы заложил только 4 сценария:

1. Stage completion / reward
- завершён этап;
- показываем notification card и будущий push.

2. Unlock milestone
- открыт новый большой блок:
  - `UEFA`
  - `World`
  - `Versus`

3. Return reminder
- пользователь давно не возвращался;
- аккуратное reminder notification.

4. Content update
- добавлены новые вопросы / обновлён контент.

## 7. Связь с текущим продуктом

С учётом уже существующего flow:
- completed stages уже считаются;
- certificates уже частично есть;
- profile уже показывает certificate-related данные;
- значит notifications должны опираться на уже существующий progression/reward слой.

То есть notifications не должны изобретать новую domain logic.

Они должны читать:
- completed stages;
- unread reward events;
- auth state;
- push permission state.

## 8. Рекомендуемый UX-порядок

Лучший порядок для пользователя:

1. login
2. появление bell icon
3. soft prompt на notifications
4. system permission
5. отдельный экран `Notifications`
6. reward / reminder / update карточки
7. позже — переход к email/certificate pipeline

## 9. Что я рекомендую для реализации после согласования

1. Добавить notification bell только для authenticated user
2. Добавить отдельный `NotificationsFragment`
3. Добавить local UI model для notification items
4. Добавить soft prompt до system permission
5. Потом уже подключать FCM token lifecycle и серверную доставку

## 10. Итоговое решение

Если коротко, рекомендуемый продуктовый подход такой:

- guest:
  - не видит bell icon
  - не получает permission prompt
- authenticated user:
  - видит bell icon справа сверху
  - может открыть отдельный notification screen
  - получает permission flow только после login
- notification center:
  - отдельный экран
  - empty state / disabled state / filled state
  - reward-first UX
