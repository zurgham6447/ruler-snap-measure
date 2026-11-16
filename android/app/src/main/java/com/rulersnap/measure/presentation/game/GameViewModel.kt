package com.rulersnap.measure.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rulersnap.measure.core.config.GameConfig
import com.rulersnap.measure.domain.model.MeasureUnit
import com.rulersnap.measure.domain.model.RulerOrientation
import com.rulersnap.measure.domain.repository.MeasurementRepository
import com.rulersnap.measure.domain.repository.SettingsRepository
import com.rulersnap.measure.domain.usecase.FormatMeasurementUseCase
import com.rulersnap.measure.domain.usecase.MeasureSpanUseCase
import com.rulersnap.measure.domain.usecase.SaveMeasurementUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val settings: SettingsRepository,
    private val measureSpan: MeasureSpanUseCase,
    private val format: FormatMeasurementUseCase,
    private val store: SaveMeasurementUseCase,
    private val measurements: MeasurementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events: Flow<GameEvent> = eventChannel.receiveAsFlow()

    private var hintEvaluated = false

    init {
        viewModelScope.launch {
            settings.settings.collect { config ->
                val hint = if (hintEvaluated) {
                    _uiState.value.hintVisible
                } else {
                    hintEvaluated = true
                    !config.calibrationHintSeen && !config.calibration.isUserCalibrated
                }
                _uiState.update { current ->
                    recalculate(
                        current.copy(
                            pxPerMm = config.calibration.pxPerMm,
                            calibrated = config.calibration.isUserCalibrated,
                            unit = config.unit,
                            horizontal = config.orientation == RulerOrientation.HORIZONTAL,
                            hapticsEnabled = config.hapticsEnabled,
                            hintVisible = hint
                        )
                    )
                }
            }
        }
    }

    fun onBedMeasured(width: Float, height: Float) {
        _uiState.update { current ->
            val next = current.copy(bedWidth = width, bedHeight = height)
            val axis = next.axisLength
            if (axis <= 0f) {
                next
            } else if (current.untouched || current.axisLength <= 0f) {
                recalculate(
                    next.copy(
                        markerA = axis * GameConfig.MARKER_A_START_FRACTION,
                        markerB = axis * GameConfig.MARKER_B_START_FRACTION
                    )
                )
            } else {
                recalculate(
                    next.copy(
                        markerA = measureSpan.clampToBed(current.markerA, axis),
                        markerB = measureSpan.clampToBed(current.markerB, axis)
                    )
                )
            }
        }
    }

    fun onMarkerDrag(marker: Int, delta: Float) {
        _uiState.update { current ->
            if (current.locked || current.axisLength <= 0f) {
                current
            } else {
                val axis = current.axisLength
                val moved = if (marker == MARKER_A) {
                    current.copy(markerA = measureSpan.clampToBed(current.markerA + delta, axis))
                } else {
                    current.copy(markerB = measureSpan.clampToBed(current.markerB + delta, axis))
                }
                recalculate(moved.copy(untouched = false))
            }
        }
    }

    fun onToggleLock() {
        _uiState.update { current -> current.copy(locked = !current.locked) }
    }

    fun onSelectUnit(unit: MeasureUnit) {
        settings.setUnit(unit)
    }

    fun onReset() {
        _uiState.update { current ->
            val axis = current.axisLength
            if (axis <= 0f) {
                current
            } else {
                recalculate(
                    current.copy(
                        markerA = axis * GameConfig.MARKER_A_START_FRACTION,
                        markerB = axis * GameConfig.MARKER_B_START_FRACTION,
                        locked = false,
                        untouched = true
                    )
                )
            }
        }
    }

    fun onStore(labelPrefix: String) {
        val current = _uiState.value
        if (!current.canStore || current.showConfirm) return
        _uiState.update { it.copy(showConfirm = true) }
        viewModelScope.launch {
            delay(GameConfig.SAVE_FEEDBACK_MS)
            val index = store.nextIndex(measurements.measurements.value.size)
            val label = labelPrefix + SPACE + index.toString()
            val entry = store(current.spanMm, label, System.currentTimeMillis())
            _uiState.update { it.copy(showConfirm = false) }
            eventChannel.send(
                GameEvent.Measured(entry.id, entry.millimetres, entry.label)
            )
        }
    }

    fun onHintConfirmed() {
        settings.markCalibrationHintSeen()
        _uiState.update { it.copy(hintVisible = false) }
    }

    fun onHintDismissed() {
        settings.markCalibrationHintSeen()
        _uiState.update { it.copy(hintVisible = false) }
    }

    fun onScreenLeft() {
        _uiState.update { current ->
            val axis = current.axisLength
            if (axis <= 0f) {
                current.copy(locked = false, showConfirm = false)
            } else {
                recalculate(
                    current.copy(
                        markerA = axis * GameConfig.MARKER_A_START_FRACTION,
                        markerB = axis * GameConfig.MARKER_B_START_FRACTION,
                        locked = false,
                        untouched = true,
                        showConfirm = false
                    )
                )
            }
        }
    }

    private fun recalculate(state: GameUiState): GameUiState {
        val spanMm = measureSpan(state.markerA, state.markerB, state.pxPerMm)
        return state.copy(
            spanMm = spanMm,
            spanValueText = format.value(spanMm, state.unit),
            millimetresText = format.millimetres(spanMm),
            inchFractionText = format.inchFraction(spanMm),
            inchDecimalText = format.inches(spanMm),
            scaleText = format.scale(state.pxPerMm),
            canStore = spanMm >= GameConfig.MIN_SPAN_MM
        )
    }

    companion object {
        const val MARKER_A = 0
        const val MARKER_B = 1
        private const val SPACE = " "
    }
}
