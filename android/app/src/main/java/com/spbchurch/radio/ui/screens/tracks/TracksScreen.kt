package com.spbchurch.radio.ui.screens.tracks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spbchurch.radio.R
import com.spbchurch.radio.data.model.SortOrder
import com.spbchurch.radio.ui.components.*
import com.spbchurch.radio.ui.theme.Theme
import com.spbchurch.radio.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TracksScreen(
    viewModel: MainViewModel,
    onTrackClick: () -> Unit
) {
    val tracks by viewModel.filteredTracks.collectAsStateWithLifecycle()
    val allTracks by viewModel.tracks.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val colors = Theme.neumorphic

    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        stringResource(R.string.search),
                        color = colors.textSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = colors.textSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Очистить",
                                tint = colors.textSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accent,
                    unfocusedBorderColor = colors.textSecondary.copy(alpha = 0.3f),
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface
                ),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box {
                IconButton(
                    onClick = { showSortMenu = true }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "Сортировка",
                        tint = colors.textSecondary
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_default)) },
                        onClick = {
                            viewModel.setSortOrder(SortOrder.DEFAULT)
                            showSortMenu = false
                        },
                        leadingIcon = {
                            if (sortOrder == SortOrder.DEFAULT) {
                                Icon(Icons.Default.Check, null, tint = colors.accent)
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_az)) },
                        onClick = {
                            viewModel.setSortOrder(SortOrder.A_TO_Z)
                            showSortMenu = false
                        },
                        leadingIcon = {
                            if (sortOrder == SortOrder.A_TO_Z) {
                                Icon(Icons.Default.Check, null, tint = colors.accent)
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_za)) },
                        onClick = {
                            viewModel.setSortOrder(SortOrder.Z_TO_A)
                            showSortMenu = false
                        },
                        leadingIcon = {
                            if (sortOrder == SortOrder.Z_TO_A) {
                                Icon(Icons.Default.Check, null, tint = colors.accent)
                            }
                        }
                    )
                }
            }
        }

        if (allTracks.isNotEmpty()) {
            Text(
                text = "${tracks.size} треков",
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        when {
            isLoading && tracks.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colors.accent)
                }
            }
            error != null && tracks.isEmpty() -> {
                EmptyStateView(
                    icon = {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = colors.error
                        )
                    },
                    title = stringResource(R.string.error),
                    subtitle = error ?: "",
                    modifier = Modifier.fillMaxSize()
                )
            }
            tracks.isEmpty() -> {
                EmptyStateView(
                    icon = {
                        Icon(
                            Icons.Default.MusicOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = colors.textSecondary
                        )
                    },
                    title = stringResource(R.string.no_tracks),
                    subtitle = if (searchQuery.isNotEmpty()) "Попробуйте изменить запрос" else "",
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(
                        items = tracks,
                        key = { it.id }
                    ) { track ->
                        val isCurrentTrack = playbackState.currentTrack?.id == track.id
                        val isPlaying = isCurrentTrack && playbackState.isPlaying && !playbackState.isRadioMode

                        TrackRow(
                            track = track,
                            isPlaying = isPlaying,
                            isCurrentTrack = isCurrentTrack,
                            isFavorite = favorites.any { it.url == track.url },
                            downloadState = viewModel.getDownloadState(track),
                            downloadProgress = viewModel.getDownloadProgress(track),
                            onPlay = {
                                viewModel.playTrack(track)
                                onTrackClick()
                            },
                            onFavorite = { viewModel.toggleFavorite(track) },
                            onDownload = { viewModel.downloadTrack(track) },
                            onCancelDownload = { viewModel.cancelDownload(track) }
                        )
                    }
                }
            }
        }
    }
}
