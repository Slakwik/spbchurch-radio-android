package com.spbchurch.radio.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spbchurch.radio.R
import com.spbchurch.radio.ui.components.MaterialCard

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    var showThemeDialog by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf("Системная") }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium,
            color = colors.onSurface,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        MaterialCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "Внешний вид",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showThemeDialog = true }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Palette,
                            contentDescription = null,
                            tint = colors.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.theme),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.onSurface
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = selectedTheme,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        MaterialCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "Ресурсы",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                SettingsItem(
                    icon = Icons.Default.Radio,
                    title = stringResource(R.string.website),
                    subtitle = "station.spbchurch.ru",
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://station.spbchurch.ru")))
                    }
                )

                Divider(color = colors.onSurfaceVariant.copy(alpha = 0.2f))

                SettingsItem(
                    icon = Icons.Default.Church,
                    title = stringResource(R.string.church_website),
                    subtitle = "spbchurch.ru",
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://spbchurch.ru")))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        MaterialCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "Информация",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    title = stringResource(R.string.help),
                    subtitle = "Как пользоваться приложением",
                    onClick = { showHelpDialog = true }
                )

                Divider(color = colors.onSurfaceVariant.copy(alpha = 0.2f))

                SettingsItem(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.about),
                    subtitle = "Версия 1.0.0",
                    onClick = { showAboutDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "SPBChurch Radio v1.0.0",
            style = MaterialTheme.typography.labelMedium,
            color = colors.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(stringResource(R.string.theme)) },
            text = {
                Column {
                    listOf("Системная", "Светлая", "Тёмная").forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTheme = theme
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTheme == theme,
                                onClick = {
                                    selectedTheme = theme
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(theme)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Закрыть")
                }
            },
            containerColor = colors.surface
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text(stringResource(R.string.about)) },
            text = {
                Column {
                    Text(stringResource(R.string.about_description))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Возможности:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text("- Прямой эфир радиостанции")
                    Text("- Каталог 2000+ треков")
                    Text("- Поиск и сортировка")
                    Text("- Избранное")
                    Text("- Офлайн-загрузки")
                    Text("- Фоновое воспроизведение")
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Закрыть")
                }
            },
            containerColor = colors.surface
        )
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text(stringResource(R.string.help)) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    HelpSection("Радио", "Нажмите кнопку Play для начала воспроизведения эфира. Название текущего трека отображается на экране.")
                    HelpSection("Треки", "Просматривайте каталог, используйте поиск и сортировку. Нажмите на трек для воспроизведения.")
                    HelpSection("Избранное", "Добавляйте треки в избранное, нажимая на сердечко.")
                    HelpSection("Загрузки", "Скачивайте треки для прослушивания без интернета. Загруженные треки доступны офлайн.")
                    HelpSection("Плеер", "Мини-плеер внизу экрана позволяет управлять воспроизведением. Нажмите на него для полноэкранного режима.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Закрыть")
                }
            },
            containerColor = colors.surface
        )
    }
}

@Composable
private fun HelpSection(title: String, content: String) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = colors.primary
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = colors.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.onSurfaceVariant
        )
    }
}
