package com.rulersnap.measure.presentation.menu

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
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
import com.rulersnap.measure.core.theme.TextPrimary
import com.rulersnap.measure.core.theme.TextSecondary
import com.rulersnap.measure.core.ui.BentoTile
import com.rulersnap.measure.core.ui.ScreenBackground
import com.rulersnap.measure.core.ui.StatCard
import com.rulersnap.measure.core.ui.unitChipLabel

@Composable
fun MenuScreen(
    onStart: () -> Unit,
    onHistory: () -> Unit,
    onCalibration: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val menuViewModel: MenuViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by menuViewModel.uiState.collectAsStateWithLifecycle()

    MenuContent(
        state = state,
        onStart = onStart,
        onHistory = onHistory,
        onCalibration = onCalibration,
        onSettings = onSettings,
        onAbout = onAbout,
        modifier = modifier
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onStart: () -> Unit,
    onHistory: () -> Unit,
    onCalibration: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val calibratedState = stringResource(R.string.state_calibrated)
    val defaultScaleState = stringResource(R.string.state_default_scale)
    val showStats = state.hasLast && state.savedCount > 0

    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        appeared = true
    }

    ScreenBackground(
        modifier = modifier,
        topOverlay = BackgroundPaper.copy(alpha = 0.88f),
        bottomOverlay = BackgroundPaper.copy(alpha = 0.70f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.app_name),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(120.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = SecondarySage.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, OutlineSoft),
                    modifier = Modifier
                        .height(36.dp)
                        .semantics {
                            stateDescription = if (state.calibrated) {
                                calibratedState
                            } else {
                                defaultScaleState
                            }
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    ) {
                        Icon(
                            imageVector = if (state.calibrated) {
                                Icons.Default.Check
                            } else {
                                Icons.Default.Warning
                            },
                            contentDescription = null,
                            tint = if (state.calibrated) PrimaryOlive else AccentBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.calibrated) {
                                stringResource(R.string.menu_pill_calibrated)
                            } else {
                                stringResource(R.string.menu_pill_default)
                            },
                            color = TextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.menu_hero_top),
                color = TextPrimary,
                fontSize = 36.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(
                text = stringResource(R.string.menu_hero_bottom),
                color = PrimaryOlive,
                fontSize = 36.sp,
                lineHeight = 42.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.menu_tagline),
                color = TextSecondary,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.7f)
                ) {
                    StaggeredTile(appeared = appeared, index = 0, modifier = Modifier.weight(1.35f)) {
                        BentoTile(
                            title = stringResource(R.string.cta_start),
                            caption = stringResource(R.string.menu_cta_caption),
                            onClick = onStart,
                            icon = Icons.Default.PlayArrow,
                            accent = SurfaceCard,
                            decor = painterResource(R.drawable.sprite_1),
                            decorSize = 72.dp,
                            gradient = listOf(PrimaryOlive, PrimaryOliveDark),
                            titleColor = SurfaceCard,
                            captionColor = SurfaceCard.copy(alpha = 0.75f),
                            titleSize = 22,
                            modifier = Modifier
                                .fillMaxHeight()
                                .defaultMinSize(minHeight = 168.dp)
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        StaggeredTile(appeared = appeared, index = 1, modifier = Modifier.weight(1f)) {
                            BentoTile(
                                title = stringResource(R.string.menu_tile_history),
                                caption = if (state.savedCount > 0) {
                                    stringResource(R.string.menu_saved_count, state.savedCount)
                                } else {
                                    null
                                },
                                onClick = onHistory,
                                iconPainter = painterResource(R.drawable.ic_list),
                                accent = AccentBlue,
                                decor = painterResource(R.drawable.sprite_2),
                                decorSize = 52.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .defaultMinSize(minHeight = 78.dp)
                            )
                        }
                        StaggeredTile(appeared = appeared, index = 2, modifier = Modifier.weight(1f)) {
                            BentoTile(
                                title = stringResource(R.string.cta_calibrate),
                                caption = stringResource(R.string.menu_tile_calibrate_caption),
                                onClick = onCalibration,
                                icon = Icons.Default.Build,
                                accent = PrimaryOlive,
                                decor = painterResource(R.drawable.sprite_3),
                                decorSize = 52.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .defaultMinSize(minHeight = 78.dp)
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.85f)
                ) {
                    StaggeredTile(appeared = appeared, index = 3, modifier = Modifier.weight(1f)) {
                        BentoTile(
                            title = stringResource(R.string.menu_tile_settings),
                            caption = unitChipLabel(state.unit),
                            onClick = onSettings,
                            icon = Icons.Default.Settings,
                            accent = TextSecondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .defaultMinSize(minHeight = 78.dp)
                        )
                    }
                    StaggeredTile(appeared = appeared, index = 4, modifier = Modifier.weight(1f)) {
                        BentoTile(
                            title = stringResource(R.string.menu_tile_about),
                            caption = stringResource(R.string.menu_tile_about_caption),
                            onClick = onAbout,
                            icon = Icons.Default.Info,
                            accent = TextSecondary,
                            decor = painterResource(R.drawable.sprite_5),
                            decorSize = 44.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .defaultMinSize(minHeight = 78.dp)
                        )
                    }
                }
            }

            if (showStats) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.stat_last),
                        value = state.lastValueText,
                        accent = AccentBlue,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.stat_saved),
                        value = state.savedCount.toString(),
                        accent = PrimaryOlive,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun StaggeredTile(
    appeared: Boolean,
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(
            durationMillis = 190,
            delayMillis = index * 60,
            easing = FastOutSlowInEasing
        ),
        label = "tileAlpha"
    )
    val lift by animateDpAsState(
        targetValue = if (appeared) 0.dp else 12.dp,
        animationSpec = tween(
            durationMillis = 190,
            delayMillis = index * 60,
            easing = FastOutSlowInEasing
        ),
        label = "tileLift"
    )
    Box(
        modifier = modifier
            .alpha(alpha)
            .offset(y = lift)
    ) {
        content()
    }
}
