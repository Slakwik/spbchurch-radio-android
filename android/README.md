# SPBChurch Radio для Android

Android-версия приложения для онлайн-радио церкви ЕХБ «Преображение» (Санкт-Петербург).

## Возможности

- **Прямой эфир** — стриминг радиопотока с отображением текущего трека (Icecast)
- **Библиотека треков** — поиск и воспроизведение из каталога 2000+ аудиозаписей
- **Shuffle и навигация** — случайный порядок воспроизведения, кнопки вперёд/назад
- **Офлайн-режим** — загрузка треков для прослушивания без интернета
- **Фоновое воспроизведение** — музыка продолжает играть при сворачивании приложения
- **Избранное** — собственный список любимых треков
- **Neumorphic UI** — мягкий объёмный дизайн в стиле AirOS
- **Адаптивный лейаут** — поддержка portrait и landscape на телефонах и планшетах

## Сборка

### Требования

- Android Studio Hedgehog (2023.1.1) или новее
- JDK 17+
- Android SDK 34

### Шаги

1. Откройте Android Studio
2. Выберите "Open an existing project"
3. Укажите папку `android/` этого репозитория
4. Дождитесь синхронизации Gradle
5. Нажмите Run (Shift+F10) для запуска на устройстве/эмуляторе

### Сборка из командной строки

```bash
cd android
./gradlew assembleDebug
```

APK будет находиться в `app/build/outputs/apk/debug/`

### Сборка release-версии

```bash
./gradlew assembleRelease
```

## Архитектура

- **Kotlin** — основной язык программирования
- **Jetpack Compose** — декларативный UI
- **MVVM** — паттерн архитектуры
- **Media3 (ExoPlayer)** — аудиовоспроизведение и буферизация
- **Kotlin Coroutines & Flow** — асинхронное программирование
- **Room/JSON** — персистентность избранного и загрузок

## Структура проекта

```
android/
├── app/src/main/
│   ├── java/com/spbchurch/radio/
│   │   ├── RadioApplication.kt         — Application class
│   │   ├── MainActivity.kt             — Main activity
│   │   ├── data/
│   │   │   ├── model/                  — Track, PlaybackState, AppSettings
│   │   │   ├── repository/             — TrackRepository, FavoritesRepository
│   │   │   └── service/                — RadioStreamService, FilePlayerService, DownloadManager
│   │   ├── ui/
│   │   │   ├── theme/                  — NeumorphicColors, Theme
│   │   │   ├── components/             — Reusable UI components
│   │   │   └── screens/                — Radio, Tracks, Favorites, Downloads, Settings, Player
│   │   ├── viewmodel/                  — MainViewModel
│   │   └── service/                    — RadioPlaybackService (MediaSession)
│   └── res/                            — Resources (strings, colors, themes)
└── build.gradle.kts                     — Build configuration
```

## Цветовая палитра

| Цвет | Светлая | Тёмная | Назначение |
|------|---------|--------|------------|
| Background | #F0F3F5 | #1C1C24 | Основной фон |
| Surface | #F2F2F5 | #26262E | Карточки |
| Accent | #D4A23A | #E8BE5A | Золотой акцент |
| Text Primary | #1F1F24 | #F2F2F5 | Основной текст |
| Text Secondary | #737380 | #9999A6 | Вторичный текст |

## API эндпоинты

- Радио: `https://station.spbchurch.ru/radio`
- Метаданные: `https://station.spbchurch.ru/`
- Каталог треков: `https://station.spbchurch.ru/mp3/mp3_files_list.html`

## Лицензия

MIT License
