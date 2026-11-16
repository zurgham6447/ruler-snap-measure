package com.rulersnap.measure.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rulersnap.measure.R
import com.rulersnap.measure.core.di.AppViewModelFactory
import com.rulersnap.measure.core.theme.AccentBlue
import com.rulersnap.measure.core.theme.BackgroundDeep
import com.rulersnap.measure.core.theme.OutlineSoft
import com.rulersnap.measure.core.theme.PrimaryOlive
import com.rulersnap.measure.core.theme.SecondarySage
import com.rulersnap.measure.core.theme.SurfaceCard
import com.rulersnap.measure.core.theme.TextMuted
import com.rulersnap.measure.core.theme.TextPrimary
import com.rulersnap.measure.core.theme.TextSecondary
import com.rulersnap.measure.core.theme.TickBlack
import com.rulersnap.measure.core.ui.ScreenBackground

private const val TICK_COUNT = 21

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val splashViewModel: SplashViewModel = viewModel(factory = AppViewModelFactory.factory)
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()
    var handled by remember { mutableStateOf(false) }

    LaunchedEffect(finished) {
        if (finished && !handled) {
            handled = true
            onFinished()
        }
    }

    SplashContent(modifier = modifier)
}

@Composable
private fun SplashContent(modifier: Modifier = Modifier) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        appeared = true
    }

    val transition = rememberInfiniteTransition(label = "splashLoop")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing)),
        label = "tickSweep"
    )
    val breathe by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "plateBreathe"
    )
    val ringA by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "ringA"
    )
    val ringB by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            initialStartOffset = StartOffset(1500)
        ),
        label = "ringB"
    )

    val plateAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(500),
        label = "plateAlpha"
    )
    val plateLift by animateDpAsState(
        targetValue = if (appeared) 0.dp else 16.dp,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow),
        label = "plateLift"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(durationMillis = 420, delayMillis = 120),
        label = "titleAlpha"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(durationMillis = 420, delayMillis = 220),
        label = "subtitleAlpha"
    )

    val loadingState = stringResource(R.string.state_loading)

    ScreenBackground(
        modifier = modifier,
        topOverlay = BackgroundDeep.copy(alpha = 0.93f),
        bottomOverlay = PrimaryOlive.copy(alpha = 0.55f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(300.dp)) {
                    val base = size.minDimension * 0.34f
                    listOf(ringA, ringB).forEach { progress ->
                        val radius = base * (0.9f + 0.35f * progress)
                        drawCircle(
                            color = SecondarySage.copy(alpha = (0.35f * (1f - progress)).coerceAtLeast(0f)),
                            radius = radius,
                            center = Offset(size.width / 2f, size.height / 2f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, OutlineSoft),
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .size(240.dp)
                        .offset(y = plateLift)
                        .alpha(plateAlpha)
                        .graphicsLayer {
                            scaleX = breathe
                            scaleY = breathe
                        }
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = stringResource(R.string.cd_logo),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.width(180.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            ) {
                val step = size.width / (TICK_COUNT - 1)
                val head = sweep * (TICK_COUNT - 1)
                for (i in 0 until TICK_COUNT) {
                    val passed = i <= head
                    val tall = i % 5 == 0
                    val lineHeight = if (tall) size.height else size.height * 0.55f
                    drawLine(
                        color = if (passed) PrimaryOlive else TickBlack.copy(alpha = 0.35f),
                        start = Offset(i * step, size.height - lineHeight),
                        end = Offset(i * step, size.height),
                        strokeWidth = if (tall) 3f else 2f
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.splash_title),
                color = TextPrimary,
                fontSize = 34.sp,
                lineHeight = 40.sp,
                letterSpacing = 2.5.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.alpha(titleAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.splash_subtitle),
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                letterSpacing = 3.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(subtitleAlpha)
            )

            Spacer(modifier = Modifier.height(40.dp))

            LinearProgressIndicator(
                color = AccentBlue,
                trackColor = SecondarySage,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                modifier = Modifier
                    .width(160.dp)
                    .height(4.dp)
                    .semantics { stateDescription = loadingState }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.splash_caption),
                color = TextMuted,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                letterSpacing = 1.4.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
