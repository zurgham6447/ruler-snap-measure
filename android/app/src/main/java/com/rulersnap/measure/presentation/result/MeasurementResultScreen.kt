package com.rulersnap.measure.presentation.result

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rulersnap.measure.R
import com.rulersnap.measure.core.di.AppViewModelFactory
import com.rulersnap.measure.core.theme.AccentBlue
import com.rulersnap.measure.core.theme.BackgroundPaper
import com.rulersnap.measure.core.theme.OutlineSoft
import com.rulersnap.measure.core.theme.PrimaryOlive
import com.rulersnap.measure.core.theme.PrimaryOliveDark
import com.rulersnap.measure.core.theme.SecondarySage
import com.rulersnap.measure.core.theme.SurfaceCard
import com.rulersnap.measure.core.theme.TextMuted
import com.rulersnap.measure.core.theme.TextPrimary
import com.rulersnap.measure.core.theme.TextSecondary
import com.rulersnap.measure.core.ui.PrimaryButton
import com.rulersnap.measure.core.ui.ScreenBackground
import com.rulersnap.measure.core.ui.SecondaryButton
import com.rulersnap.measure.core.ui.StatCard

@Composable
fun MeasurementResultScreen(
    measurementId: Long,
    millimetres: Float,
    initialLabel: String,
    onMeasureAgain: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resultViewModel: MeasurementResultViewModel =
        viewModel(factory = AppViewModelFactory.factory)
    val state by resultViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(measurementId) {
        resultViewModel.bind(measurementId, millimetres, initialLabel)
    }

    MeasurementResultContent(
        state = state,
        onLabelChanged = { value -> resultViewModel.onLabelChanged(value) },
        onMeasureAgain = onMeasureAgain,
        onMenu = onMenu,
        modifier = modifier
    )
}

@Composable
private fun MeasurementResultContent(
    state: MeasurementResultUiState,
    onLabelChanged: (String) -> Unit,
    onMeasureAgain: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(state.millimetres) {
        appeared = true
    }

    val cardAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "resultCardAlpha"
    )
    val cardLift by animateDpAsState(
        targetValue = if (appeared) 0.dp else 20.dp,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "resultCardLift"
    )
    val badgeScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.5f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "resultBadgeScale"
    )
    val valueScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.86f,
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
        label = "resultValueScale"
    )

    val millimetreUnit = stringResource(R.string.unit_mm_short)
    val showStats = state.savedCount > 0 && state.hasBest

    ScreenBackground(
        modifier = modifier,
        topOverlay = BackgroundPaper.copy(alpha = 0.80f),
        bottomOverlay = SecondarySage.copy(alpha = 0.55f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(R.string.result_eyebrow),
                color = PrimaryOlive,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, OutlineSoft),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(cardAlpha)
                    .offset(y = cardLift)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .graphicsLayer {
                                    scaleX = badgeScale
                                    scaleY = badgeScale
                                }
                                .background(PrimaryOlive.copy(alpha = 0.12f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                tint = PrimaryOlive,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = stringResource(
                                R.string.value_with_unit,
                                state.millimetresText,
                                millimetreUnit
                            ),
                            color = TextPrimary,
                            fontSize = 42.sp,
                            lineHeight = 48.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = valueScale
                                    scaleY = valueScale
                                    transformOrigin = TransformOrigin(0f, 0.5f)
                                }
                                .semantics { liveRegion = LiveRegionMode.Polite }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(
                                R.string.result_conversions,
                                state.centimetresText,
                                state.inchesText
                            ),
                            color = TextSecondary,
                            fontSize = 15.sp,
                            lineHeight = 21.sp,
                            maxLines = 1
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.sprite_4),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        alpha = 0.9f,
                        modifier = Modifier.size(92.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(R.string.result_name_label),
                color = TextMuted,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                letterSpacing = 1.4.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.label,
                onValueChange = onLabelChanged,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                textStyle = TextStyle(
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = OutlineSoft,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    cursorColor = AccentBlue,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                QuickLabelChip(
                    text = stringResource(R.string.quick_screw),
                    onPick = onLabelChanged,
                    modifier = Modifier.weight(1f)
                )
                QuickLabelChip(
                    text = stringResource(R.string.quick_button),
                    onPick = onLabelChanged,
                    modifier = Modifier.weight(1f)
                )
                QuickLabelChip(
                    text = stringResource(R.string.quick_hole),
                    onPick = onLabelChanged,
                    modifier = Modifier.weight(1f)
                )
            }

            if (showStats) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.stat_saved),
                        value = state.savedCount.toString(),
                        accent = PrimaryOlive,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.stat_best),
                        value = state.bestSpanText,
                        accent = AccentBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = stringResource(R.string.cta_again),
                onClick = onMeasureAgain,
                icon = Icons.Default.Done,
                height = 60.dp,
                colors = listOf(PrimaryOlive, PrimaryOliveDark),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = stringResource(R.string.cta_menu),
                onClick = onMenu,
                iconPainter = painterResource(R.drawable.icon_home),
                iconDescription = null,
                height = 50.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuickLabelChip(
    text: String,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onPick(text) },
        shape = RoundedCornerShape(50),
        color = SurfaceCard,
        border = BorderStroke(1.dp, OutlineSoft),
        modifier = modifier.height(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = text,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
