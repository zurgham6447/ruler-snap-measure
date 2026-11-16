package com.rulersnap.measure.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rulersnap.measure.domain.repository.MeasurementRepository
import com.rulersnap.measure.domain.usecase.FormatMeasurementUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MeasurementResultViewModel(
    private val measurements: MeasurementRepository,
    private val format: FormatMeasurementUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeasurementResultUiState())
    val uiState: StateFlow<MeasurementResultUiState> = _uiState.asStateFlow()

    private var boundId = 0L

    init {
        viewModelScope.launch {
            measurements.measurements.collect { items ->
                val best = items.maxByOrNull { entry -> entry.millimetres }
                _uiState.update { current ->
                    current.copy(
                        savedCount = items.size,
                        bestSpanText = if (best == null) "" else format.millimetres(best.millimetres),
                        hasBest = best != null && best.millimetres > 0f
                    )
                }
            }
        }
    }

    fun bind(id: Long, millimetres: Float, label: String) {
        if (boundId == id) return
        boundId = id
        _uiState.update { current ->
            current.copy(
                label = label,
                millimetres = millimetres,
                millimetresText = format.millimetres(millimetres),
                centimetresText = format.centimetres(millimetres),
                inchesText = format.inches(millimetres)
            )
        }
    }

    fun onLabelChanged(value: String) {
        val trimmed = value.take(MAX_LABEL)
        _uiState.update { it.copy(label = trimmed) }
        measurements.rename(boundId, trimmed)
    }

    companion object {
        private const val MAX_LABEL = 28
    }
}
