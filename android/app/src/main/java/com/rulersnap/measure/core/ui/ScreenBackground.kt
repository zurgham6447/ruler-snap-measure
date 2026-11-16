package com.rulersnap.measure.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.rulersnap.measure.R
import com.rulersnap.measure.core.theme.BackgroundDeep
import com.rulersnap.measure.core.theme.BackgroundPaper

@Composable
fun ScreenBackground(
    modifier: Modifier = Modifier,
    topOverlay: Color = BackgroundPaper.copy(alpha = 0.90f),
    bottomOverlay: Color = BackgroundDeep.copy(alpha = 0.78f),
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(topOverlay, bottomOverlay)))
        )
        content()
    }
}
