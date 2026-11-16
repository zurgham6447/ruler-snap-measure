package com.rulersnap.measure.domain.usecase

import com.rulersnap.measure.core.config.GameConfig
import com.rulersnap.measure.domain.model.Calibration

class CalibrateFromCardUseCase {

    fun fromCardWidth(cardWidthPx: Float, fallbackPxPerMm: Float): Calibration {
        val raw = cardWidthPx / GameConfig.CARD_WIDTH_MM
        return if (raw in GameConfig.MIN_PX_PER_MM..GameConfig.MAX_PX_PER_MM) {
            Calibration(raw, true)
        } else {
            Calibration(clamp(fallbackPxPerMm), false)
        }
    }

    fun fromPxPerMm(pxPerMm: Float): Calibration = Calibration(clamp(pxPerMm), true)

    fun cardWidthPx(pxPerMm: Float): Float = pxPerMm * GameConfig.CARD_WIDTH_MM

    fun cardHeightPx(cardWidthPx: Float): Float = cardWidthPx / GameConfig.CARD_ASPECT

    fun clamp(pxPerMm: Float): Float =
        pxPerMm.coerceIn(GameConfig.MIN_PX_PER_MM, GameConfig.MAX_PX_PER_MM)
}
