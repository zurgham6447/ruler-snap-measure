package com.rulersnap.measure.presentation.calibration

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
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
import com.rulersnap.measure.core.theme.BackgroundDeep
import com.rulersnap.measure.core.theme.BackgroundPaper
import com.rulersnap.measure.core.theme.MarkerRed
import com.rulersnap.measure.core.theme.OutlineSoft
import com.rulersnap.measure.core.theme.PrimaryOlive
import com.rulersnap.measure.core.theme.PrimaryOliveDark
import com.rulersnap.measure.core.theme.SurfaceCard
import com.rulersnap.measure.core.theme.SurfaceSunken
import com.rulersnap.measure.core.theme.TextMuted
import com.rulersnap.measure.core.theme.TextPrimary
import com.rulersnap.measure.core.theme.TextSecondary
import com.rulersnap.measure.core.ui.PrimaryButton
import com.rulersnap.measure.core.ui.ScreenScaffold
import com.rulersnap.measure.core.ui.SecondaryButton
import kotlin.math.roundToInt

@Composable
fun CalibrationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val calibrationViewModel: CalibrationViewModel =
        viewModel(factory = AppViewModelFactory.factory)
    val state by calibrationViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(calibrationViewModel) {
        calibrationViewModel.events.collect { event ->
            when (event) {
                is CalibrationEvent.Stored -> onBack()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { calibrationViewModel.pause() }
    }

    CalibrationContent(
        state = state,
        onBack = onBack,
        onStep = { direction -> calibrationViewModel.onStep(direction) },
        onHoldStart = { direction -> calibrationViewModel.onStepHoldStart(direction) },
        onHoldStop = { calibrationViewModel.onStepHoldStop() },
        onSlider = { value -> calibrationViewModel.onSliderChanged(value) },
        onCardDrag = { delta -> calibrationViewModel.onCardWidthDrag(delta) },
        onStore = { calibrationViewModel.onSaveCalibration() },
        onResetDefault = { calibrationViewModel.onResetToDefault() },
        modifier = modifier
    )
}

@Composable
private fun CalibrationContent(
    state: CalibrationUiState,
    onBack: () -> Unit,
    onStep: (Int) -> Unit,
    onHoldStart: (Int) -> Unit,
    onHoldStop: () -> Unit,
    onSlider: (Float) -> Unit,
    onCardDrag: (Float) -> Unit,
    onStore: () -> Unit,
    onResetDefault: () -> Unit,
    modifier: Modifier = Modifier
) {
    val outlineDescription = stringResource(R.string.cd_card_outline)
    val widthState = stringResource(R.string.state_card_width, state.pxPerMmText)
    val decreaseDescription = stringResource(R.string.cd_decrease)
    val increaseDescription = stringResource(R.string.cd_increase)
    val badgeScale by animateFloatAsState(
        targetValue = if (state.saved) 1f else 0.5f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "calibrationBadgeScale"
    )

    ScreenScaffold(
        title = stringResource(R.string.calibration_title),
        subtitle = stringResource(R.string.calibration_subtitle),
        onBack = onBack,
        modifier = modifier,
        topOverlay = BackgroundPaper.copy(alpha = 0.90f),
        bottomOverlay = BackgroundDeep.copy(alpha = 0.78f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, OutlineSoft),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.sprite_3),
                        contentDescription = stringResource(R.string.cd_card_reference),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(104.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.calibration_explainer),
                        color = TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            CardOutline(
                widthPx = state.cardWidthPx,
                heightPx = state.cardHeightPx,
                description = outlineDescription,
                widthState = widthState,
                onDrag = onCardDrag,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                StepperButton(
                    description = decreaseDescription,
                    onStep = { onStep(-1) },
                    onHoldStart = { onHoldStart(-1) },
                    onHoldStop = onHoldStop
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_minus),
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.calibration_scale_label),
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        letterSpacing = 1.4.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = state.pxPerMmText,
                        color = TextPrimary,
                        fontSize = 24.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1
                    )
                }
                StepperButton(
                    description = increaseDescription,
                    onStep = { onStep(1) },
                    onHoldStart = { onHoldStart(1) },
                    onHoldStop = onHoldStop
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Slider(
                value = state.pxPerMm,
                onValueChange = onSlider,
                valueRange = state.minPxPerMm..state.maxPxPerMm,
                colors = SliderDefaults.colors(
                    thumbColor = AccentBlue,
                    activeTrackColor = AccentBlue,
                    inactiveTrackColor = SurfaceSunken
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                PrimaryButton(
                    text = stringResource(R.string.cta_save_calibration),
                    onClick = onStore,
                    icon = Icons.Default.Check,
                    height = 60.dp,
                    colors = listOf(PrimaryOlive, PrimaryOliveDark),
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.saved) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SurfaceCard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = PrimaryOlive,
                            modifier = Modifier.size((26 * badgeScale).dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = stringResource(R.string.cta_reset_default),
                onClick = onResetDefault,
                height = 50.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CardOutline(
    widthPx: Float,
    heightPx: Float,
    description: String,
    widthState: String,
    onDrag: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var areaWidth by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceCard.copy(alpha = 0.55f))
            .border(1.dp, OutlineSoft, RoundedCornerShape(18.dp))
            .onSizeChanged { size -> areaWidth = size.width.toFloat() }
            .semantics {
                contentDescription = description
                stateDescription = widthState
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (widthPx <= 0f || heightPx <= 0f) return@Canvas
            val top = ((size.height - heightPx) / 2f).coerceAtLeast(12f)
            val left = 16f * density
            val radius = CornerRadius(12f * density, 12f * density)
            drawRoundRect(
                color = AccentBlue.copy(alpha = 0.06f),
                topLeft = Offset(left, top),
                size = Size(widthPx, heightPx),
                cornerRadius = radius
            )
            drawRoundRect(
                color = AccentBlue,
                topLeft = Offset(left, top),
                size = Size(widthPx, heightPx),
                cornerRadius = radius,
                style = Stroke(
                    width = 2f * density,
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(12f * density, 8f * density)
                    )
                )
            )
            val tick = 14f * density
            val corners = listOf(
                Offset(left, top),
                Offset(left + widthPx, top),
                Offset(left, top + heightPx),
                Offset(left + widthPx, top + heightPx)
            )
            corners.forEachIndexed { index, corner ->
                val horizontalSign = if (index % 2 == 0) 1f else -1f
                val verticalSign = if (index < 2) 1f else -1f
                drawLine(
                    color = MarkerRed,
                    start = corner,
                    end = Offset(corner.x + tick * horizontalSign, corner.y),
                    strokeWidth = 3f * density
                )
                drawLine(
                    color = MarkerRed,
                    start = corner,
                    end = Offset(corner.x, corner.y + tick * verticalSign),
                    strokeWidth = 3f * density
                )
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .width(44.dp)
                .offset {
                    val limit = (areaWidth - 44.dp.toPx()).coerceAtLeast(0f)
                    val target = 16f * density + widthPx - 22.dp.toPx()
                    IntOffset(target.coerceIn(0f, limit).roundToInt(), 0)
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x)
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(AccentBlue)
            )
        }
    }
}

@Composable
private fun StepperButton(
    description: String,
    onStep: () -> Unit,
    onHoldStart: () -> Unit,
    onHoldStop: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.5.dp, OutlineSoft, RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onHoldStart()
                        tryAwaitRelease()
                        onHoldStop()
                    },
                    onTap = { onStep() }
                )
            }
            .semantics {
                contentDescription = description
                role = Role.Button
            }
    ) {
        content()
    }
}
