package com.rulersnap.measure.presentation.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rulersnap.measure.R
import com.rulersnap.measure.core.di.AppViewModelFactory
import com.rulersnap.measure.core.theme.AccentBlue
import com.rulersnap.measure.core.theme.AccentBlueDeep
import com.rulersnap.measure.core.theme.BackgroundPaper
import com.rulersnap.measure.core.theme.OutlineSoft
import com.rulersnap.measure.core.theme.PrimaryOlive
import com.rulersnap.measure.core.theme.SecondarySage
import com.rulersnap.measure.core.theme.SurfaceCard
import com.rulersnap.measure.core.theme.SurfaceSunken
import com.rulersnap.measure.core.theme.TextMuted
import com.rulersnap.measure.core.theme.TextPrimary
import com.rulersnap.measure.core.theme.TextSecondary
import com.rulersnap.measure.core.ui.PrimaryButton
import com.rulersnap.measure.core.ui.ScreenScaffold
import com.rulersnap.measure.core.ui.SecondaryButton
import com.rulersnap.measure.core.ui.UnitChipRow
import com.rulersnap.measure.core.ui.unitShortLabel
import com.rulersnap.measure.presentation.dialog.CalibrationHintDialog
import kotlin.math.roundToInt

@Composable
fun GameScreen(
    onBack: () -> Unit,
    onCalibration: () -> Unit,
    onMeasured: (Long, Float, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val gameViewModel: GameViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by gameViewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val labelPrefix = stringResource(R.string.label_prefix)

    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.Measured -> onMeasured(event.id, event.millimetres, event.label)
            }
        }
    }

    LaunchedEffect(state.showConfirm) {
        if (state.showConfirm && state.hapticsEnabled) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    DisposableEffect(Unit) {
        onDispose { gameViewModel.onScreenLeft() }
    }

    GameContent(
        state = state,
        onBack = onBack,
        onCalibration = onCalibration,
        onToggleLock = { gameViewModel.onToggleLock() },
        onMarkerDrag = { marker, delta -> gameViewModel.onMarkerDrag(marker, delta) },
        onSelectUnit = { unit -> gameViewModel.onSelectUnit(unit) },
        onReset = { gameViewModel.onReset() },
        onStore = { gameViewModel.onStore(labelPrefix) },
        onBedMeasured = { width, height -> gameViewModel.onBedMeasured(width, height) },
        onHintConfirmed = {
            gameViewModel.onHintConfirmed()
            onCalibration()
        },
        onHintDismissed = { gameViewModel.onHintDismissed() },
        modifier = modifier
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onBack: () -> Unit,
    onCalibration: () -> Unit,
    onToggleLock: () -> Unit,
    onMarkerDrag: (Int, Float) -> Unit,
    onSelectUnit: (com.rulersnap.measure.domain.model.MeasureUnit) -> Unit,
    onReset: () -> Unit,
    onStore: () -> Unit,
    onBedMeasured: (Float, Float) -> Unit,
    onHintConfirmed: () -> Unit,
    onHintDismissed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lockedState = stringResource(R.string.state_markers_locked)
    val freeState = stringResource(R.string.state_markers_free)
    val markerADescription = stringResource(R.string.cd_marker_a)
    val markerBDescription = stringResource(R.string.cd_marker_b)

    ScreenScaffold(
        title = stringResource(R.string.game_title),
        subtitle = stringResource(R.string.game_subtitle),
        onBack = onBack,
        modifier = modifier,
        topOverlay = BackgroundPaper.copy(alpha = 0.95f),
        bottomOverlay = SurfaceSunken.copy(alpha = 0.92f),
        headerTrailing = {
            Surface(
                onClick = onToggleLock,
                shape = RoundedCornerShape(14.dp),
                color = if (state.locked) SecondarySage else SurfaceSunken,
                border = BorderStroke(
                    width = if (state.locked) 2.dp else 1.dp,
                    color = if (state.locked) PrimaryOlive else OutlineSoft
                ),
                modifier = Modifier
                    .size(48.dp)
                    .semantics {
                        stateDescription = if (state.locked) lockedState else freeState
                    }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(R.drawable.icon_pause),
                        contentDescription = stringResource(R.string.cd_lock_markers),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(SurfaceSunken)
                .onSizeChanged { measured ->
                    onBedMeasured(measured.width.toFloat(), measured.height.toFloat())
                }
        ) {
            RulerCanvas(
                pxPerMm = state.pxPerMm,
                horizontal = state.horizontal,
                modifier = Modifier.fillMaxSize()
            )
            SpanOverlay(
                spanStart = state.spanStart,
                spanEnd = state.spanEnd,
                horizontal = state.horizontal,
                modifier = Modifier.fillMaxSize()
            )

            if (state.spanMm > 0f) {
                SpanValuePill(state = state)
            }

            MarkerHandle(
                position = state.markerA,
                horizontal = state.horizontal,
                locked = state.locked,
                description = markerADescription,
                state = if (state.locked) lockedState else freeState,
                onDrag = { delta -> onMarkerDrag(GameViewModel.MARKER_A, delta) }
            )
            MarkerHandle(
                position = state.markerB,
                horizontal = state.horizontal,
                locked = state.locked,
                description = markerBDescription,
                state = if (state.locked) lockedState else freeState,
                onDrag = { delta -> onMarkerDrag(GameViewModel.MARKER_B, delta) }
            )

            HintOverlay(
                visible = state.untouched,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        ReadoutStrip(state = state)

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceSunken)
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 16.dp)
        ) {
            UnitChipRow(
                selected = state.unit,
                onSelect = onSelectUnit,
                height = 44.dp
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                PrimaryButton(
                    text = stringResource(R.string.cta_save),
                    onClick = onStore,
                    icon = Icons.Default.Check,
                    enabled = state.canStore && !state.showConfirm,
                    height = 60.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                ConfirmBadge(visible = state.showConfirm, modifier = Modifier.align(Alignment.Center))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SecondaryButton(
                    text = stringResource(R.string.cta_reset),
                    onClick = onReset,
                    icon = Icons.Default.Refresh,
                    height = 48.dp,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.cta_calibrate),
                    onClick = onCalibration,
                    icon = Icons.Default.Build,
                    height = 48.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (state.hintVisible) {
        CalibrationHintDialog(
            onConfirm = onHintConfirmed,
            onDismiss = onHintDismissed
        )
    }
}

@Composable
private fun SpanValuePill(state: GameUiState) {
    val unitLabel = unitShortLabel(state.unit)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .then(
                if (state.horizontal) {
                    Modifier.fillMaxHeight()
                } else {
                    Modifier.fillMaxWidth()
                }
            )
            .offset {
                val middle = (state.spanStart + state.spanEnd) / 2f
                val shift = (middle - 18.dp.toPx()).roundToInt()
                if (state.horizontal) IntOffset(shift, 0) else IntOffset(0, shift)
            }
    ) {
        Surface(
            shape = RoundedCornerShape(50),
            color = SurfaceCard,
            border = BorderStroke(1.dp, OutlineSoft),
            shadowElevation = 4.dp
        ) {
            Text(
                text = stringResource(R.string.value_with_unit, state.spanValueText, unitLabel),
                color = AccentBlue,
                fontSize = 20.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
            )
        }
    }
}

@Composable
private fun HintOverlay(visible: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(180)),
        exit = fadeOut(tween(180)),
        modifier = modifier
    ) {
        HintCard()
    }
}

@Composable
private fun HintCard() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SurfaceCard.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, OutlineSoft),
        shadowElevation = 6.dp,
        modifier = Modifier.padding(horizontal = 48.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.game_hint),
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
private fun ReadoutStrip(state: GameUiState) {
    val unitLabel = unitShortLabel(state.unit)
    Surface(
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
        color = SurfaceSunken,
        border = BorderStroke(1.dp, OutlineSoft),
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .semantics { liveRegion = LiveRegionMode.Polite }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            ReadoutColumn(
                label = stringResource(R.string.readout_span),
                value = state.spanValueText,
                caption = unitLabel,
                valueSize = 30,
                valueColor = TextPrimary,
                modifier = Modifier.weight(1.2f)
            )
            ReadoutDivider()
            ReadoutColumn(
                label = stringResource(R.string.readout_inch),
                value = state.inchFractionText,
                caption = state.inchDecimalText,
                valueSize = 18,
                valueColor = TextSecondary,
                modifier = Modifier.weight(1f)
            )
            ReadoutDivider()
            ReadoutColumn(
                label = stringResource(R.string.readout_scale),
                value = state.scaleText,
                caption = if (state.calibrated) {
                    stringResource(R.string.menu_pill_calibrated)
                } else {
                    stringResource(R.string.menu_pill_default)
                },
                valueSize = 15,
                valueColor = TextMuted,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ReadoutDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(44.dp)
            .background(OutlineSoft)
    )
}

@Composable
private fun ReadoutColumn(
    label: String,
    value: String,
    caption: String,
    valueSize: Int,
    valueColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 10.dp)
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 10.sp,
            lineHeight = 13.sp,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = valueSize.sp,
            lineHeight = (valueSize + 6).sp,
            fontWeight = FontWeight.Black,
            maxLines = 1
        )
        Text(
            text = caption,
            color = TextMuted,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun ConfirmBadge(visible: Boolean, modifier: Modifier = Modifier) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.6f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "confirmScale"
    )
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(120)),
        exit = fadeOut(tween(160)),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(CircleShape)
                .background(SurfaceCard)
        ) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                tint = PrimaryOlive,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
