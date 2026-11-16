package com.rulersnap.measure.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rulersnap.measure.R
import com.rulersnap.measure.core.theme.BackgroundDeep
import com.rulersnap.measure.core.theme.BackgroundPaper
import com.rulersnap.measure.core.theme.OutlineSoft
import com.rulersnap.measure.core.theme.TextMuted
import com.rulersnap.measure.core.theme.TextPrimary

@Composable
fun ScreenScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    topOverlay: Color = BackgroundPaper.copy(alpha = 0.90f),
    bottomOverlay: Color = BackgroundDeep.copy(alpha = 0.78f),
    headerTrailing: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    ScreenBackground(modifier = modifier, topOverlay = topOverlay, bottomOverlay = bottomOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 8.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = stringResource(R.string.cd_back),
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                ) {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        lineHeight = 21.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = TextMuted,
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                headerTrailing()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(OutlineSoft)
            )
            content()
        }
    }
}
