package com.example.calmingbreath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmingbreath.R
import com.example.calmingbreath.ui.viewmodel.HeartRateInputViewModel
import kotlin.math.abs

private val ResultsTitleColor = Color(0xFF2C2C2A)
private val ResultsSubtitleColor = Color(0xFF6B6B66)
private val ResultsCircleBg = Color(0xFFCBD9C4)
private val ResultsCardBg = Color(0xFFD8E1D2)
private val ResultsAccent = Color(0xFF5E7C58)
private val ResultsDivider = Color(0xFFBFCDB9)
private val ResultsCardLabel = Color(0xFF6B7A66)
private val InfoCardBg = Color(0xFFFDFDFB)
private val ResultsSecondaryBg = Color(0xFFE6E5DF)
private val ResultsSecondaryText = Color(0xFF4A4A45)

@Composable
fun ResultsScreenView(
    viewModel: HeartRateInputViewModel,
    onNavigateHome: () -> Unit = {},
    onViewHistory: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    ResultsScreenContent(
        heartRateBefore = state.heartRateBefore,
        heartRateAfter = state.heartRateAfter,
        onNavigateHome = onNavigateHome,
        onViewHistory = onViewHistory,
    )
}

@Composable
fun ResultsScreenContent(
    heartRateBefore: Int,
    heartRateAfter: Int,
    onNavigateHome: () -> Unit = {},
    onViewHistory: () -> Unit = {},
) {
    val diff = heartRateBefore - heartRateAfter
    val percent = if (heartRateBefore != 0) abs(diff) * 100f / heartRateBefore else 0f
    val percentText = String.format("%.1f", percent)

    Scaffold(containerColor = ScreenBg) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(ResultsCircleBg, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = when {
                        diff > 0 -> Icons.AutoMirrored.Filled.TrendingDown
                        diff < 0 -> Icons.AutoMirrored.Filled.TrendingUp
                        else -> Icons.AutoMirrored.Filled.TrendingFlat
                    },
                    contentDescription = null,
                    tint = ResultsAccent,
                    modifier = Modifier.size(40.dp),
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = stringResource(
                    if (diff > 0) R.string.results_title_decrease else R.string.results_title_no_change
                ),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = ResultsTitleColor,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(
                    when {
                        diff > 0 -> R.string.results_subtitle_decrease
                        diff < 0 -> R.string.results_subtitle_increase
                        else -> R.string.results_subtitle_no_change
                    }
                ),
                fontSize = 15.sp,
                color = ResultsSubtitleColor,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(28.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ResultsCardBg, RoundedCornerShape(20.dp))
                    .padding(horizontal = 24.dp, vertical = 24.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BpmColumn(
                        label = stringResource(R.string.results_label_before),
                        bpm = heartRateBefore,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = ResultsCardLabel,
                        modifier = Modifier.size(26.dp),
                    )
                    BpmColumn(
                        label = stringResource(R.string.results_label_after),
                        bpm = heartRateAfter,
                    )
                }

                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(ResultsDivider),
                )
                Spacer(Modifier.height(16.dp))

                val sign = if (diff > 0) "−" else if (diff < 0) "+" else ""
                Text(
                    text = "$sign${abs(diff)} ${stringResource(R.string.bpm_unit)}",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = ResultsAccent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = when {
                        diff > 0 -> stringResource(R.string.results_change_decrease, percentText)
                        diff < 0 -> stringResource(R.string.results_change_increase, percentText)
                        else -> stringResource(R.string.results_change_no_change)
                    },
                    fontSize = 14.sp,
                    color = ResultsCardLabel,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(InfoCardBg, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = ResultsAccent,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.results_tip),
                    fontSize = 13.sp,
                    color = ResultsSubtitleColor,
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onNavigateHome,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                Text(
                    text = stringResource(R.string.results_back_home),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onViewHistory,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ResultsSecondaryBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                Text(
                    text = stringResource(R.string.history_title),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = ResultsSecondaryText,
                )
            }
        }
    }
}

@Composable
private fun BpmColumn(label: String, bpm: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = ResultsCardLabel,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = bpm.toString(),
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = ResultsTitleColor,
        )
        Text(
            text = stringResource(R.string.bpm_unit),
            fontSize = 12.sp,
            color = ResultsCardLabel,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResultsScreenPreview() {
    ResultsScreenContent(heartRateBefore = 120, heartRateAfter = 92)
}
