package com.example.calmingbreath.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calmingbreath.R
import com.example.calmingbreath.ui.viewmodel.BreathingPhase
import com.example.calmingbreath.ui.viewmodel.ExerciseViewModel

private val ExerciseTitleColor = Color(0xFF2C2C2A)
private val ExerciseSubtitleColor = Color(0xFF6B6B66)
private val SecondaryButtonBg = Color(0xFFE6E5DF)
private val SecondaryButtonText = Color(0xFF4A4A45)

@Composable
fun ExerciseScreenView(
    viewModel: ExerciseViewModel,
    onNavigateNext: () -> Unit,
    onDiscard: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val cycles = remember { viewModel.cycles }

    var showFinishDialog by remember { mutableStateOf(false) }
    // Было ли упражнение запущено до открытия диалога — чтобы продолжить его при отмене.
    var wasRunningBeforeDialog by remember { mutableStateOf(false) }

    if (showFinishDialog) {
        val closeAndResume = {
            showFinishDialog = false
            if (wasRunningBeforeDialog) viewModel.onPause()
        }
        AlertDialog(
            onDismissRequest = closeAndResume,
            title = { Text(stringResource(R.string.finish_dialog_title)) },
            text = { Text(stringResource(R.string.finish_dialog_text)) },
            confirmButton = {
                Column(horizontalAlignment = Alignment.End) {
                    TextButton(onClick = {
                        showFinishDialog = false
                        viewModel.onFinishEarly()
                    }) {
                        Text(stringResource(R.string.finish_dialog_measure))
                    }
                    TextButton(onClick = {
                        showFinishDialog = false
                        onDiscard()
                    }) {
                        Text(stringResource(R.string.finish_dialog_discard))
                    }
                    TextButton(onClick = closeAndResume) {
                        Text(stringResource(R.string.finish_dialog_continue))
                    }
                }
            },
        )
    }

    LaunchedEffect(state.shouldNavigateNext) {
        if (state.shouldNavigateNext) {
            onNavigateNext()
            viewModel.onNavigationHandled()
        }
    }

    // Анимация круга дыхания
    val scale = remember { Animatable(0.3f) }

    LaunchedEffect(state.currentCycleIndex, state.currentPhase, state.isPaused) {
        if (state.isPaused || state.isFinished) return@LaunchedEffect
        val cycle = cycles[state.currentCycleIndex]
        when (state.currentPhase) {
            BreathingPhase.INHALE -> {
                scale.snapTo(0.3f)
                scale.animateTo(1f, tween(cycle.inhaleSeconds * 1000))
            }
            BreathingPhase.EXHALE -> {
                scale.snapTo(1f)
                scale.animateTo(0.3f, tween(cycle.exhaleSeconds * 1000))
            }
            BreathingPhase.HOLD -> Unit
        }
    }

    val currentCycle = cycles[state.currentCycleIndex]
    val phaseText = when (state.currentPhase) {
        BreathingPhase.INHALE -> stringResource(R.string.phase_inhale)
        BreathingPhase.EXHALE -> stringResource(R.string.phase_exhale)
        BreathingPhase.HOLD -> stringResource(R.string.phase_hold)
    }

    Scaffold(containerColor = ScreenBg) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.exercise_title),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = ExerciseTitleColor,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.exercise_stage, currentCycle.stage),
                fontSize = 16.sp,
                color = ExerciseSubtitleColor
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(
                    R.string.exercise_cycle_info,
                    currentCycle.inhaleSeconds,
                    currentCycle.exhaleSeconds,
                    currentCycle.holdSeconds
                ),
                fontSize = 14.sp,
                color = ExerciseSubtitleColor
            )

            Spacer(Modifier.height(40.dp))

            // Анимированный круг
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = SageGreen,
                        radius = (size.minDimension / 2f) * scale.value,
                        alpha = 0.85f
                    )
                }
                Text(
                    text = phaseText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.seconds_short, state.secondsRemaining),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = ExerciseTitleColor
            )

            Spacer(Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = viewModel::onRestart,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryButtonBg),
                    modifier = Modifier.height(54.dp)
                ) {
                    Icon(Icons.Default.Replay, contentDescription = null, tint = SecondaryButtonText)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        stringResource(R.string.restart),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = SecondaryButtonText
                    )
                }

                Spacer(Modifier.width(16.dp))

                Button(
                    onClick = viewModel::onPause,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                    modifier = Modifier.height(54.dp)
                ) {
                    Icon(
                        imageVector = if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (state.isPaused) stringResource(R.string.start) else stringResource(R.string.pause),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    // Ставим на паузу, пока пользователь решает, что делать.
                    wasRunningBeforeDialog = !state.isPaused
                    if (wasRunningBeforeDialog) viewModel.onPause()
                    showFinishDialog = true
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryButtonBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = stringResource(R.string.finish_early),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = SecondaryButtonText
                )
            }
        }
    }
}
