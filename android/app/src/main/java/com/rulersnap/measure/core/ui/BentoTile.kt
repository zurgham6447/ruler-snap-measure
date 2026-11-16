package com.rulersnap.measure.core.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rulersnap.measure.core.theme.OutlineSoft
import com.rulersnap.measure.core.theme.SurfaceCard
import com.rulersnap.measure.core.theme.TextPrimary
import com.rulersnap.measure.core.theme.TextSecondary

@Composable
fun BentoTile(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    caption: String? = null,
    icon: ImageVector? = null,
    iconPainter: Painter? = null,
    accent: Color = TextSecondary,
    decor: Painter? = null,
    decorSize: Dp = 56.dp,
    gradient: List<Color>? = null,
    titleColor: Color = TextPrimary,
    captionColor: Color = TextSecondary,
    titleSize: Int = 13
) {
    val shape = RoundedCornerShape(18.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "bentoTileScale"
    )

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (gradient == null) SurfaceCard else Color.Transparent
        ),
        border = if (gradient == null) BorderStroke(1.dp, OutlineSoft) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (gradient == null) 3.dp else 8.dp),
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (gradient != null) {
                        Modifier.background(Brush.linearGradient(gradient))
                    } else {
                        Modifier
                    }
                )
        ) {
            if (decor != null) {
                Image(
                    painter = decor,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    alpha = 0.85f,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 10.dp, bottom = 10.dp)
                        .size(decorSize)
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier
                            .size(if (gradient == null) 22.dp else 28.dp)
                            .padding(bottom = 0.dp)
                    )
                } else if (iconPainter != null) {
                    Icon(
                        painter = iconPainter,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(if (gradient == null) 22.dp else 28.dp)
                    )
                }
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = titleSize.sp,
                    lineHeight = (titleSize + 6).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
                if (caption != null) {
                    Text(
                        text = caption,
                        color = captionColor,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        letterSpacing = 0.6.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }
        }
    }
}
