package com.spbchurch.radio.ui.screens.radio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spbchurch.radio.ui.components.*
import com.spbchurch.radio.ui.theme.Theme
import com.spbchurch.radio.viewmodel.MainViewModel

@Composable
fun RadioScreen(
    viewModel: MainViewModel,
    onTrackClick: () -> Unit
) {
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val colors = Theme.neumorphic

    val backgroundAlpha by animateFloatAsState(
        targetValue = if (playbackState.isPlaying) 0.75f else 0.3f,
        animationSpec = tween(1000),
        label = "bg_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(backgroundAlpha)
                .blur(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                colors.accent.copy(alpha = 0.3f),
                                colors.background
                            )
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "SPBChurch",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.accent
            )

            Text(
                text = "Radio",
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.weight(0.3f))

            LiveIndicator(
                isLive = playbackState.isPlaying,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ArtworkView(
                artworkUrl = null,
                title = "Древо жизни",
                size = 280.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = playbackState.isPlaying || playbackState.currentTitle.isNotBlank(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (playbackState.isPlaying) playbackState.currentTitle else "Нет данных",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .clickable(enabled = playbackState.currentTrack != null) {
                                onTrackClick()
                            }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (playbackState.currentTrack != null) {
                        TextButton(onClick = onTrackClick) {
                            Icon(
                                imageVector = Icons.Default.LibraryMusic,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Найти в библиотеке")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.3f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeumorphicIconButton(
                    onClick = { viewModel.previousTrack() },
                    size = 56.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Предыдущий",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                PlayButton(
                    isPlaying = playbackState.isPlaying,
                    onClick = { viewModel.toggleRadioPlayback() },
                    size = 120.dp
                )

                NeumorphicIconButton(
                    onClick = { viewModel.nextTrack() },
                    size = 56.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Следующий",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
