package com.rulersnap.measure.presentation.calibration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rulersnap.measure.core.config.GameConfig
import com.rulersnap.measure.domain.model.Calibration
import com.rulersnap.measure.domain.repository.SettingsRepository
import com.rulersnap.measure.domain.usecase.CalibrateFromCardUseCase
import com.rulersnap.measure.domain.usecase.FormatMeasurementUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CalibrationViewModel(
    private val settings: SettingsRepository,
    private val calibrate: CalibrateFromCardUseCase,
    private val format: FormatMeasurementUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalibrationUiState())
    val uiState: StateFlow<CalibrationUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<CalibrationEvent>(Channel.BUFFERED)
    val events: Flow<CalibrationEvent> = eventChannel.receiveAsFlow()

    private var repeatJob: Job? = null
    private var confirmJob: Job? = null

    init {
        val current = settings.settings.value.calibration
        _uiState.update { state -> withScale(state, current.pxPerMm, current.isUserCalibrated) }
    }

    fun onStep(direction: Int) {
        applyScale(_uiState.value.pxPerMm + direction * GameConfig.STEPPER_STEP_PX_PER_MM)
    }

    fun onStepHoldStart(direction: Int) {
        repeatJob?.cancel()
        repeatJob = viewModelScope.launch {
            delay(GameConfig.STEPPER_REPEAT_MS * 4)
            while (isActive) {
                onStep(direction)
                delay(GameConfig.STEPPER_REPEAT_MS)
            }
        }
    }

    fun onStepHoldStop() {
        repeatJob?.cancel()
        repeatJob = null
    }

    fun onSliderChanged(value: Float) {
        applyScale(value)
    }

    fun onCardWidthDrag(deltaPx: Float) {
        val next = _uiState.value.cardWidthPx + deltaPx
        if (next <= 0f) return
        applyScale(next / GameConfig.CARD_WIDTH_MM)
    }

    fun onResetToDefault() {
        onStepHoldStop()
        val fallback = calibrate.clamp(settings.defaultPxPerMm)
        settings.setCalibration(Calibration(fallback, false))
        _uiState.update { state -> withScale(state, fallback, false) }
    }

    fun onSaveCalibration() {
        onStepHoldStop()
        val target = calibrate.fromPxPerMm(_uiState.value.pxPerMm)
        settings.setCalibration(target)
        _uiState.update { state -> withScale(state, target.pxPerMm, true).copy(saved = true) }
        confirmJob?.cancel()
        confirmJob = viewModelScope.launch {
            delay(GameConfig.SAVE_FEEDBACK_MS)
            _uiState.update { it.copy(saved = false) }
            eventChannel.send(CalibrationEvent.Stored)
        }
    }

    fun pause() {
        onStepHoldStop()
    }

    override fun onCleared() {
        super.onCleared()
        onStepHoldStop()
    }

    private fun applyScale(raw: Float) {
        val clamped = calibrate.clamp(raw)
        _uiState.update { state -> withScale(state, clamped, state.userCalibrated) }
    }

    private fun withScale(
        state: CalibrationUiState,
        pxPerMm: Float,
        userCalibrated: Boolean
    ): CalibrationUiState {
        val width = calibrate.cardWidthPx(pxPerMm)
        return state.copy(
            pxPerMm = pxPerMm,
            pxPerMmText = format.scale(pxPerMm),
            cardWidthPx = width,
            cardHeightPx = calibrate.cardHeightPx(width),
            cardWidthMmText = format.millimetres(GameConfig.CARD_WIDTH_MM),
            userCalibrated = userCalibrated,
            minPxPerMm = GameConfig.MIN_PX_PER_MM,
            maxPxPerMm = GameConfig.MAX_PX_PER_MM
        )
    }
}

sealed interface CalibrationEvent {
    data object Stored : CalibrationEvent
}
