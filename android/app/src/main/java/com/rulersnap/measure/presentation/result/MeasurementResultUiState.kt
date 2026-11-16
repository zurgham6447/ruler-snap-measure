package com.rulersnap.measure.presentation.result

data class MeasurementResultUiState(
    val label: String = "",
    val millimetres: Float = 0f,
    val millimetresText: String = "0.0",
    val centimetresText: String = "0.00",
    val inchesText: String = "0.00",
    val savedCount: Int = 0,
    val bestSpanText: String = "",
    val hasBest: Boolean = false
)
