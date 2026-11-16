package com.rulersnap.measure.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import com.rulersnap.measure.core.theme.BackgroundDeep
import com.rulersnap.measure.core.theme.BackgroundPaper
import com.rulersnap.measure.core.theme.OutlineSoft
import com.rulersnap.measure.core.theme.PrimaryOlive
import com.rulersnap.measure.core.theme.SurfaceCard
import com.rulersnap.measure.core.theme.SurfaceSunken
import com.rulersnap.measure.core.theme.TextMuted
import com.rulersnap.measure.core.theme.TextPrimary
import com.rulersnap.measure.core.theme.TextSecondary
import com.rulersnap.measure.core.theme.TickBlack
import com.rulersnap.measure.core.ui.ScreenScaffold
import com.rulersnap.measure.core.ui.SecondaryButton
import com.rulersnap.measure.core.ui.SectionHeader
import com.rulersnap.measure.core.ui.UnitChipRow
import com.rulersnap.measure.domain.model.MeasureUnit
import com.rulersnap.measure.domain.model.RulerOrientation

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onCalibration: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by settingsViewModel.uiState.collectAsStateWithLifecycle()

    SettingsContent(
        state = state,
        onBack = onBack,
        onCalibration = onCalibration,
        onSelectUnit = { unit -> settingsViewModel.onSelectUnit(unit) },
        onSelectOrientation = { orientation -> settingsViewModel.onSelectOrientation(orientation) },
        onToggleHaptics = { enabled -> settingsViewModel.onToggleHaptics(enabled) },
        modifier = modifier
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onBack: () -> Unit,
    onCalibration: () -> Unit,
    onSelectUnit: (MeasureUnit) -> Unit,
    onSelectOrientation: (RulerOrientation) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticsOn = stringResource(R.string.state_on)
    val hapticsOff = stringResource(R.string.state_off)

    ScreenScaffold(
        title = stringResource(R.string.settings_title),
        subtitle = stringResource(R.string.settings_subtitle),
        onBack = onBack,
        modifier = modifier,
        topOverlay = BackgroundPaper.copy(alpha = 0.90f),
        bottomOverlay = BackgroundDeep.copy(alpha = 0.78f),
        headerTrailing = {
            Image(
                painter = painterResource(R.drawable.sprite_4),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                alpha = 0.85f,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(48.dp)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            SectionRow(
                title = stringResource(R.string.settings_section_units),
                confirmed = state.confirmedSection == SettingsSection.UNITS
            )
            SettingsCard {
                UnitChipRow(
                    selected = state.unit,
                    onSelect = onSelectUnit,
                    height = 48.dp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionRow(
                title = stringResource(R.string.settings_section_orientation),
                confirmed = state.confirmedSection == SettingsSection.ORIENTATION
            )
            SettingsCard {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OrientationOption(
                        text = stringResource(R.string.orientation_vertical),
                        vertical = true,
                        active = state.orientation == RulerOrientation.VERTICAL,
                        onClick = { onSelectOrientation(RulerOrientation.VERTICAL) },
                        modifier = Modifier.weight(1f)
                    )
                    OrientationOption(
                        text = stringResource(R.string.orientation_horizontal),
                        vertical = false,
                        active = state.orientation == RulerOrientation.HORIZONTAL,
                        onClick = { onSelectOrientation(RulerOrientation.HORIZONTAL) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionRow(
                title = stringResource(R.string.settings_section_feedback),
                confirmed = state.confirmedSection == SettingsSection.FEEDBACK
            )
            SettingsCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = stringResource(R.string.settings_haptics),
                        color = TextPrimary,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = state.hapticsEnabled,
                        onCheckedChange = onToggleHaptics,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SurfaceCard,
                            checkedTrackColor = PrimaryOlive,
                            uncheckedThumbColor = SurfaceCard,
                            uncheckedTrackColor = SurfaceSunken,
                            uncheckedBorderColor = OutlineSoft
                        ),
                        modifier = Modifier.semantics {
                            stateDescription = if (state.hapticsEnabled) hapticsOn else hapticsOff
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionRow(
                title = stringResource(R.string.settings_section_calibration),
                confirmed = false
            )
            SettingsCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.settings_current_scale),
                            color = TextSecondary,
                            fontSize = 14.sp,
                            lineHeight = 19.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = state.scaleText,
                            color = AccentBlue,
                            fontSize = 18.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = if (state.userCalibrated) {
                            stringResource(R.string.menu_pill_calibrated)
                        } else {
                            stringResource(R.string.menu_pill_default)
                        },
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SecondaryButton(
                        text = stringResource(R.string.cta_open_calibration),
                        onClick = onCalibration,
                        height = 48.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SectionRow(title: String, confirmed: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        SectionHeader(text = title, modifier = Modifier.weight(1f))
        AnimatedVisibility(
            visible = confirmed,
            enter = fadeIn(tween(120)),
            exit = fadeOut(tween(180))
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = PrimaryOlive,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(18.dp)
            )
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, OutlineSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(14.dp)) {
            content()
        }
    }
}

@Composable
private fun OrientationOption(
    text: String,
    vertical: Boolean,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selected = stringResource(R.string.state_selected)
    val notSelected = stringResource(R.string.state_not_selected)
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (active) AccentBlue.copy(alpha = 0.14f) else SurfaceCard,
        border = BorderStroke(
            width = if (active) 2.dp else 1.dp,
            color = if (active) AccentBlue else OutlineSoft
        ),
        modifier = modifier
            .height(64.dp)
            .semantics { stateDescription = if (active) selected else notSelected }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Canvas(
                modifier = Modifier
                    .width(22.dp)
                    .height(32.dp)
            ) {
                val tickColor = if (active) AccentBlue else TickBlack.copy(alpha = 0.45f)
                val count = 6
                if (vertical) {
                    val step = size.height / count
                    for (index in 0..count) {
                        val long = index % 2 == 0
                        drawLine(
                            color = tickColor,
                            start = Offset(0f, index * step),
                            end = Offset(if (long) size.width else size.width * 0.55f, index * step),
                            strokeWidth = 2f
                        )
                    }
                } else {
                    val step = size.width / count
                    for (index in 0..count) {
                        val long = index % 2 == 0
                        drawLine(
                            color = tickColor,
                            start = Offset(index * step, size.height),
                            end = Offset(
                                index * step,
                                if (long) 0f else size.height * 0.45f
                            ),
                            strokeWidth = 2f
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                color = if (active) AccentBlue else TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 1.2.sp,
                fontWeight = if (active) FontWeight.Black else FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}
