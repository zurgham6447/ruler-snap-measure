package com.rulersnap.measure.domain.model

enum class MeasureUnit {
    MM,
    CM,
    INCH;

    companion object {
        fun fromKey(key: String): MeasureUnit = entries.firstOrNull { it.name == key } ?: MM
    }
}
