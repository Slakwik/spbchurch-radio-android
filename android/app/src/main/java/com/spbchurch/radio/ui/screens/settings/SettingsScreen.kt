package com.spbchurch.radio.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import com.spbchurch.radio.ui.theme.LocalThemeManager
import com.spbchurch.radio.ui.theme.ThemeMode
import com.spbchurch.radio.ui.theme.neumorphicRaised

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val themeManager = LocalThemeManager.current
    val currentMode by themeManager.mode.collectAsState()

    var showAbout by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }

    if (showAbout) {
        AboutSheet(onClose = { showAbout = false })
        return
    }
    if (showHelp) {
        HelpSheet(onClose = { showHelp = false })
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Настройки",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        SectionHeader("ОФОРМЛЕНИЕ")
        NeumorphicCard {
            ThemeMode.entries.forEachIndexed { index, mode ->
                ThemeRow(
                    mode = mode,
                    selected = currentMode == mode,
                    onClick = { themeManager.setMode(mode) }
                )
                if (index != ThemeMode.entries.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 62.dp),
                        color = colors.onSurfaceVariant.copy(alpha = 0.15f)
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        SectionHeader("ПОМОЩЬ")
        NeumorphicCard {
            SettingsRow(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                title = "Руководство пользователя",
                subtitle = "Описание функций и подсказки",
                onClick = { showHelp = true }
            )
        }

        Spacer(Modifier.height(18.dp))
        SectionHeader("ССЫЛКИ")
        NeumorphicCard {
            SettingsRow(
                icon = Icons.Filled.Wifi,
                title = "Радиостанция",
                subtitle = "station.spbchurch.ru",
                trailing = {
                    Icon(Icons.Filled.OpenInNew, null, tint = colors.onSurfaceVariant.copy(alpha = 0.5f))
                },
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://station.spbchurch.ru/")))
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(start = 62.dp),
                color = colors.onSurfaceVariant.copy(alpha = 0.15f)
            )
            SettingsRow(
                icon = Icons.Filled.Add,
                title = "Церковь «Преображение»",
                subtitle = "spbchurch.ru",
                trailing = {
                    Icon(Icons.Filled.OpenInNew, null, tint = colors.onSurfaceVariant.copy(alpha = 0.5f))
                },
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://spbchurch.ru/")))
                }
            )
        }

        Spacer(Modifier.height(18.dp))
        SectionHeader("О ПРИЛОЖЕНИИ")
        NeumorphicCard {
            AboutInfoRow()
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = colors.onSurfaceVariant.copy(alpha = 0.15f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAbout = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Описание приложения",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.primary,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Filled.ChevronRight, null, tint = colors.onSurfaceVariant.copy(alpha = 0.4f))
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun NeumorphicCard(content: @Composable Column.() -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .neumorphicRaised(cornerRadius = 16.dp, elevation = 5.dp, blurRadius = 8.dp)
            .background(colors.background, RoundedCornerShape(16.dp))
    ) {
        content()
    }
}

@Composable
private fun ThemeRow(mode: ThemeMode, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBadge(icon = mode.icon, tint = if (selected) colors.primary else colors.onSurfaceVariant)
        Spacer(Modifier.width(14.dp))
        Text(
            text = mode.displayName,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onBackground,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBadge(icon = icon, tint = colors.primary)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.onBackground)
            Text(subtitle, fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
        if (trailing != null) trailing() else Icon(
            Icons.Filled.ChevronRight,
            null,
            tint = colors.onSurfaceVariant.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun SettingsIconBadge(icon: ImageVector, tint: androidx.compose.ui.graphics.Color) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .neumorphicRaised(cornerRadius = 19.dp, elevation = 2.dp, blurRadius = 3.dp)
            .background(colors.background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun AboutInfoRow() {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBadge(icon = Icons.Filled.Info, tint = colors.primary)
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                "SPBChurch Radio",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackground
            )
            Text(
                "Версия 1.0.0",
                fontSize = 12.sp,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AboutSheet(onClose: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        SheetHeader(title = "О приложении", onClose = onClose)
        Spacer(Modifier.height(20.dp))

        AboutCard(title = "О приложении") {
            Text(
                "SPBChurch Radio — мобильное приложение для прослушивания христианского интернет-радио: псалмы, духовные песни и прямой эфир в любое время и в любом месте.",
                fontSize = 14.sp,
                color = colors.onBackground.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Слушайте прямой эфир с отображением текущего трека в реальном времени, просматривайте каталог из более чем 2000 аудиозаписей, добавляйте треки в избранное и загружайте любимые песни для офлайн-прослушивания.",
                fontSize = 14.sp,
                color = colors.onBackground.copy(alpha = 0.85f)
            )
        }

        Spacer(Modifier.height(16.dp))
        AboutCard(title = "Возможности") {
            listOf(
                "Прямой эфир радиостанции с метаданными треков",
                "Каталог 2000+ аудиозаписей с поиском",
                "Случайное и последовательное воспроизведение",
                "Загрузка треков для офлайн-прослушивания",
                "Фоновое воспроизведение",
                "Тёмная и светлая темы оформления"
            ).forEach { item ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("•", color = colors.primary, fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(item, fontSize = 13.sp, color = colors.onBackground.copy(alpha = 0.85f))
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun HelpSheet(onClose: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        SheetHeader(title = "Помощь", onClose = onClose)
        Spacer(Modifier.height(16.dp))

        helpSections.forEach { section ->
            AboutCard(title = section.title) {
                section.items.forEach { item ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text("•", color = colors.primary, fontSize = 14.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(item, fontSize = 13.sp, color = colors.onBackground.copy(alpha = 0.85f))
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SheetHeader(title: String, onClose: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, "Закрыть", tint = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun AboutCard(title: String, content: @Composable () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .neumorphicRaised(cornerRadius = 16.dp, elevation = 5.dp, blurRadius = 8.dp)
            .background(colors.background, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Text(
            title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primary,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        content()
    }
}

private data class HelpSection(val title: String, val items: List<String>)

private val helpSections = listOf(
    HelpSection("Радио", listOf(
        "Большая круглая кнопка запускает или останавливает прямой эфир.",
        "Дерево на фоне светится золотом, когда идёт эфир.",
        "Под кнопкой отображается название текущего трека из метаданных.",
        "«Найти в библиотеке» подставляет название играющего трека в поиск каталога."
    )),
    HelpSection("Треки", listOf(
        "Поиск фильтрует каталог в реальном времени.",
        "Меню сортировки в заголовке: по умолчанию, А–Я, Я–А.",
        "Сердечко добавляет трек в избранное.",
        "Стрелка загружает трек в офлайн.",
        "Маленькая круглая кнопка play запускает воспроизведение."
    )),
    HelpSection("Избранное", listOf(
        "Список хранится локально и работает без интернета.",
        "Заполненное сердце убирает трек из избранного."
    )),
    HelpSection("Загрузки", listOf(
        "Сохранённые треки доступны без интернета.",
        "Кнопка-корзина удаляет файл с устройства."
    )),
    HelpSection("Плеер", listOf(
        "Виджет «Микс/Повтор/До конца» переключает порядок воспроизведения.",
        "iPod-колесо: центр — play/pause, стороны — предыдущий/следующий трек.",
        "Стрелка вниз сворачивает плеер — воспроизведение продолжается."
    )),
    HelpSection("Настройки", listOf(
        "Системная / светлая / тёмная тема — выбор сохраняется.",
        "Фоновое воспроизведение работает при свёрнутом приложении."
    ))
)
