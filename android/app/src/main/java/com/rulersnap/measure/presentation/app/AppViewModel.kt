package com.rulersnap.measure.presentation.app

import androidx.lifecycle.ViewModel
import com.rulersnap.measure.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = mutableListOf<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _forward = MutableStateFlow(true)
    val forward: StateFlow<Boolean> = _forward.asStateFlow()

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current == target) return
        if (current != Screen.Splash) {
            backStack.add(current)
        }
        _forward.value = true
        _screen.value = target
        syncBackState()
    }

    fun back() {
        if (backStack.isEmpty()) return
        val lastIndex = backStack.size - 1
        val previous = backStack[lastIndex]
        backStack.removeAt(lastIndex)
        _forward.value = false
        _screen.value = previous
        syncBackState()
    }

    fun backToMenu() {
        backStack.clear()
        _forward.value = false
        _screen.value = Screen.Menu
        syncBackState()
    }

    private fun syncBackState() {
        _canGoBack.value = backStack.isNotEmpty()
    }
}
