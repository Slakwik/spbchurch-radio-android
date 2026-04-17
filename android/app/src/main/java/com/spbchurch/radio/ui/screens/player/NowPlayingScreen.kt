package com.spbchurch.radio.ui.screens.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spbchurch.radio.data.model.DownloadState
import com.spbchurch.radio.data.model.PlaybackOrder
import com.spbchurch.radio.ui.components.ArtworkViewFrosted
import com.spbchurch.radio.ui.components.MiniEqualizerView
import com.spbchurch.radio.ui.theme.AppColors
import com.spbchurch.radio.ui.theme.neumorphicRaised
import com.spbchurch.radio.viewmodel.MainViewModel
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun NowPlayingScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    val progress = if (playbackState.isRadioMode || playbackState.duration <= 0) 0f
    else (playbackState.position.toFloat() / playbackState.duration).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Header
            PlayerHeader(
                title = if (playbackState.isRadioMode) "Радио" else "Музыка",
                isPlaying = playbackState.isPlaying,
                onBack = onBack
            )

            Spacer(Modifier.height(8.dp))

            // Dotted progress ring + artwork
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DottedProgressRing(
                    progress = progress,
                    artwork = playbackState.artwork,
                    title = playbackState.currentTrack?.title ?: ""
                )
            }

            Spacer(Modifier.height(20.dp))

            // Track title + station
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = playbackState.currentTitle.ifBlank {
                        playbackState.currentTrack?.title ?: "Выберите трек"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "SPBChurch Radio",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurfaceVariant
                )
            }

            // Seek slider (file mode only)
            if (!playbackState.isRadioMode && playbackState.duration > 0) {
                Spacer(Modifier.height(20.dp))
                SeekSlider(
                    progress = progress,
                    position = playbackState.position,
                    duration = playbackState.duration,
                    onSeek = { newProgress ->
                        viewModel.seekTo((newProgress * playbackState.duration).toLong())
                    },
                    modifier = Modifier.padding(horizontal = 36.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            // Bottom controls
            BottomControls(
                playbackState = playbackState,
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            )
        }
    }
}

@Composable
private fun PlayerHeader(
    title: String,
    isPlaying: Boolean,
    onBack: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground,
            modifier = Modifier.weight(1f)
        )
        MiniEqualizerView(isPlaying = isPlaying)
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Закрыть",
                tint = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DottedProgressRing(
    progress: Float,
    artwork: ByteArray?,
    title: String
) {
    val colors = MaterialTheme.colorScheme
    val ringSize = 270.dp
    val artSize = 220.dp
    val dotCount = 36

    Box(
        modifier = Modifier.size(ringSize + 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(ringSize)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f
            for (i in 0 until dotCount) {
                val normalized = i.toFloat() / dotCount
                val angle = Math.toRadians((normalized * 360.0) - 90.0)
                val x = center.x + radius * cos(angle).toFloat()
                val y = center.y + radius * sin(angle).toFloat()
                val isFilled = normalized <= progress
                val dotRadius = if (i % 4 == 0) 3.5f.dp.toPx() else 2.25f.dp.toPx()
                drawCircle(
                    color = if (isFilled) AppColors.Accent
                    else colors.onSurfaceVariant.copy(alpha = 0.15f),
                    radius = dotRadius,
                    center = Offset(x, y)
                )
            }
        }
        ArtworkViewFrosted(
            artwork = artwork,
            title = title,
            size = artSize,
            modifier = Modifier.neumorphicRaised(
                cornerRadius = artSize / 2,
                elevation = 10.dp,
                blurRadius = 20.dp
            )
        )
    }
}

@Composable
private fun SeekSlider(
    progress: Float,
    position: Long,
    duration: Long,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val density = LocalDensity.current

    Column(modifier = modifier) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(20.dp)) {
            val widthPx = with(density) { maxWidth.toPx() }
            val filled = (progress * maxWidth.value).dp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(colors.onSurfaceVariant.copy(alpha = 0.15f))
                    .pointerInput(widthPx) {
                        detectTapGestures { offset ->
                            val newProgress = (offset.x / widthPx).coerceIn(0f, 1f)
                            onSeek(newProgress)
                        }
                    }
            )
            Box(
                modifier = Modifier
                    .width(filled.coerceAtLeast(0.dp))
                    .height(4.dp)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .background(colors.primary)
            )
        }
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(position),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = colors.onBackground
            )
            Text(
                text = formatTime(duration),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BottomControls(
    playbackState: com.spbchurch.radio.data.model.PlaybackState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left column: order toggle + actions menu
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OrderToggleWidget(
                order = playbackState.playbackOrder,
                onCycle = {
                    val next = when (playbackState.playbackOrder) {
                        PlaybackOrder.SHUFFLE -> PlaybackOrder.REPEAT
                        PlaybackOrder.REPEAT -> PlaybackOrder.PLAY_ONCE
                        PlaybackOrder.PLAY_ONCE -> PlaybackOrder.SHUFFLE
                    }
                    viewModel.setPlaybackOrder(next)
                }
            )
            ActionsMenuWidget(
                track = playbackState.currentTrack,
                viewModel = viewModel
            )
        }

        // Right: click wheel
        ClickWheel(
            isPlaying = playbackState.isPlaying,
            onPlayPause = { viewModel.togglePlayPause() },
            onPrevious = { viewModel.previousTrack() },
            onNext = { viewModel.nextTrack() }
        )
    }
}

@Composable
private fun OrderToggleWidget(order: PlaybackOrder, onCycle: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val (icon, label) = when (order) {
        PlaybackOrder.SHUFFLE -> Icons.Filled.Shuffle to "Микс"
        PlaybackOrder.REPEAT -> Icons.Filled.Repeat to "Повтор"
        PlaybackOrder.PLAY_ONCE -> Icons.Filled.RepeatOne to "До конца"
    }
    NeumorphicWidget(onClick = onCycle) {
        Icon(icon, null, tint = colors.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.onSurfaceVariant)
    }
}

@Composable
private fun ActionsMenuWidget(
    track: com.spbchurch.radio.data.model.Track?,
    viewModel: MainViewModel
) {
    val colors = MaterialTheme.colorScheme
    var open by remember { mutableStateOf(false) }
    Box {
        NeumorphicWidget(onClick = { if (track != null) open = true }) {
            Icon(Icons.Filled.MoreHoriz, null, tint = colors.primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text("Действия", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.onSurfaceVariant)
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            track?.let {
                val isFav = viewModel.isFavorite(it)
                DropdownMenuItem(
                    text = { Text(if (isFav) "Убрать из избранного" else "Добавить в избранное") },
                    leadingIcon = {
                        Icon(
                            if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            null
                        )
                    },
                    onClick = {
                        viewModel.toggleFavorite(it)
                        open = false
                    }
                )
                val state = viewModel.getDownloadState(it)
                if (state == DownloadState.None || state == DownloadState.Failed) {
                    DropdownMenuItem(
                        text = { Text("Скачать трек") },
                        leadingIcon = { Icon(Icons.Filled.Download, null) },
                        onClick = {
                            viewModel.downloadTrack(it)
                            open = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NeumorphicWidget(
    onClick: () -> Unit,
    content: @Composable Column.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clip(RoundedCornerShape(16.dp))
            .neumorphicRaised(cornerRadius = 16.dp, elevation = 4.dp, blurRadius = 6.dp)
            .background(colors.background, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun ClickWheel(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val wheelSize = 150.dp
    val centerSize = 52.dp
    val density = LocalDensity.current
    val wheelSizePx = with(density) { wheelSize.toPx() }

    Box(
        modifier = Modifier.size(wheelSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(wheelSize)
                .clip(CircleShape)
                .neumorphicRaised(cornerRadius = wheelSize / 2, elevation = 6.dp, blurRadius = 10.dp)
                .background(colors.background, CircleShape)
        )

        // Quadrant icons (visual only — interaction via icon buttons below)
        WheelIcon(
            icon = Icons.Filled.SkipPrevious,
            offset = IntOffset(-(wheelSizePx * 0.28f).roundToInt(), 0),
            tint = colors.onBackground.copy(alpha = 0.5f),
            onClick = onPrevious
        )
        WheelIcon(
            icon = Icons.Filled.SkipNext,
            offset = IntOffset((wheelSizePx * 0.28f).roundToInt(), 0),
            tint = colors.onBackground.copy(alpha = 0.5f),
            onClick = onNext
        )
        Box(
            modifier = Modifier
                .size(44.dp)
                .offset { IntOffset(0, -(wheelSizePx * 0.28f).roundToInt()) },
            contentAlignment = Alignment.Center
        ) {
            Text("MENU", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = colors.onBackground.copy(alpha = 0.4f))
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .offset { IntOffset(0, (wheelSizePx * 0.28f).roundToInt()) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Pause,
                contentDescription = null,
                tint = colors.onBackground.copy(alpha = 0.4f),
                modifier = Modifier.size(14.dp)
            )
        }

        // Center play/pause
        Box(
            modifier = Modifier
                .size(centerSize)
                .clip(CircleShape)
                .neumorphicRaised(cornerRadius = centerSize / 2, elevation = 2.dp, blurRadius = 4.dp)
                .background(colors.background, CircleShape)
                .clickable(onClick = onPlayPause),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Пауза" else "Воспроизвести",
                tint = if (isPlaying) colors.primary else colors.onBackground,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun WheelIcon(
    icon: ImageVector,
    offset: IntOffset,
    tint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .offset { offset }
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
    }
}

private fun formatTime(millis: Long): String {
    if (millis <= 0) return "0:00"
    val total = millis / 1000
    val m = total / 60
    val s = total % 60
    return "%d:%02d".format(m, s)
}
