package com.rulersnap.measure.presentation.game

import com.rulersnap.measure.domain.model.MeasureUnit

data class GameUiState(
    val pxPerMm: Float = 10f,
    val calibrated: Boolean = false,
    val unit: MeasureUnit = MeasureUnit.MM,
    val horizontal: Boolean = false,
    val bedWidth: Float = 0f,
    val bedHeight: Float = 0f,
    val markerA: Float = 0f,
    val markerB: Float = 0f,
    val locked: Boolean = false,
    val untouched: Boolean = true,
    val spanMm: Float = 0f,
    val spanValueText: String = "0.0",
    val millimetresText: String = "0.0",
    val inchFractionText: String = "0",
    val inchDecimalText: String = "0.00",
    val scaleText: String = "0.00",
    val canStore: Boolean = false,
    val showConfirm: Boolean = false,
    val hintVisible: Boolean = false,
    val hapticsEnabled: Boolean = true
) {
    val axisLength: Float
        get() = if (horizontal) bedWidth else bedHeight

    val spanStart: Float
        get() = if (markerA <= markerB) markerA else markerB

    val spanEnd: Float
        get() = if (markerA <= markerB) markerB else markerA
}

sealed interface GameEvent {
    data class Measured(val id: Long, val millimetres: Float, val label: String) : GameEvent
}
