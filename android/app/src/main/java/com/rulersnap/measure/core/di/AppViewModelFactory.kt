package com.rulersnap.measure.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rulersnap.measure.presentation.about.AboutViewModel
import com.rulersnap.measure.presentation.app.AppViewModel
import com.rulersnap.measure.presentation.calibration.CalibrationViewModel
import com.rulersnap.measure.presentation.game.GameViewModel
import com.rulersnap.measure.presentation.history.HistoryViewModel
import com.rulersnap.measure.presentation.menu.MenuViewModel
import com.rulersnap.measure.presentation.result.MeasurementResultViewModel
import com.rulersnap.measure.presentation.settings.SettingsViewModel
import com.rulersnap.measure.presentation.splash.SplashViewModel

object AppViewModelFactory {

    val factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { AppViewModel() }
        initializer { SplashViewModel() }
        initializer {
            MenuViewModel(
                ServiceLocator.measurements,
                ServiceLocator.settings,
                ServiceLocator.format
            )
        }
        initializer {
            GameViewModel(
                ServiceLocator.settings,
                ServiceLocator.measureSpan,
                ServiceLocator.format,
                ServiceLocator.store,
                ServiceLocator.measurements
            )
        }
        initializer {
            MeasurementResultViewModel(
                ServiceLocator.measurements,
                ServiceLocator.format
            )
        }
        initializer {
            CalibrationViewModel(
                ServiceLocator.settings,
                ServiceLocator.calibrate,
                ServiceLocator.format
            )
        }
        initializer {
            HistoryViewModel(
                ServiceLocator.measurements,
                ServiceLocator.settings,
                ServiceLocator.format
            )
        }
        initializer {
            SettingsViewModel(
                ServiceLocator.settings,
                ServiceLocator.format
            )
        }
        initializer { AboutViewModel(ServiceLocator.measurements) }
    }
}
