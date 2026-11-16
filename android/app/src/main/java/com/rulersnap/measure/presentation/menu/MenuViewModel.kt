package com.rulersnap.measure.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rulersnap.measure.domain.repository.MeasurementRepository
import com.rulersnap.measure.domain.repository.SettingsRepository
import com.rulersnap.measure.domain.usecase.FormatMeasurementUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModel(
    measurements: MeasurementRepository,
    settings: SettingsRepository,
    private val format: FormatMeasurementUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(measurements.measurements, settings.settings) { items, config ->
                val latest = items.maxByOrNull { entry -> entry.createdAt }
                MenuUiState(
                    calibrated = config.calibration.isUserCalibrated,
                    unit = config.unit,
                    savedCount = items.size,
                    lastValueText = if (latest == null) {
                        ""
                    } else {
                        format.value(latest.millimetres, config.unit)
                    },
                    hasLast = latest != null
                )
            }.collect { snapshot ->
                _uiState.update { snapshot }
            }
        }
    }
}
