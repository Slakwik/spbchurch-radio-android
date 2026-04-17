# Разработка

## Настройка окружения

### 1. Установка Android Studio

Скачайте [Android Studio](https://developer.android.com/studio) Hedgehog (2023.1.1) или новее.

При установке выберите:
- Android SDK
- Android SDK Platform 34
- Build Tools
- Kotlin plugin

### 2. Клонирование репозитория

```bash
git clone https://github.com/Slakwik/spbchurch-radio-android.git
cd spbchurch-radio-android
```

### 3. Открытие проекта

1. File → Open
2. Выберите папку `android/`
3. Android Studio синхронизирует Gradle
4. Дождитесь завершения индексации

### 4. Запуск на эмуляторе/устройстве

1. Подключите устройство или создайте эмулятор
2. Нажмите Run (Shift+F10)
3. Выберите цель

## Архитектура

Приложение использует **MVVM** (Model-View-ViewModel):

```
┌─────────────────────────────────────────────────┐
│                     Views                        │
│  (RadioScreen, TracksScreen, NowPlayingScreen)  │
└─────────────────────┬───────────────────────────┘
                      │ @Composable
┌─────────────────────▼───────────────────────────┐
│                 ViewModels                       │
│              (MainViewModel)                     │
│  - playbackState, tracks, favorites, downloads   │
└─────────────────────┬───────────────────────────┘
                      │ StateFlow
┌─────────────────────▼───────────────────────────┐
│                 Services                         │
│  RadioStreamService, FilePlayerService          │
│  DownloadManager, FavoritesManager              │
└─────────────────────┬───────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────┐
│               Repositories                       │
│    TrackRepository, FavoritesRepository         │
└─────────────────────────────────────────────────┘
```

## Ключевые компоненты

### RadioStreamService
- Управляет воспроизведением радиопотока
- Периодически опрашивает метаданные (каждые 10 сек)
- Использует ExoPlayer для стриминга

### FilePlayerService
- Воспроизводит MP3 файлы из каталога
- Поддерживает shuffle, repeat, play once
- Управляет очередью воспроизведения

### DownloadManager
- Загружает треки в `filesDir/OfflineTracks/`
- Отслеживает прогресс загрузки
- Ведёт индекс в `downloads.json`

### FavoritesManager
- Хранит избранное в `favorites.json`
- Поддерживает добавление, удаление, переупорядочивание

## Gradle

### Версии зависимостей

| Компонент | Версия |
|-----------|--------|
| Kotlin | 1.9.22 |
| Compose BOM | 2024.01.00 |
| Compose Compiler | 1.5.8 |
| Media3 | 1.2.1 |
| Navigation | 2.7.6 |

### Обновление зависимостей

```bash
cd android
./gradlew dependencies --configuration releaseRuntimeClasspath
```

## CI/CD

GitHub Actions автоматически:
- Собирает debug APK при push
- Собирает release APK с master ветки
- Артефакты хранятся 30 дней

См. [`.github/workflows/android.yml`](https://github.com/Slakwik/spbchurch-radio-android/blob/master/.github/workflows/android.yml)

## Полезные команды

```bash
# Сборка debug
./gradlew assembleDebug

# Сборка release
./gradlew assembleRelease

# Очистка
./gradlew clean

# Проверка зависимостей
./gradlew dependencies

# Генерация документации (если есть)
./gradlew dokkaHtml
```

## Внесение изменений

1. Fork репозитория
2. Создайте ветку: `git checkout -b feature/my-feature`
3. Внесите изменения
4. Тестируйте локально
5. Push и создайте Pull Request
