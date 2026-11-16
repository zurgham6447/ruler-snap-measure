package com.rulersnap.measure.data.sample

import com.rulersnap.measure.domain.model.Measurement

object SampleData {

    private const val DAY_MS = 86_400_000L

    fun seedMeasurements(now: Long): List<Measurement> = listOf(
        Measurement(
            id = now,
            label = "SD card width",
            millimetres = 24.0f,
            createdAt = now
        ),
        Measurement(
            id = now - DAY_MS,
            label = "Shirt button",
            millimetres = 11.4f,
            createdAt = now - DAY_MS
        ),
        Measurement(
            id = now - 2 * DAY_MS,
            label = "M3 screw",
            millimetres = 12.0f,
            createdAt = now - 2 * DAY_MS
        )
    )
}
