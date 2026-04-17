# SPBChurch Radio

Android-приложение для прослушивания онлайн-радио и каталога аудиозаписей церкви ЕХБ «Преображение» (Санкт-Петербург).

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Language](https://img.shields.io/badge/Language-Kotlin-blue)
![UI Framework](https://img.shields.io/badge/UI-Jetpack%20Compose-purple)
![License](https://img.shields.io/badge/License-MIT-yellow)

## Возможности

### Прямой эфир
- Стриминг радиопотока в реальном времени
- Отображение текущего играющего трека
- Индикатор "ЭФИР" с пульсирующей точкой
- Буферизация для стабильного воспроизведения

### Библиотека треков
- Каталог из 2000+ аудиозаписей
- Поиск по названию
- Сортировка: по умолчанию, А–Я, Я–А
- Обложки альбомов

### Воспроизведение
- Три режима воспроизведения:
  - **Микс** — случайный порядок
  - **Повтор** — по кругу
  - **До конца** — один раз
- Кнопки вперёд/назад
- Перемотка трека

### Офлайн-режим
- Загрузка треков для прослушивания без интернета
- Отслеживание прогресса загрузки
- Локальное хранение файлов

### Избранное
- Собственный список любимых треков
- Быстрое добавление/удаление
- Свайп для удаления

### Фоновое воспроизведение
- Музыка играет при свёрнутом приложении
- Управление с экрана блокировки
- Уведомление с кнопками управления

## Скриншоты

| Радио | Треки | Избранное |
|:-----:|:-----:|:---------:|
| ![Radio](https://github.com/Slakwik/spbchurch-radio-android/raw/main/screenshots/radio.png) | ![Tracks](https://github.com/Slakwik/spbchurch-radio-android/raw/main/screenshots/tracks.png) | ![Favorites](https://github.com/Slakwik/spbchurch-radio-android/raw/main/screenshots/favorites.png) |

| Плеер | Загрузки | Настройки |
|:-----:|:--------:|:---------:|
| ![Player](https://github.com/Slakwik/spbchurch-radio-android/raw/main/screenshots/player.png) | ![Downloads](https://github.com/Slakwik/spbchurch-radio-android/raw/main/screenshots/downloads.png) | ![Settings](https://github.com/Slakwik/spbchurch-radio-android/raw/main/screenshots/settings.png) |

## Дизайн

Приложение использует **Neumorphic UI** — мягкий объёмный дизайн с тенями, создающий эффект "выпуклых" элементов.

### Цветовая палитра

| Элемент | Светлая тема | Тёмная тема |
|---------|-------------|-------------|
| Фон | `#F0F3F5` | `#1C1C24` |
| Поверхность | `#F2F2F5` | `#26262E` |
| Акцент | `#D4A23A` | `#E8BE5A` |
| Текст основной | `#1F1F24` | `#F2F2F5` |
| Текст вторичный | `#737380` | `#9999A6` |

### Темы
- **Системная** — следует настройкам телефона
- **Светлая** — всегда светлая
- **Тёмная** — всегда тёмная

## Технологии

- **Kotlin** — основной язык
- **Jetpack Compose** — декларативный UI
- **MVVM** — архитектура приложения
- **Media3 (ExoPlayer)** — аудиовоспроизведение
- **Kotlin Coroutines & Flow** — асинхронность
- **Coil** — загрузка изображений
- **JSON** — персистентность данных

## Сборка

### Требования
- Android Studio Hedgehog (2023.1.1)+
- JDK 17+
- Android SDK 34
- minSdk 26

### Локальная сборка

```bash
# Клонирование
git clone https://github.com/Slakwik/spbchurch-radio-android.git
cd spbchurch-radio-android

# Сборка debug APK
cd android
./gradlew assembleDebug

# APK будет в:
# android/app/build/outputs/apk/debug/app-debug.apk
```

### GitHub Actions

При каждом push автоматически собирается debug APK.

1. Перейдите в [Actions](https://github.com/Slakwik/spbchurch-radio-android/actions)
2. Выберите последний workflow run
3. Скачайте artifact `app-debug`

## Структура проекта

```
android/
├── app/src/main/
│   ├── java/com/spbchurch/radio/
│   │   ├── MainActivity.kt          # Точка входа
│   │   ├── RadioApplication.kt      # Application class
│   │   ├── data/
│   │   │   ├── model/               # Track, PlaybackState, AppSettings
│   │   │   ├── repository/          # TrackRepository, FavoritesRepository
│   │   │   └── service/             # RadioStreamService, FilePlayerService
│   │   ├── ui/
│   │   │   ├── theme/               # NeumorphicColors, Theme
│   │   │   ├── components/          # NeumorphicButton, PlayButton, etc.
│   │   │   └── screens/             # Radio, Tracks, Favorites, Downloads, Settings, Player
│   │   └── viewmodel/               # MainViewModel
│   └── res/
│       ├── values/                  # strings, colors, themes
│       └── drawable/                # icons, launcher
└── build.gradle.kts
```

## API

Приложение использует следующие эндпоинты:

| Ресурс | URL |
|--------|-----|
| Радиопоток | `https://station.spbchurch.ru/radio` |
| Метаданные | `https://station.spbchurch.ru/` |
| Каталог треков | `https://station.spbchurch.ru/mp3/mp3_files_list.html` |

## Связанные проекты

- [iOS версия](https://github.com/Slakwik/spbchurch-radio) — SwiftUI приложение для iPhone/iPad

## Лицензия

MIT License — используйте свободно!
