# Project Context: Football Quiz (Current)

Этот файл служит основным источником контекста для ИИ-помощника. Здесь фиксируется текущее состояние проекта, выполненные задачи и план по модернизации.

## 📌 Обзор проекта
- **Название:** Football Quiz
- **Версия:** 1.3.0
- **Основной стек:** Kotlin, Android SDK (API 35/Android 15), Single Activity Architecture (SAA), Jetpack Navigation Component, Firebase (Firestore, Auth, Analytics, Messaging), Realm DB, Coroutines.
- **Цель:** Полная модернизация приложения, переход на SAA, внедрение Google Sign-In и динамическая загрузка контента из Firestore.

---

## 🛠 Выполненные задачи
- **Миграция на SAA:** Весь проект переведен в `MainActivity` с использованием Fragments.
- **Навигация:** Внедрен `Navigation Component`.
- **Сборка:** Переход на `Version Catalog` (`libs.versions.toml`).
- **Язык:** Практически полная миграция на Kotlin.
- **UI:** Поддержка Edge-to-Edge (Android 15), внедрены кастомные диалоги (New Game, Language) с дизайном из версии 1.8.
- **Bug Fixes:** Исправлено отображение вопросов/ответов в PlayFragment, устранены ошибки ViewBinding и отсутствующих ресурсов.
- **Offline:** Реализована логика офлайн-игры (загрузка из Firestore -> сохранение в Realm).

---

## 🚀 Текущий план модернизации (Backlog)

1.  **AdMob: Адаптивные баннеры**
    - **Задача:** Перенос логики `Adaptive Banner` из версии 1.8 в текущий проект.
    - **Статус:** ✅ Завершено.

2.  **Firebase: Fetch Questions from Firestore**
    - **Задача:** Переход с локальных ресурсов (`GetQuestions.kt` -> XML) на загрузку из Firestore, как это было реализовано в 1.8.
    - **Статус:** ✅ Завершено (создан `FirestoreQuestionService.kt`).

3.  **Auth: Google Sign-In**
    - **Задача:** Реализация авторизации через Google для сохранения прогресса.
    - **Статус:** ✅ Завершено (интегрировано в `AuthManager` и `StartPageFragment`).

4.  **UI Modernization: Custom Dialogs**
    - **Задача:** Замена стандартных `AlertDialog` на кастомные диалоги с дизайном оригинальной игры.
    - **Статус:** ✅ Завершено.

5.  **Архитектура: ViewModel & Flow**
    - **Задача:** Отказ от `GlobalScope` и переход на архитектурные компоненты (Coroutines, Flow, ViewModel).
    - **Статус:** ⏳ В планах.

6.  **Очистка:**
    - **Задача:** Удаление `TestActivity.java` и `TouchActivity.java`, перевод на фрагменты. Перевод кастомных вью (`CircleImageView`, `JustifiedTextView`) на Kotlin.
    - **Статус:** ✅ Завершено.

---

## 📝 Заметки
- **Google Sign-In:** Необходимо настроить `google-services.json` с актуальными SHA-ключами.
- **Firestore:** Убедиться, что структура данных в Firestore соответствует `QuestionModel`.

---
*Последнее обновление: 02.04.2026*
