package com.rulersnap.measure.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rulersnap.measure.core.config.GameConfig
import com.rulersnap.measure.domain.model.MeasureUnit
import com.rulersnap.measure.domain.model.RulerOrientation
import com.rulersnap.measure.domain.repository.SettingsRepository
import com.rulersnap.measure.domain.usecase.FormatMeasurementUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settings: SettingsRepository,
    private val format: FormatMeasurementUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var confirmJob: Job? = null

    init {
        viewModelScope.launch {
            settings.settings.collect { config ->
                _uiState.update { current ->
                    current.copy(
                        unit = config.unit,
                        orientation = config.orientation,
                        hapticsEnabled = config.hapticsEnabled,
                        scaleText = format.scale(config.calibration.pxPerMm),
                        userCalibrated = config.calibration.isUserCalibrated
                    )
                }
            }
        }
    }

    fun onSelectUnit(unit: MeasureUnit) {
        if (unit == _uiState.value.unit) return
        settings.setUnit(unit)
        confirm(SettingsSection.UNITS)
    }

    fun onSelectOrientation(orientation: RulerOrientation) {
        if (orientation == _uiState.value.orientation) return
        settings.setOrientation(orientation)
        confirm(SettingsSection.ORIENTATION)
    }

    fun onToggleHaptics(enabled: Boolean) {
        settings.setHaptics(enabled)
        confirm(SettingsSection.FEEDBACK)
    }

    private fun confirm(section: SettingsSection) {
        _uiState.update { it.copy(confirmedSection = section) }
        confirmJob?.cancel()
        confirmJob = viewModelScope.launch {
            delay(GameConfig.ANIM_FAST_MS.toLong() * 4)
            _uiState.update { it.copy(confirmedSection = SettingsSection.NONE) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        confirmJob?.cancel()
    }
}
