package com.rulersnap.measure.presentation.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rulersnap.measure.R
import com.rulersnap.measure.core.theme.MarkerRed
import com.rulersnap.measure.core.theme.SurfaceCard
import com.rulersnap.measure.core.theme.TextPrimary
import com.rulersnap.measure.core.theme.TextSecondary

@Composable
fun ClearHistoryDialog(
    count: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = SurfaceCard,
        tonalElevation = 0.dp,
        title = {
            Text(
                text = stringResource(R.string.dialog_clear_title),
                color = TextPrimary,
                fontSize = 20.sp,
                lineHeight = 26.sp,
                letterSpacing = 0.8.sp,
                fontWeight = FontWeight.Black
            )
        },
        text = {
            Text(
                text = stringResource(R.string.dialog_clear_body, count),
                color = TextSecondary,
                fontSize = 15.sp,
                lineHeight = 21.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.cta_clear),
                    color = MarkerRed,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Black
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cta_cancel),
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
