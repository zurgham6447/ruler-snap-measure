package com.rulersnap.measure.presentation.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rulersnap.measure.R
import com.rulersnap.measure.core.di.AppViewModelFactory
import com.rulersnap.measure.core.theme.AccentBlue
import com.rulersnap.measure.core.theme.BackgroundDeep
import com.rulersnap.measure.core.theme.BackgroundPaper
import com.rulersnap.measure.core.theme.OutlineSoft
import com.rulersnap.measure.core.theme.PrimaryOlive
import com.rulersnap.measure.core.theme.PrimaryOliveDark
import com.rulersnap.measure.core.theme.SecondarySage
import com.rulersnap.measure.core.theme.SurfaceCard
import com.rulersnap.measure.core.theme.TextMuted
import com.rulersnap.measure.core.theme.TextPrimary
import com.rulersnap.measure.core.ui.PrimaryButton
import com.rulersnap.measure.core.ui.ScreenScaffold
import com.rulersnap.measure.core.ui.SectionHeader
import com.rulersnap.measure.core.ui.StatCard
import com.rulersnap.measure.core.ui.unitShortLabel
import com.rulersnap.measure.presentation.dialog.ClearHistoryDialog

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val historyViewModel: HistoryViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by historyViewModel.uiState.collectAsStateWithLifecycle()

    HistoryContent(
        state = state,
        onBack = onBack,
        onStart = onStart,
        onRemove = { id -> historyViewModel.onRemove(id) },
        onClearRequested = { historyViewModel.onClearRequested() },
        onClearConfirmed = { historyViewModel.onClearConfirmed() },
        onClearDismissed = { historyViewModel.onClearDismissed() },
        modifier = modifier
    )
}

@Composable
private fun HistoryContent(
    state: HistoryUiState,
    onBack: () -> Unit,
    onStart: () -> Unit,
    onRemove: (Long) -> Unit,
    onClearRequested: () -> Unit,
    onClearConfirmed: () -> Unit,
    onClearDismissed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val unitLabel = unitShortLabel(state.unit)

    ScreenScaffold(
        title = stringResource(R.string.history_title),
        subtitle = stringResource(R.string.history_subtitle),
        onBack = onBack,
        modifier = modifier,
        topOverlay = BackgroundPaper.copy(alpha = 0.90f),
        bottomOverlay = BackgroundDeep.copy(alpha = 0.78f),
        headerTrailing = {
            IconButton(onClick = onClearRequested, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.cd_clear_history),
                    tint = if (state.entries.isEmpty()) TextMuted else TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            if (state.hasAverage) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.stat_saved),
                        value = state.savedCount.toString(),
                        accent = PrimaryOlive,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.stat_average),
                        value = state.averageText,
                        accent = AccentBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (state.entries.isEmpty()) {
                EmptyHistory(onStart = onStart)
            } else {
                SectionHeader(
                    text = stringResource(R.string.history_section_recent),
                    decor = painterResource(R.drawable.sprite_5),
                    decorSize = 40.dp,
                    modifier = Modifier.padding(top = 14.dp, bottom = 10.dp)
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(items = state.entries, key = { entry -> entry.id }) { entry ->
                        HistoryRow(
                            entry = entry,
                            unitLabel = unitLabel,
                            onRemove = onRemove,
                            modifier = Modifier.animateItem()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (state.clearRequested) {
        ClearHistoryDialog(
            count = state.savedCount,
            onConfirm = onClearConfirmed,
            onDismiss = onClearDismissed
        )
    }
}

@Composable
private fun HistoryRow(
    entry: HistoryEntry,
    unitLabel: String,
    onRemove: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, OutlineSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        SecondarySage.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = PrimaryOlive,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = entry.label,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = entry.dateText,
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 1
                )
            }
            Text(
                text = stringResource(R.string.value_with_unit, entry.valueText, unitLabel),
                color = AccentBlue,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
            IconButton(
                onClick = { onRemove(entry.id) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.cd_delete_entry),
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyHistory(onStart: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_2),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alpha = 0.7f,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.history_empty_title),
            color = TextPrimary,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.history_empty_caption),
            color = TextMuted,
            fontSize = 14.sp,
            lineHeight = 19.sp
        )
        Spacer(modifier = Modifier.height(22.dp))
        PrimaryButton(
            text = stringResource(R.string.cta_start),
            onClick = onStart,
            height = 60.dp,
            colors = listOf(PrimaryOlive, PrimaryOliveDark),
            modifier = Modifier.width(260.dp)
        )
    }
}
