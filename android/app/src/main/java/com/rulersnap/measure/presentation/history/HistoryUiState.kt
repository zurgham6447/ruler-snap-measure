package com.rulersnap.measure.presentation.history

import com.rulersnap.measure.domain.model.MeasureUnit

data class HistoryEntry(
    val id: Long,
    val label: String,
    val valueText: String,
    val dateText: String
)

data class HistoryUiState(
    val entries: List<HistoryEntry> = emptyList(),
    val unit: MeasureUnit = MeasureUnit.MM,
    val savedCount: Int = 0,
    val averageText: String = "",
    val hasAverage: Boolean = false,
    val clearRequested: Boolean = false
)
