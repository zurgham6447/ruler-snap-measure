package com.rulersnap.measure.presentation.settings

import com.rulersnap.measure.domain.model.MeasureUnit
import com.rulersnap.measure.domain.model.RulerOrientation

enum class SettingsSection {
    NONE,
    UNITS,
    ORIENTATION,
    FEEDBACK
}

data class SettingsUiState(
    val unit: MeasureUnit = MeasureUnit.MM,
    val orientation: RulerOrientation = RulerOrientation.VERTICAL,
    val hapticsEnabled: Boolean = true,
    val scaleText: String = "0.00",
    val userCalibrated: Boolean = false,
    val confirmedSection: SettingsSection = SettingsSection.NONE
)
