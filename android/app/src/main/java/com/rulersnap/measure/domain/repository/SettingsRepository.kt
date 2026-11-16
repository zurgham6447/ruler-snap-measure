package com.rulersnap.measure.domain.repository

import com.rulersnap.measure.domain.model.AppSettings
import com.rulersnap.measure.domain.model.Calibration
import com.rulersnap.measure.domain.model.MeasureUnit
import com.rulersnap.measure.domain.model.RulerOrientation
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<AppSettings>

    val defaultPxPerMm: Float

    fun setCalibration(calibration: Calibration)

    fun setUnit(unit: MeasureUnit)

    fun setOrientation(orientation: RulerOrientation)

    fun setHaptics(enabled: Boolean)

    fun markCalibrationHintSeen()
}
