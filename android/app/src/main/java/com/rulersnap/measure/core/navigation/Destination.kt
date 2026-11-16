package com.rulersnap.measure.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object Game : Screen
    data class Result(val id: Long, val millimetres: Float, val label: String) : Screen
    data object Calibration : Screen
    data object History : Screen
    data object Settings : Screen
    data object About : Screen
}
