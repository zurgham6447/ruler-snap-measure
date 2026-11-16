package com.rulersnap.measure.data.repository

import com.rulersnap.measure.data.local.MeasurePreferences
import com.rulersnap.measure.domain.model.AppSettings
import com.rulersnap.measure.domain.model.Calibration
import com.rulersnap.measure.domain.model.MeasureUnit
import com.rulersnap.measure.domain.model.RulerOrientation
import com.rulersnap.measure.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsRepositoryImpl(
    private val preferences: MeasurePreferences
) : SettingsRepository {

    private val state = MutableStateFlow(read())

    override val settings: StateFlow<AppSettings> = state.asStateFlow()

    override val defaultPxPerMm: Float = preferences.devicePxPerMm

    override fun setCalibration(calibration: Calibration) {
        preferences.pxPerMm = calibration.pxPerMm
        preferences.isUserCalibrated = calibration.isUserCalibrated
        state.update { it.copy(calibration = calibration) }
    }

    override fun setUnit(unit: MeasureUnit) {
        preferences.unitKey = unit.name
        state.update { it.copy(unit = unit) }
    }

    override fun setOrientation(orientation: RulerOrientation) {
        preferences.orientationKey = orientation.name
        state.update { it.copy(orientation = orientation) }
    }

    override fun setHaptics(enabled: Boolean) {
        preferences.hapticsEnabled = enabled
        state.update { it.copy(hapticsEnabled = enabled) }
    }

    override fun markCalibrationHintSeen() {
        preferences.calibrationHintSeen = true
        state.update { it.copy(calibrationHintSeen = true) }
    }

    private fun read(): AppSettings = AppSettings(
        calibration = Calibration(preferences.pxPerMm, preferences.isUserCalibrated),
        unit = MeasureUnit.fromKey(preferences.unitKey),
        orientation = RulerOrientation.fromKey(preferences.orientationKey),
        hapticsEnabled = preferences.hapticsEnabled,
        calibrationHintSeen = preferences.calibrationHintSeen
    )
}
