package com.rulersnap.measure.presentation.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.rulersnap.measure.core.theme.MarkerRed
import com.rulersnap.measure.core.theme.SurfaceCard
import kotlin.math.roundToInt

private val HandleArea = 56.dp
private val HandleCircle = 44.dp
private val HandleDot = 14.dp

@Composable
fun MarkerHandle(
    position: Float,
    horizontal: Boolean,
    locked: Boolean,
    description: String,
    state: String,
    onDrag: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val dragModifier = if (locked) {
        Modifier
    } else {
        Modifier.pointerInput(horizontal) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                onDrag(if (horizontal) dragAmount.x else dragAmount.y)
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .then(
                if (horizontal) {
                    Modifier
                        .fillMaxHeight()
                        .width(HandleArea)
                } else {
                    Modifier
                        .fillMaxWidth()
                        .height(HandleArea)
                }
            )
            .offset {
                val shift = (position - HandleArea.toPx() / 2f).roundToInt()
                if (horizontal) IntOffset(shift, 0) else IntOffset(0, shift)
            }
            .alpha(if (locked) 0.6f else 1f)
            .semantics {
                contentDescription = description
                stateDescription = state
            }
    ) {
        Box(
            modifier = if (horizontal) {
                Modifier
                    .fillMaxHeight()
                    .width(2.dp)
                    .background(MarkerRed)
            } else {
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(MarkerRed)
            }
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(HandleCircle)
                .shadow(6.dp, CircleShape)
                .background(SurfaceCard, CircleShape)
                .border(2.dp, MarkerRed, CircleShape)
                .then(dragModifier)
        ) {
            Box(
                modifier = Modifier
                    .size(HandleDot)
                    .background(MarkerRed, CircleShape)
            )
        }
    }
}
