package com.rulersnap.measure.presentation.game

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.rulersnap.measure.core.theme.AccentBlue
import com.rulersnap.measure.core.theme.TextPrimary
import com.rulersnap.measure.core.theme.TickBlack


private const val MM_PER_CM = 10
private const val SIXTEENTHS_PER_INCH = 16
private const val MM_PER_INCH = 25.4f

@Composable
fun RulerCanvas(
    pxPerMm: Float,
    horizontal: Boolean,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = remember {
        TextStyle(
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }

    Canvas(modifier = modifier) {
        if (pxPerMm <= 0f) return@Canvas
        val axis = if (horizontal) size.width else size.height
        val millimetreCount = (axis / pxPerMm).toInt()
        val faint = TickBlack.copy(alpha = 0.55f)
        val medium = TickBlack.copy(alpha = 0.80f)

        for (index in 0..millimetreCount) {
            val position = index * pxPerMm
            val length = when {
                index % 10 == 0 -> 28.dpPx(density)
                index % 5 == 0 -> 18.dpPx(density)
                else -> 10.dpPx(density)
            }
            val color = when {
                index % 10 == 0 -> TickBlack
                index % 5 == 0 -> medium
                else -> faint
            }
            val stroke = when {
                index % 10 == 0 -> 1.8f * density
                index % 5 == 0 -> 1.4f * density
                else -> 1f * density
            }
            if (horizontal) {
                drawLine(
                    color = color,
                    start = Offset(position, size.height),
                    end = Offset(position, size.height - length),
                    strokeWidth = stroke
                )
            } else {
                drawLine(
                    color = color,
                    start = Offset(0f, position),
                    end = Offset(length, position),
                    strokeWidth = stroke
                )
            }
            if (index % MM_PER_CM == 0 && index > 0) {
                val label = (index / MM_PER_CM).toString()
                val layout = textMeasurer.measure(label, labelStyle)
                val topLeft = if (horizontal) {
                    Offset(
                        position + 4f * density,
                        size.height - 34.dpPx(density) - layout.size.height
                    )
                } else {
                    Offset(34.dpPx(density), position + 3f * density)
                }
                drawText(textLayoutResult = layout, topLeft = topLeft)
            }
        }

        val sixteenth = pxPerMm * MM_PER_INCH / SIXTEENTHS_PER_INCH
        if (sixteenth > 0f) {
            val inchCount = (axis / sixteenth).toInt()
            for (index in 0..inchCount) {
                val position = index * sixteenth
                val length = when {
                    index % 16 == 0 -> 30.dpPx(density)
                    index % 8 == 0 -> 22.dpPx(density)
                    index % 4 == 0 -> 16.dpPx(density)
                    index % 2 == 0 -> 12.dpPx(density)
                    else -> 8.dpPx(density)
                }
                val color = if (index % 16 == 0) TickBlack else faint
                val stroke = if (index % 16 == 0) 1.8f * density else 1f * density
                if (horizontal) {
                    drawLine(
                        color = color,
                        start = Offset(position, 0f),
                        end = Offset(position, length),
                        strokeWidth = stroke
                    )
                } else {
                    drawLine(
                        color = color,
                        start = Offset(size.width, position),
                        end = Offset(size.width - length, position),
                        strokeWidth = stroke
                    )
                }
                if (index % SIXTEENTHS_PER_INCH == 0 && index > 0) {
                    val label = (index / SIXTEENTHS_PER_INCH).toString()
                    val layout = textMeasurer.measure(label, labelStyle)
                    val topLeft = if (horizontal) {
                        Offset(position + 4f * density, 34.dpPx(density))
                    } else {
                        Offset(
                            size.width - 36.dpPx(density) - layout.size.width,
                            position + 3f * density
                        )
                    }
                    drawText(textLayoutResult = layout, topLeft = topLeft)
                }
            }
        }
    }
}

@Composable
fun SpanOverlay(
    spanStart: Float,
    spanEnd: Float,
    horizontal: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val start = spanStart.coerceAtLeast(0f)
        val end = spanEnd.coerceAtLeast(start)
        val thickness = end - start
        if (thickness <= 0f) return@Canvas
        val fill: Color = AccentBlue.copy(alpha = 0.10f)
        if (horizontal) {
            drawRect(
                color = fill,
                topLeft = Offset(start, 0f),
                size = Size(thickness, size.height)
            )
            drawLine(
                color = AccentBlue,
                start = Offset(start, 0f),
                end = Offset(start, size.height),
                strokeWidth = 1.5f * density
            )
            drawLine(
                color = AccentBlue,
                start = Offset(end, 0f),
                end = Offset(end, size.height),
                strokeWidth = 1.5f * density
            )
        } else {
            drawRect(
                color = fill,
                topLeft = Offset(0f, start),
                size = Size(size.width, thickness)
            )
            drawLine(
                color = AccentBlue,
                start = Offset(0f, start),
                end = Offset(size.width, start),
                strokeWidth = 1.5f * density
            )
            drawLine(
                color = AccentBlue,
                start = Offset(0f, end),
                end = Offset(size.width, end),
                strokeWidth = 1.5f * density
            )
        }
    }
}

private fun Int.dpPx(density: Float): Float = this * density
