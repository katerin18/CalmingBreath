package com.example.calmingbreath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmingbreath.R
import com.example.calmingbreath.data.ExerciseSessionEntity
import com.example.calmingbreath.ui.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

private val HistoryTitleColor = Color(0xFF2C2C2A)
private val HistorySubtitleColor = Color(0xFF6B6B66)
private val HistoryCardBg = Color(0xFFFDFDFB)
private val HistoryAccentDown = Color(0xFF5E7C58)
private val HistoryAccentUp = Color(0xFFB5675F)
private val HistoryCardLabel = Color(0xFF6B7A66)

@Composable
fun HistoryScreenView(
    viewModel: HistoryViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ScreenBg)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = HistoryTitleColor,
                    )
                }
                Spacer(Modifier.size(4.dp))
                Text(
                    text = stringResource(R.string.history_title),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = HistoryTitleColor,
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(color = SageGreen)
                }

                state.items.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.history_empty),
                        fontSize = 16.sp,
                        color = HistorySubtitleColor,
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.items, key = { it.id }) { item ->
                            HistoryCard(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(item: ExerciseSessionEntity) {
    val diff = item.bpmBefore - item.bpmAfter // > 0 => пульс снизился
    val accent = if (diff >= 0) HistoryAccentDown else HistoryAccentUp
    val sign = if (diff > 0) "−" else if (diff < 0) "+" else ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HistoryCardBg, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Text(
            text = formatDate(item.startExerciseTime),
            fontSize = 13.sp,
            color = HistorySubtitleColor,
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = item.bpmBefore.toString(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = HistoryTitleColor,
            )
            Spacer(Modifier.size(10.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = HistoryCardLabel,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.size(10.dp))
            Text(
                text = item.bpmAfter.toString(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = HistoryTitleColor,
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.bpm_unit),
                fontSize = 13.sp,
                color = HistoryCardLabel,
            )
            Spacer(Modifier.size(12.dp))
            Text(
                text = "$sign${abs(diff)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = accent,
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.history_duration, formatDuration(item.exercisesDurationSec)),
            fontSize = 13.sp,
            color = HistoryCardLabel,
        )
    }
}

private fun formatDate(epochMillis: Long): String {
    return SimpleDateFormat("d MMM yyyy, HH:mm", Locale("ru")).format(Date(epochMillis))
}

private fun formatDuration(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
