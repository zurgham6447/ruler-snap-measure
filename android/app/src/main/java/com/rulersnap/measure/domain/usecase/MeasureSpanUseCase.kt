package com.rulersnap.measure.domain.usecase

import com.rulersnap.measure.core.config.GameConfig
import kotlin.math.abs

class MeasureSpanUseCase {

    operator fun invoke(markerAPx: Float, markerBPx: Float, pxPerMm: Float): Float {
        if (pxPerMm <= 0f) return 0f
        val span = abs(markerBPx - markerAPx) / pxPerMm
        return if (span < GameConfig.MIN_SPAN_MM) 0f else span
    }

    fun clampToBed(valuePx: Float, bedSizePx: Float): Float = valuePx.coerceIn(0f, bedSizePx)
}
