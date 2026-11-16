package com.rulersnap.measure.domain.usecase

import com.rulersnap.measure.domain.model.Measurement
import com.rulersnap.measure.domain.repository.MeasurementRepository

class SaveMeasurementUseCase(
    private val repository: MeasurementRepository
) {

    operator fun invoke(millimetres: Float, label: String, now: Long): Measurement {
        val measurement = Measurement(
            id = now,
            label = label,
            millimetres = millimetres,
            createdAt = now
        )
        repository.add(measurement)
        return measurement
    }

    fun nextIndex(existing: Int): Int = existing + 1
}
