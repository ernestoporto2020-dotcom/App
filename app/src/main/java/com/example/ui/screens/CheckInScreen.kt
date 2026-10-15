package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CheckInSubmission
import com.example.ui.theme.FormaDarkBg
import com.example.ui.theme.FormaDarkBorder
import com.example.ui.theme.FormaDarkCard
import com.example.ui.theme.FormaDarkCardElevated
import com.example.ui.theme.FormaEmerald
import com.example.ui.theme.FormaTextMuted
import com.example.ui.theme.FormaTextPrimary
import com.example.ui.theme.FormaTextSecondary

@Composable
fun CheckInScreen(
    plannedDays: Int,
    isSubmitting: Boolean,
    lastFeedback: Pair<String, String>?,
    onSubmitCheckIn: (CheckInSubmission) -> Unit,
    onResetFeedback: () -> Unit,
    onBack: () -> Unit
) {
    var completedDays by remember { mutableIntStateOf(plannedDays) }
    var fatigueLevel by remember { mutableFloatStateOf(4f) }
    var sorenessRating by remember { mutableFloatStateOf(3f) }
    var rpeRating by remember { mutableFloatStateOf(7f) }
    var userNotes by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FormaDarkBg)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = FormaTextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Check-in Semanal",
                    color = FormaTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ajuste fino de volume e fadiga com o Coach IA",
                    color = FormaTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (lastFeedback != null) {
                // Display Feedback Result
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FormaDarkCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, FormaEmerald)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = FormaEmerald, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Diagnóstico do Coach FormaAI",
                                color = FormaTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = lastFeedback.first,
                            color = FormaTextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(FormaDarkCard)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Recomendação para a próxima semana:",
                                    color = FormaEmerald,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = lastFeedback.second,
                                    color = FormaTextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onResetFeedback,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FormaEmerald,
                                contentColor = FormaDarkBg
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Confirmar e Aplicar ao Meu Plano", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Text(
                    text = "Como foi sua semana de treinos?",
                    color = FormaTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // 1. Treinos feitos
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = FormaDarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Quantos treinos você completou nesta semana?",
                            color = FormaTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$completedDays de $plannedDays treinos planejados",
                            color = FormaEmerald,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Slider(
                            value = completedDays.toFloat(),
                            onValueChange = { completedDays = it.toInt() },
                            valueRange = 0f..7f,
                            steps = 6,
                            colors = SliderDefaults.colors(
                                thumbColor = FormaEmerald,
                                activeTrackColor = FormaEmerald,
                                inactiveTrackColor = FormaDarkBorder
                            )
                        )
                    }
                }

                // 2. Fadiga
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = FormaDarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Nível de Fadiga Geral acumulada (1 a 10):",
                            color = FormaTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${fatigueLevel.toInt()} / 10 - ${if (fatigueLevel < 4) "Descansado e disposto" else if (fatigueLevel < 8) "Cansaço moderado normal" else "Fadiga muito elevada"}",
                            color = FormaEmerald,
                            fontSize = 12.sp
                        )
                        Slider(
                            value = fatigueLevel,
                            onValueChange = { fatigueLevel = it },
                            valueRange = 1f..10f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = FormaEmerald,
                                activeTrackColor = FormaEmerald,
                                inactiveTrackColor = FormaDarkBorder
                            )
                        )
                    }
                }

                // 3. Dor Muscular
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = FormaDarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Dor Muscular Tardia / Articular (1 a 10):",
                            color = FormaTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${sorenessRating.toInt()} / 10 - ${if (sorenessRating < 4) "Músculos recuperados" else if (sorenessRating < 8) "Dor muscular normal" else "Dor aguda/desconforto alto"}",
                            color = FormaEmerald,
                            fontSize = 12.sp
                        )
                        Slider(
                            value = sorenessRating,
                            onValueChange = { sorenessRating = it },
                            valueRange = 1f..10f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = FormaEmerald,
                                activeTrackColor = FormaEmerald,
                                inactiveTrackColor = FormaDarkBorder
                            )
                        )
                    }
                }

                // 4. Percepção de esforço (RPE)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = FormaDarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Percepção de Esforço RPE Médio (1 a 10):",
                            color = FormaTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${rpeRating.toInt()} / 10 - ${if (rpeRating <= 6) "Treinos leves/moderados" else if (rpeRating <= 8) "Treinos intensos com 1-2 reps na reserva" else "Próximo à falha concêntrica"}",
                            color = FormaEmerald,
                            fontSize = 12.sp
                        )
                        Slider(
                            value = rpeRating,
                            onValueChange = { rpeRating = it },
                            valueRange = 1f..10f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = FormaEmerald,
                                activeTrackColor = FormaEmerald,
                                inactiveTrackColor = FormaDarkBorder
                            )
                        )
                    }
                }

                // Observações
                OutlinedTextField(
                    value = userNotes,
                    onValueChange = { userNotes = it },
                    label = { Text("Comentários adicionais para a IA") },
                    placeholder = { Text("Ex: senti o joelho incomodar no leg press, preferi reduzir carga...") },
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

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val submission = CheckInSubmission(
                            consistencyDaysDone = completedDays,
                            plannedDays = plannedDays,
                            fatigueLevel = fatigueLevel.toInt(),
                            sorenessRating = sorenessRating.toInt(),
                            perceivedEffortRpe = rpeRating.toInt(),
                            userNotes = userNotes
                        )
                        onSubmitCheckIn(submission)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_checkin_button"),
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FormaEmerald,
                        contentColor = FormaDarkBg
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = FormaDarkBg, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analisando com Coach IA...", fontWeight = FontWeight.Bold)
                    } else {
                        Text("Enviar Check-in para o Coach IA", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
