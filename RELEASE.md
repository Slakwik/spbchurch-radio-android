# Выпуск релизов

## Артефакты

Release-workflow запускается по тегу `v*` и собирает три файла:

| Файл | Назначение |
|---|---|
| `spbchurch-radio-vX.Y.Z-debug.apk` | Для разработки и `adb install` |
| `spbchurch-radio-vX.Y.Z-release.apk` | Для sideload (TG, сайт, APK-зеркала) |
| `spbchurch-radio-vX.Y.Z-release.aab` | Android App Bundle — для Google Play |

Все три прикладываются к GitHub Release.

## Выпустить версию

```bash
git checkout master
git pull
git tag v1.0.0
git push origin v1.0.0
```

После push тега:
1. Запускается `.github/workflows/release.yml`.
2. Gradle собирает debug APK, release APK и release AAB.
3. `softprops/action-gh-release@v2` создаёт GitHub Release и прикладывает артефакты.

Можно также запустить вручную: Actions → Release → Run workflow, указав тег.

## Подпись для Google Play

Без своего keystore release-сборки подписываются debug-ключом. Play Store такие файлы не примет — для публикации нужен собственный ключ.

### 1. Создать keystore один раз

На локальной машине:

```bash
keytool -genkey -v \
    -keystore spbchurch-release.keystore \
    -alias spbchurch \
    -keyalg RSA -keysize 2048 -validity 10000
```

Ответить на вопросы (CN, Organization и т.д.), задать два пароля — keystore и key (обычно можно совпадающие).

**Сохрани файл и пароли в надёжном месте** (менеджер паролей, зашифрованный архив). Потеря ключа = невозможность обновить приложение в Play Store.

### 2. Добавить секреты в GitHub

Settings → Secrets and variables → Actions → New repository secret. Нужны четыре секрета:

| Имя | Значение |
|---|---|
| `ANDROID_KEYSTORE_BASE64` | `base64 -w0 spbchurch-release.keystore` (вывод команды) |
| `ANDROID_KEYSTORE_PASSWORD` | пароль от keystore |
| `ANDROID_KEY_ALIAS` | `spbchurch` (или свой alias) |
| `ANDROID_KEY_PASSWORD` | пароль от ключа |

Команда для base64 (macOS/Linux):
```bash
base64 -w0 spbchurch-release.keystore | pbcopy   # macOS
base64 -w0 spbchurch-release.keystore            # Linux — скопировать вывод
```

После этого workflow автоматически подпишет release APK и AAB правильным ключом.

## Публикация в Google Play

### Разовые шаги

1. [Google Play Console](https://play.google.com/console) → **Create app**.
2. Тип: **App**. Free. Согласиться с декларациями.
3. **Dashboard → Set up your app** — пройти все обязательные пункты:
   - App access (полностью открытое)
   - Ads (нет)
   - Content rating (музыкальное приложение)
   - Target audience (Adults)
   - News app (нет)
   - Data safety (данные не собираются — см. ниже)
   - Privacy policy URL (нужна страница, напр. в GitHub Pages)

### Загрузка сборки

1. Create release → Production (или Internal testing / Closed testing).
2. Upload → выбрать `spbchurch-radio-v1.0.0-release.aab`.
3. Release notes на русском (несколько строк, что нового).
4. **Save → Review release → Start rollout.**

Первая публикация уходит на ручную модерацию — обычно 1–7 дней.

### Store listing (контент страницы)

Все ассеты собраны в `playstore/` в корне репозитория:

- `short-description.txt` (80 символов)
- `full-description.txt` (до 4000 символов)
- `screenshots/*.png` — минимум 2, лучше 5
- `icon-512.png` — иконка приложения 512×512 PNG
- `feature-graphic.png` — баннер 1024×500 PNG (нужно создать отдельно)

### Data safety

В форме укажи:
- **Data collection**: None
- **Data sharing**: None
- **Security practices**: Data encrypted in transit (HTTPS используется для потока и каталога)

### Privacy policy

Простой текст можно разместить в wiki/GitHub Pages этого репозитория. Минимум: название приложения, отсутствие сбора данных, контакт для вопросов.

## Версионирование

`versionCode` и `versionName` лежат в `android/app/build.gradle.kts`. Для каждого Play-релиза `versionCode` **должен увеличиваться** (целое). Удобный формат:

- `1.0.0` → `versionCode = 1`
- `1.0.1` → `versionCode = 2`
- `1.1.0` → `versionCode = 3`

Можно автоматизировать: вычислять `versionCode` из тега или даты коммита. Пока проще править руками перед тегом.
