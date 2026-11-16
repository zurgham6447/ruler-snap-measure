package com.rulersnap.measure.presentation.menu

import com.rulersnap.measure.domain.model.MeasureUnit

data class MenuUiState(
    val calibrated: Boolean = false,
    val unit: MeasureUnit = MeasureUnit.MM,
    val savedCount: Int = 0,
    val lastValueText: String = "",
    val hasLast: Boolean = false
)
