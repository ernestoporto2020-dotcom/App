package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProfileEntity
import com.example.data.model.AnamneseData
import com.example.data.model.WorkoutPlan
import com.example.data.model.WorkoutSessionDay
import com.example.ui.theme.FormaDarkBg
import com.example.ui.theme.FormaDarkBorder
import com.example.ui.theme.FormaDarkCard
import com.example.ui.theme.FormaDarkCardElevated
import com.example.ui.theme.FormaEmerald
import com.example.ui.theme.FormaTextMuted
import com.example.ui.theme.FormaTextPrimary
import com.example.ui.theme.FormaTextSecondary

@Composable
fun DashboardScreen(
    profile: UserProfileEntity?,
    anamnese: AnamneseData?,
    plan: WorkoutPlan?,
    totalSessions: Int,
    totalVolumeKg: Double,
    onStartWorkout: (WorkoutSessionDay) -> Unit,
    onViewWorkoutSheet: () -> Unit,
    onOpenCoachChat: () -> Unit,
    onOpenCheckIn: () -> Unit,
    onOpenHistory: () -> Unit
) {
    val scrollState = rememberScrollState()
    val athleteName = profile?.name?.ifBlank { "Atleta" } ?: "Atleta"

    // Select current day's workout (modulo calculation or first day)
    val todayWorkout: WorkoutSessionDay? = plan?.days?.let { days ->
        if (days.isNotEmpty()) {
            val index = totalSessions % days.size
            days[index]
        } else null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FormaDarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BOM TREINO,",
                    color = FormaEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = athleteName,
                    color = FormaTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(FormaDarkCard)
                    .border(1.dp, FormaDarkBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = FormaEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$totalSessions sessões",
                        color = FormaTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Featured Card: Treino do Dia
        if (todayWorkout != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("workout_of_day_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FormaDarkCardElevated),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, FormaEmerald.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(FormaEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "SESSÃO PROGRAMADA",
                                color = FormaEmerald,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "${todayWorkout.exercises.size} exercícios",
                            color = FormaTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = todayWorkout.title,
                        color = FormaTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Grupos: ${todayWorkout.muscleGroups.joinToString(" • ")}",
                        color = FormaTextSecondary,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { onStartWorkout(todayWorkout) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("start_workout_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FormaEmerald,
                            contentColor = FormaDarkBg
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Iniciar Sessão de Treino",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Resumo Semanal / Estatísticas
        Text(
            text = "PROGRESSO & CONSISTÊNCIA",
            color = FormaTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Volume total
            DashboardMetricCard(
                modifier = Modifier.weight(1f),
                title = "Volume Total",
                value = if (totalVolumeKg > 1000) "%.1f t".format(totalVolumeKg / 1000) else "%.0f kg".format(totalVolumeKg),
                subtitle = "Carga acumulada",
                icon = Icons.Default.TrendingUp
            )

            // Frequência
            val targetDays = anamnese?.daysPerWeek ?: 4
            DashboardMetricCard(
                modifier = Modifier.weight(1f),
                title = "Meta Semanal",
                value = "$targetDays dias",
                subtitle = "${plan?.weeklySplit?.take(16) ?: "Ativo"}",
                icon = Icons.Default.DateRange
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Actions
        Text(
            text = "ACESSO RÁPIDO",
            color = FormaTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        QuickActionRow(
            title = "Minha Ficha Completa",
            subtitle = "Consulte todos os dias, séries e exercícios alternativos",
            icon = Icons.Default.FitnessCenter,
            onClick = onViewWorkoutSheet
        )

        Spacer(modifier = Modifier.height(8.dp))

        QuickActionRow(
            title = "Coach FormaAI (Assistente Gemini)",
            subtitle = "Tire dúvidas sobre cargas passadas ou peça orientações",
            icon = Icons.Default.Psychology,
            onClick = onOpenCoachChat
        )

        Spacer(modifier = Modifier.height(8.dp))

        QuickActionRow(
            title = "Check-in Semanal",
            subtitle = "Avalie fadiga, dores e receba ajuste inteligente de volume",
            icon = Icons.Default.DateRange,
            onClick = onOpenCheckIn
        )

        Spacer(modifier = Modifier.height(8.dp))

        QuickActionRow(
            title = "Histórico de Cargas",
            subtitle = "Acompanhe evolução de cargas e sessões finalizadas",
            icon = Icons.Default.History,
            onClick = onOpenHistory
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DashboardMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FormaDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FormaEmerald,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = FormaTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = FormaTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = FormaTextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun QuickActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FormaDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(FormaEmerald.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = FormaEmerald,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = FormaTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = FormaTextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
