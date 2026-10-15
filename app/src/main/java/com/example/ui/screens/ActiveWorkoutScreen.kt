package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActiveExerciseState
import com.example.data.model.WorkoutSessionDay
import com.example.ui.theme.FormaDarkBg
import com.example.ui.theme.FormaDarkBorder
import com.example.ui.theme.FormaDarkCard
import com.example.ui.theme.FormaDarkCardElevated
import com.example.ui.theme.FormaEmerald
import com.example.ui.theme.FormaTextMuted
import com.example.ui.theme.FormaTextPrimary
import com.example.ui.theme.FormaTextSecondary
import kotlinx.coroutines.delay

@Composable
fun ActiveWorkoutScreen(
    sessionDay: WorkoutSessionDay?,
    exercises: List<ActiveExerciseState>,
    workoutStartTime: Long,
    restSecondsRemaining: Int,
    restTimerTotal: Int,
    isTimerRunning: Boolean,
    notes: String,
    onUpdateSet: (exerciseIndex: Int, setIndex: Int, weight: Double, reps: Int, isCompleted: Boolean) -> Unit,
    onStartRestTimer: (Int) -> Unit,
    onAddRestTime: (Int) -> Unit,
    onSkipRestTimer: () -> Unit,
    onNotesChange: (String) -> Unit,
    onFinishWorkout: () -> Unit,
    onCancelWorkout: () -> Unit
) {
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var showFinishConfirmDialog by remember { mutableStateOf(false) }

    // Live workout elapsed clock
    LaunchedEffect(workoutStartTime) {
        while (true) {
            val now = System.currentTimeMillis()
            elapsedSeconds = maxOf(0, ((now - workoutStartTime) / 1000).toInt())
            delay(1000)
        }
    }

    val elapsedMinutes = elapsedSeconds / 60
    val elapsedRemainderSeconds = elapsedSeconds % 60
    val formattedTime = "%02d:%02d".format(elapsedMinutes, elapsedRemainderSeconds)

    if (showFinishConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showFinishConfirmDialog = false },
            title = {
                Text(
                    text = "Finalizar Treino?",
                    color = FormaTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Suas cargas e repetições concluídas serão gravadas no histórico e computadas na sua evolução semanal.",
                    color = FormaTextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFinishConfirmDialog = false
                        onFinishWorkout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FormaEmerald,
                        contentColor = FormaDarkBg
                    )
                ) {
                    Text("Salvar e Finalizar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showFinishConfirmDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FormaDarkCard,
                        contentColor = FormaTextSecondary
                    )
                ) {
                    Text("Continuar Treinando")
                }
            },
            containerColor = FormaDarkCardElevated
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FormaDarkBg)
    ) {
        // Sticky Header with Session Title and Live Timer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FormaDarkCardElevated)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCancelWorkout, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Cancelar", tint = FormaTextSecondary)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = sessionDay?.title ?: "Treino em Andamento",
                        color = FormaTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "Tempo decorrido: $formattedTime",
                        color = FormaEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Button(
                onClick = { showFinishConfirmDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FormaEmerald,
                    contentColor = FormaDarkBg
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(38.dp)
                    .testTag("finish_workout_button")
            ) {
                Text("Concluir", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        // Rest Timer Floating Banner
        AnimatedVisibility(
            visible = isTimerRunning && restSecondsRemaining > 0,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = FormaDarkCardElevated),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, FormaEmerald)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(contentAlignment = Alignment.Center) {
                            val progress = if (restTimerTotal > 0) restSecondsRemaining.toFloat() / restTimerTotal else 0f
                            CircularProgressIndicator(
                                progress = { progress },
                                color = FormaEmerald,
                                trackColor = FormaDarkBorder,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(38.dp)
                            )
                            Text(
                                text = "$restSecondsRemaining",
                                color = FormaTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Descanso Ativo",
                                color = FormaEmerald,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Recupere o fôlego para a próxima série",
                                color = FormaTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row {
                        Button(
                            onClick = { onAddRestTime(15) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FormaDarkCard,
                                contentColor = FormaEmerald
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("+15s", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = onSkipRestTimer,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FormaDarkCard,
                                contentColor = FormaTextSecondary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Pular", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Exercises List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
            }

            itemsIndexed(exercises) { exerciseIndex, exState ->
                ActiveExerciseCard(
                    exerciseIndex = exerciseIndex,
                    exerciseState = exState,
                    onUpdateSet = onUpdateSet,
                    onTriggerRest = { onStartRestTimer(exState.exercise.restSeconds) }
                )
            }

            item {
                // Workout notes
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Anotações da Sessão",
                    color = FormaTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    placeholder = { Text("Ex: senti o tríceps queimar bastante, próxima vez aumentar 2kg no supino...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FormaEmerald,
                        unfocusedBorderColor = FormaDarkBorder,
                        focusedTextColor = FormaTextPrimary,
                        unfocusedTextColor = FormaTextPrimary,
                        focusedContainerColor = FormaDarkCard,
                        unfocusedContainerColor = FormaDarkCard
                    ),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ActiveExerciseCard(
    exerciseIndex: Int,
    exerciseState: ActiveExerciseState,
    onUpdateSet: (exerciseIndex: Int, setIndex: Int, weight: Double, reps: Int, isCompleted: Boolean) -> Unit,
    onTriggerRest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FormaDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Exercise Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exerciseState.exercise.name,
                        color = FormaTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${exerciseState.exercise.muscleGroup} • Alvo: ${exerciseState.exercise.reps} reps (${exerciseState.exercise.restSeconds}s descanso)",
                        color = FormaTextMuted,
                        fontSize = 11.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(FormaDarkCardElevated)
                        .clickable { onTriggerRest() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = FormaEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${exerciseState.exercise.restSeconds}s",
                            color = FormaEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sets Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "SÉRIE", color = FormaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(42.dp))
                Text(text = "CARGA (KG)", color = FormaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = "REPETIÇÕES", color = FormaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = "FEITO", color = FormaTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(46.dp), textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Set Rows
            exerciseState.sets.forEachIndexed { setIndex, setRecord ->
                SetRowItem(
                    setNumber = setRecord.setNumber,
                    weight = setRecord.weightKg,
                    reps = setRecord.repsDone,
                    isCompleted = setRecord.isCompleted,
                    onUpdate = { w, r, done ->
                        onUpdateSet(exerciseIndex, setIndex, w, r, done)
                    }
                )
            }
        }
    }
}

@Composable
fun SetRowItem(
    setNumber: Int,
    weight: Double,
    reps: Int,
    isCompleted: Boolean,
    onUpdate: (weight: Double, reps: Int, isCompleted: Boolean) -> Unit
) {
    var weightText by remember(weight) { mutableStateOf(if (weight == 0.0) "" else if (weight % 1.0 == 0.0) weight.toInt().toString() else weight.toString()) }
    var repsText by remember(reps) { mutableStateOf(if (reps == 0) "10" else reps.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isCompleted) FormaEmerald.copy(alpha = 0.08f) else FormaDarkCardElevated)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set number badge
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$setNumber",
                color = if (isCompleted) FormaEmerald else FormaTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Weight Input
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        ) {
            OutlinedTextField(
                value = weightText,
                onValueChange = {
                    weightText = it
                    val parsed = it.toDoubleOrNull() ?: 0.0
                    onUpdate(parsed, repsText.toIntOrNull() ?: 10, isCompleted)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = FormaTextPrimary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FormaEmerald,
                    unfocusedBorderColor = FormaDarkBorder,
                    focusedContainerColor = FormaDarkCard,
                    unfocusedContainerColor = FormaDarkCard
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        // Reps Input
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        ) {
            OutlinedTextField(
                value = repsText,
                onValueChange = {
                    repsText = it
                    val parsed = it.toIntOrNull() ?: 0
                    onUpdate(weightText.toDoubleOrNull() ?: 0.0, parsed, isCompleted)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = FormaTextPrimary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FormaEmerald,
                    unfocusedBorderColor = FormaDarkBorder,
                    focusedContainerColor = FormaDarkCard,
                    unfocusedContainerColor = FormaDarkCard
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        // Completed Checkbox
        Box(
            modifier = Modifier
                .width(44.dp)
                .height(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { checked ->
                    val w = weightText.toDoubleOrNull() ?: 0.0
                    val r = repsText.toIntOrNull() ?: 10
                    onUpdate(w, r, checked)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = FormaEmerald,
                    checkmarkColor = FormaDarkBg,
                    uncheckedColor = FormaDarkBorder
                )
            )
        }
    }
}
