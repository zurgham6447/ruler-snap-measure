package com.rulersnap.measure.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rulersnap.measure.domain.repository.MeasurementRepository
import com.rulersnap.measure.domain.repository.SettingsRepository
import com.rulersnap.measure.domain.usecase.FormatMeasurementUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val measurements: MeasurementRepository,
    settings: SettingsRepository,
    private val format: FormatMeasurementUseCase
) : ViewModel() {

    private val dateFormat = SimpleDateFormat(DATE_PATTERN, Locale.US)

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(measurements.measurements, settings.settings) { items, config ->
                val average = if (items.isEmpty()) {
                    0f
                } else {
                    items.sumOf { entry -> entry.millimetres.toDouble() }.toFloat() / items.size
                }
                HistoryUiState(
                    entries = items.map { entry ->
                        HistoryEntry(
                            id = entry.id,
                            label = entry.label,
                            valueText = format.value(entry.millimetres, config.unit),
                            dateText = dateFormat.format(Date(entry.createdAt))
                        )
                    },
                    unit = config.unit,
                    savedCount = items.size,
                    averageText = format.value(average, config.unit),
                    hasAverage = items.size >= 2
                )
            }.collect { snapshot ->
                _uiState.update { current ->
                    snapshot.copy(clearRequested = current.clearRequested)
                }
            }
        }
    }

    fun onRemove(id: Long) {
        measurements.remove(id)
    }

    fun onClearRequested() {
        if (_uiState.value.entries.isEmpty()) return
        _uiState.update { it.copy(clearRequested = true) }
    }

    fun onClearDismissed() {
        _uiState.update { it.copy(clearRequested = false) }
    }

    fun onClearConfirmed() {
        measurements.removeAll()
        _uiState.update { it.copy(clearRequested = false) }
    }

    companion object {
        private const val DATE_PATTERN = "dd MMM yyyy"
    }
}
