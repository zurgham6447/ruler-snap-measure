package com.rulersnap.measure.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rulersnap.measure.R
import com.rulersnap.measure.core.theme.AccentBlue
import com.rulersnap.measure.core.theme.OutlineSoft
import com.rulersnap.measure.core.theme.SurfaceCard
import com.rulersnap.measure.core.theme.TextSecondary
import com.rulersnap.measure.domain.model.MeasureUnit

@Composable
fun unitChipLabel(unit: MeasureUnit): String = stringResource(
    when (unit) {
        MeasureUnit.MM -> R.string.unit_mm
        MeasureUnit.CM -> R.string.unit_cm
        MeasureUnit.INCH -> R.string.unit_inch
    }
)

@Composable
fun unitShortLabel(unit: MeasureUnit): String = stringResource(
    when (unit) {
        MeasureUnit.MM -> R.string.unit_mm_short
        MeasureUnit.CM -> R.string.unit_cm_short
        MeasureUnit.INCH -> R.string.unit_inch_short
    }
)

@Composable
fun UnitChipRow(
    selected: MeasureUnit,
    onSelect: (MeasureUnit) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp
) {
    val selectedDescription = stringResource(R.string.state_selected)
    val unselectedDescription = stringResource(R.string.state_not_selected)
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        MeasureUnit.entries.forEach { unit ->
            val active = unit == selected
            Surface(
                onClick = { onSelect(unit) },
                shape = RoundedCornerShape(50),
                color = if (active) AccentBlue.copy(alpha = 0.14f) else SurfaceCard,
                border = BorderStroke(
                    width = if (active) 2.dp else 1.dp,
                    color = if (active) AccentBlue else OutlineSoft
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(height)
                    .semantics {
                        stateDescription = if (active) selectedDescription else unselectedDescription
                    }
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = unitChipLabel(unit),
                        color = if (active) AccentBlue else TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        letterSpacing = 1.4.sp,
                        fontWeight = if (active) FontWeight.Black else FontWeight.Medium
                    )
                }
            }
        }
    }
}
