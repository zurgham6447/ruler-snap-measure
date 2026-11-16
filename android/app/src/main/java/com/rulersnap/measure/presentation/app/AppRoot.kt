package com.rulersnap.measure.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rulersnap.measure.core.config.GameConfig
import com.rulersnap.measure.core.di.AppViewModelFactory
import com.rulersnap.measure.core.navigation.Screen
import com.rulersnap.measure.core.theme.BackgroundPaper
import com.rulersnap.measure.presentation.about.AboutScreen
import com.rulersnap.measure.presentation.calibration.CalibrationScreen
import com.rulersnap.measure.presentation.game.GameScreen
import com.rulersnap.measure.presentation.history.HistoryScreen
import com.rulersnap.measure.presentation.menu.MenuScreen
import com.rulersnap.measure.presentation.result.MeasurementResultScreen
import com.rulersnap.measure.presentation.settings.SettingsScreen
import com.rulersnap.measure.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.factory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()
    val forward by appViewModel.forward.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) { appViewModel.back() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPaper)
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                if (targetState == Screen.Splash || initialState == Screen.Splash) {
                    fadeIn(tween(260)) togetherWith fadeOut(tween(220))
                } else {
                    val direction = if (forward) 1 else -1
                    val duration = GameConfig.ANIM_MEDIUM_MS
                    (
                        slideInHorizontally(tween(duration)) { full -> direction * full / 12 } +
                            fadeIn(tween(duration))
                        ) togetherWith (
                        slideOutHorizontally(tween(duration)) { full -> -direction * full / 12 } +
                            fadeOut(tween(duration))
                        )
                }
            },
            label = "screenTransition"
        ) { current ->
            when (current) {
                is Screen.Splash -> SplashScreen(
                    onFinished = { appViewModel.navigate(Screen.Menu) }
                )

                is Screen.Menu -> MenuScreen(
                    onStart = { appViewModel.navigate(Screen.Game) },
                    onHistory = { appViewModel.navigate(Screen.History) },
                    onCalibration = { appViewModel.navigate(Screen.Calibration) },
                    onSettings = { appViewModel.navigate(Screen.Settings) },
                    onAbout = { appViewModel.navigate(Screen.About) }
                )

                is Screen.Game -> GameScreen(
                    onBack = { appViewModel.back() },
                    onCalibration = { appViewModel.navigate(Screen.Calibration) },
                    onMeasured = { id, millimetres, label ->
                        appViewModel.navigate(Screen.Result(id, millimetres, label))
                    }
                )

                is Screen.Result -> MeasurementResultScreen(
                    measurementId = current.id,
                    millimetres = current.millimetres,
                    initialLabel = current.label,
                    onMeasureAgain = { appViewModel.back() },
                    onMenu = { appViewModel.backToMenu() }
                )

                is Screen.Calibration -> CalibrationScreen(
                    onBack = { appViewModel.back() }
                )

                is Screen.History -> HistoryScreen(
                    onBack = { appViewModel.back() },
                    onStart = { appViewModel.navigate(Screen.Game) }
                )

                is Screen.Settings -> SettingsScreen(
                    onBack = { appViewModel.back() },
                    onCalibration = { appViewModel.navigate(Screen.Calibration) }
                )

                is Screen.About -> AboutScreen(
                    onBack = { appViewModel.back() },
                    onMenu = { appViewModel.backToMenu() }
                )
            }
        }
    }
}
