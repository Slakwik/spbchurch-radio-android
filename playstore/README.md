# Play Store ассеты

Материалы для заполнения страницы приложения в Google Play Console.

## Контент

| Файл | Назначение | Лимит |
|---|---|---|
| `short-description.txt` | Короткое описание | 80 символов |
| `full-description.txt` | Полное описание | 4000 символов |
| `screenshots/` | Скриншоты интерфейса | мин. 2, макс. 8, 16:9 или 9:16 |

## Что надо ещё подготовить отдельно

| Ассет | Формат | Размер |
|---|---|---|
| App icon | PNG | 512×512, без прозрачности |
| Feature graphic | PNG/JPG | 1024×500 |
| Android screenshots | PNG/JPG | мин. 1080×1920 (portrait) |

Скриншоты сейчас взяты из iOS-версии как черновик — перед публикацией сделай снимки Android-приложения на эмуляторе или устройстве.

Для иконки можно переэкспортировать `android/app/src/main/res/mipmap-*` в 512×512 PNG. Feature graphic обычно делают в Figma — тот же мотив дерева с акцентной золотой подсветкой.

## Data safety (форма в Play Console)

- **Data collection**: None
- **Data sharing**: None
- **Security**: "Data is encrypted in transit" (HTTPS для потока и каталога)
- **Data deletion**: N/A — данные хранятся только локально

## Privacy policy

Нужна публичная страница. Варианты:
1. GitHub Pages этого репозитория (`docs/privacy.html`)
2. Раздел на сайте spbchurch.ru

Минимальный текст: название приложения, отсутствие сбора данных, контакт для вопросов.
