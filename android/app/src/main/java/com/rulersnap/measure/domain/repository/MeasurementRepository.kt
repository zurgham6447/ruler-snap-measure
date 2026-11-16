package com.rulersnap.measure.domain.repository

import com.rulersnap.measure.domain.model.Measurement
import kotlinx.coroutines.flow.StateFlow

interface MeasurementRepository {
    val measurements: StateFlow<List<Measurement>>

    fun add(measurement: Measurement)

    fun rename(id: Long, label: String)

    fun remove(id: Long)

    fun removeAll()
}
