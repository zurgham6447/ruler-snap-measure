package com.rulersnap.measure.domain.model

enum class RulerOrientation {
    VERTICAL,
    HORIZONTAL;

    companion object {
        fun fromKey(key: String): RulerOrientation = entries.firstOrNull { it.name == key } ?: VERTICAL
    }
}
