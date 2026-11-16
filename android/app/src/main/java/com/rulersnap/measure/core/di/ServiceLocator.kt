package com.rulersnap.measure.core.di

import android.content.Context
import com.rulersnap.measure.data.local.MeasurePreferences
import com.rulersnap.measure.data.repository.MeasurementRepositoryImpl
import com.rulersnap.measure.data.repository.SettingsRepositoryImpl
import com.rulersnap.measure.domain.repository.MeasurementRepository
import com.rulersnap.measure.domain.repository.SettingsRepository
import com.rulersnap.measure.domain.usecase.CalibrateFromCardUseCase
import com.rulersnap.measure.domain.usecase.FormatMeasurementUseCase
import com.rulersnap.measure.domain.usecase.MeasureSpanUseCase
import com.rulersnap.measure.domain.usecase.SaveMeasurementUseCase

object ServiceLocator {

    private class Graph(context: Context) {
        val preferences = MeasurePreferences(context)
        val measurements: MeasurementRepository = MeasurementRepositoryImpl(preferences)
        val settings: SettingsRepository = SettingsRepositoryImpl(preferences)
        val measureSpan = MeasureSpanUseCase()
        val calibrate = CalibrateFromCardUseCase()
        val format = FormatMeasurementUseCase()
        val store = SaveMeasurementUseCase(measurements)
    }

    private var graph: Graph? = null

    fun init(context: Context) {
        if (graph == null) {
            graph = Graph(context.applicationContext)
        }
    }

    val measurements: MeasurementRepository get() = require().measurements
    val settings: SettingsRepository get() = require().settings
    val measureSpan: MeasureSpanUseCase get() = require().measureSpan
    val calibrate: CalibrateFromCardUseCase get() = require().calibrate
    val format: FormatMeasurementUseCase get() = require().format
    val store: SaveMeasurementUseCase get() = require().store

    private fun require(): Graph = checkNotNull(graph)
}
