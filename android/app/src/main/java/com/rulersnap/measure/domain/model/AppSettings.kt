package com.rulersnap.measure.domain.model

data class AppSettings(
    val calibration: Calibration,
    val unit: MeasureUnit,
    val orientation: RulerOrientation,
    val hapticsEnabled: Boolean,
    val calibrationHintSeen: Boolean
)
