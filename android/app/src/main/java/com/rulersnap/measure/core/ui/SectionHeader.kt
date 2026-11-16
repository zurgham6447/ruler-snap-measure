package com.rulersnap.measure.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rulersnap.measure.core.theme.OutlineSoft
import com.rulersnap.measure.core.theme.TextMuted

@Composable
fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    decor: Painter? = null,
    decorSize: Dp = 40.dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        if (decor != null) {
            Image(
                painter = decor,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                alpha = 0.8f,
                modifier = Modifier
                    .size(decorSize)
                    .padding(end = 8.dp)
            )
        }
        Text(
            text = text,
            color = TextMuted,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Medium
        )
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f)
                .height(1.dp)
                .background(OutlineSoft)
        )
    }
}
