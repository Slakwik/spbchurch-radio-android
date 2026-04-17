package com.spbchurch.radio.ui.screens.radio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spbchurch.radio.R
import com.spbchurch.radio.ui.components.LargeEqualizerView
import com.spbchurch.radio.ui.components.MiniEqualizerView
import com.spbchurch.radio.ui.theme.AppColors
import com.spbchurch.radio.ui.theme.neumorphicRaised
import com.spbchurch.radio.util.RadioTitle
import com.spbchurch.radio.viewmodel.MainViewModel

@Composable
fun RadioScreen(
    viewModel: MainViewModel,
    onTrackClick: () -> Unit,
    onFindInLibrary: (String) -> Unit
) {
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme
    val isPlaying = playbackState.isRadioMode && playbackState.isPlaying
    val title = playbackState.currentTitle
    val canSearch = isPlaying && RadioTitle.isSearchable(title)

    val haloPulse by rememberInfiniteTransition(label = "halo").animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Background tree (always present, brightness varies)
        val treeAlpha by animateFloatAsState(
            targetValue = if (isPlaying) 0.55f else 0.18f,
            animationSpec = tween(1200),
            label = "tree_alpha"
        )
        Image(
            painter = painterResource(R.drawable.tree_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(treeAlpha),
            contentScale = ContentScale.Fit
        )

        // Halo behind play button when playing
        if (isPlaying) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(haloPulse)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                AppColors.Accent.copy(alpha = 0.30f),
                                AppColors.Accent.copy(alpha = 0.06f),
                                colors.background.copy(alpha = 0f)
                            ),
                            radius = 600f
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Радио",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                    Text(
                        text = "SPBChurch",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.primary
                    )
                }
                if (isPlaying) {
                    LargeEqualizerView(
                        isPlaying = true,
                        modifier = Modifier.height(32.dp)
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Play / Stop button (neumorphic)
            PlayStopButton(
                isPlaying = isPlaying,
                onClick = { viewModel.toggleRadioPlayback() }
            )

            Spacer(Modifier.weight(1f))

            // Track info
            Text(
                text = title.ifBlank { "Нет данных" },
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                maxLines = 2,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "SPBChurch Radio",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = colors.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(Modifier.height(14.dp))

            // Find-in-library button (always reserves space)
            AnimatedVisibility(
                visible = canSearch,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FindInLibraryButton(
                    onClick = { onFindInLibrary(RadioTitle.cleaned(title)) }
                )
            }

            Spacer(Modifier.height(20.dp))

            // Bottom status row
            LiveStatusRow(isPlaying = isPlaying)

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PlayStopButton(
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val size = 144.dp

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .neumorphicRaised(cornerRadius = size / 2, elevation = 9.dp, blurRadius = 16.dp)
            .background(colors.background, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Inner gold ring outline
        Box(
            modifier = Modifier
                .size(size - 14.dp)
                .clip(CircleShape)
                .background(colors.background, CircleShape)
        )
        Icon(
            imageVector = if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
            contentDescription = if (isPlaying) "Стоп" else "Воспроизвести",
            tint = if (isPlaying) colors.primary else colors.onBackground,
            modifier = Modifier.size(56.dp)
        )
    }
}

@Composable
private fun FindInLibraryButton(onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .neumorphicRaised(cornerRadius = 20.dp, elevation = 4.dp, blurRadius = 6.dp)
            .background(colors.background, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.size(6.dp))
        Text(
            text = "Найти в библиотеке",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.primary
        )
    }
}

@Composable
private fun LiveStatusRow(isPlaying: Boolean) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .neumorphicRaised(cornerRadius = 14.dp, elevation = 5.dp, blurRadius = 8.dp)
            .background(colors.background, RoundedCornerShape(14.dp))
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val dotColor = if (isPlaying) AppColors.SuccessLight else colors.onSurfaceVariant.copy(alpha = 0.3f)
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(Modifier.size(10.dp))
        Text(
            text = if (isPlaying) "В ЭФИРЕ" else "ОФЛАЙН",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPlaying) colors.primary else colors.onSurfaceVariant,
            letterSpacing = 2.sp
        )
        if (isPlaying) {
            Spacer(Modifier.weight(1f))
            MiniEqualizerView(isPlaying = true)
        }
    }
}
