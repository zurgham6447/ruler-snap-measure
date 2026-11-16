package com.rulersnap.measure.domain.model

data class Measurement(
    val id: Long,
    val label: String,
    val millimetres: Float,
    val createdAt: Long
)
