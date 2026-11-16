package com.rulersnap.measure.presentation.calibration

data class CalibrationUiState(
    val pxPerMm: Float = 10f,
    val pxPerMmText: String = "10.00",
    val cardWidthPx: Float = 0f,
    val cardHeightPx: Float = 0f,
    val cardWidthMmText: String = "85.60",
    val userCalibrated: Boolean = false,
    val minPxPerMm: Float = 2f,
    val maxPxPerMm: Float = 24f,
    val saved: Boolean = false
)
