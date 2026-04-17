# SPBChurch Radio

Приложения для прослушивания онлайн-радио и каталога треков церкви ЕХБ «Преображение» (Санкт-Петербург).

## Репозитории

- **iOS:** https://github.com/Slakwik/spbchurch-radio (SwiftUI)
- **Android:** https://github.com/Slakwik/spbchurch-radio-android (Kotlin + Jetpack Compose)

## Перенос на другую платформу

Если вы хотите перенести это приложение на другую платформу (web, desktop, etc.), основные компоненты остаются теми же:

### Общие компоненты

1. **Radio Stream Service** — подключение к Icecast потоку `https://station.spbchurch.ru/radio`
2. **Track Catalog** — парсинг HTML-каталога `https://station.spbchurch.ru/mp3/mp3_files_list.html`
3. **Metadata Polling** — получение текущего трека с `https://station.spbchurch.ru/`
4. **Download Manager** — кэширование MP3 файлов локально
5. **Favorites Manager** — персистентный список избранного (JSON)

### API

- Радиопоток: `https://station.spbchurch.ru/radio` (MP3/AAC)
- Метаданные: HTML-страница с информацией о текущем треке
- Каталог треков: HTML-страница со ссылками на MP3 файлы

### Дизайн

- Neumorphic UI с золотыми акцентами (#D4A23A)
- Цветовая палитра адаптирована под светлую/тёмную тему
- Адаптивный лейаут для телефонов и планшетов

## Разработка

См. README.md в соответствующей папке (ios/ или android/) для инструкций по сборке.
