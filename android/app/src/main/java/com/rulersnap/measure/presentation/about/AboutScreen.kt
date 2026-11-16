package com.rulersnap.measure.presentation.about

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rulersnap.measure.R
import com.rulersnap.measure.core.di.AppViewModelFactory
import com.rulersnap.measure.core.theme.AccentBlue
import com.rulersnap.measure.core.theme.BackgroundDeep
import com.rulersnap.measure.core.theme.BackgroundPaper
import com.rulersnap.measure.core.theme.OutlineSoft
import com.rulersnap.measure.core.theme.PrimaryOlive
import com.rulersnap.measure.core.theme.SurfaceCard
import com.rulersnap.measure.core.theme.TextMuted
import com.rulersnap.measure.core.theme.TextPrimary
import com.rulersnap.measure.core.theme.TextSecondary
import com.rulersnap.measure.core.ui.ScreenScaffold
import com.rulersnap.measure.core.ui.SecondaryButton
import com.rulersnap.measure.core.ui.SectionHeader

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aboutViewModel: AboutViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by aboutViewModel.uiState.collectAsStateWithLifecycle()

    AboutContent(
        state = state,
        onBack = onBack,
        onMenu = onMenu,
        modifier = modifier
    )
}

@Composable
private fun AboutContent(
    state: AboutUiState,
    onBack: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenScaffold(
        title = stringResource(R.string.about_title),
        subtitle = stringResource(R.string.about_subtitle),
        onBack = onBack,
        modifier = modifier,
        topOverlay = BackgroundPaper.copy(alpha = 0.90f),
        bottomOverlay = BackgroundDeep.copy(alpha = 0.78f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(88.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = stringResource(R.string.about_brand),
                        color = TextPrimary,
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = stringResource(R.string.about_version),
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SectionHeader(text = stringResource(R.string.about_section_what))
            Spacer(modifier = Modifier.height(8.dp))
            AboutCard(body = stringResource(R.string.about_what_body))

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(text = stringResource(R.string.about_section_privacy))
            Spacer(modifier = Modifier.height(8.dp))
            AboutCard(body = stringResource(R.string.about_privacy_body))

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(text = stringResource(R.string.about_section_contact))
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, OutlineSoft),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.about_contact_value),
                        color = AccentBlue,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.about_saved_total, state.savedCount),
                        color = PrimaryOlive,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            SecondaryButton(
                text = stringResource(R.string.cta_back_to_menu),
                onClick = onMenu,
                height = 50.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun AboutCard(body: String) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, OutlineSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = body,
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}
