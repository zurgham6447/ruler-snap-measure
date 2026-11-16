package com.rulersnap.measure.data.repository

import com.rulersnap.measure.core.config.GameConfig
import com.rulersnap.measure.data.local.MeasurePreferences
import com.rulersnap.measure.data.sample.SampleData
import com.rulersnap.measure.domain.model.Measurement
import com.rulersnap.measure.domain.repository.MeasurementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MeasurementRepositoryImpl(
    private val preferences: MeasurePreferences
) : MeasurementRepository {

    private val state = MutableStateFlow(load())

    override val measurements: StateFlow<List<Measurement>> = state.asStateFlow()

    override fun add(measurement: Measurement) {
        val next = (listOf(measurement) + state.value.filter { it.id != measurement.id })
            .take(GameConfig.MAX_HISTORY)
        publish(next)
    }

    override fun rename(id: Long, label: String) {
        val cleaned = label.trim()
        if (cleaned.isEmpty()) return
        publish(state.value.map { if (it.id == id) it.copy(label = cleaned) else it })
    }

    override fun remove(id: Long) {
        publish(state.value.filter { it.id != id })
    }

    override fun removeAll() {
        publish(emptyList())
    }

    private fun publish(items: List<Measurement>) {
        val sorted = items.sortedByDescending { it.createdAt }
        preferences.writeHistory(sorted)
        state.value = sorted
    }

    private fun load(): List<Measurement> {
        val stored = preferences.readHistory()
        if (stored.isNotEmpty()) return stored.sortedByDescending { it.createdAt }
        if (!preferences.isFirstLaunch) return emptyList()
        val seeded = SampleData.seedMeasurements(System.currentTimeMillis())
        preferences.writeHistory(seeded)
        return seeded
    }
}
