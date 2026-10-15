package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ExerciseLoadHistoryEntity
import com.example.data.local.WorkoutLogEntity
import com.example.ui.theme.FormaDarkBg
import com.example.ui.theme.FormaDarkBorder
import com.example.ui.theme.FormaDarkCard
import com.example.ui.theme.FormaDarkCardElevated
import com.example.ui.theme.FormaEmerald
import com.example.ui.theme.FormaTextMuted
import com.example.ui.theme.FormaTextPrimary
import com.example.ui.theme.FormaTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    logs: List<WorkoutLogEntity>,
    recentLoads: List<ExerciseLoadHistoryEntity>,
    totalSessions: Int,
    totalVolumeKg: Double,
    onBack: () -> Unit
) {
    // Unique exercises with logged loads
    val exercisesWithLoads = remember(recentLoads) {
        recentLoads.map { it.exerciseName }.distinct()
    }
    var selectedExercise by remember(exercisesWithLoads) {
        mutableStateOf(exercisesWithLoads.firstOrNull() ?: "")
    }

    val selectedExerciseLoads = remember(recentLoads, selectedExercise) {
        recentLoads.filter { it.exerciseName == selectedExercise }
    }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")) }

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
                    text = "Histórico & Evolução",
                    color = FormaTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$totalSessions sessões registradas",
                    color = FormaTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                // Overview metrics banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = FormaDarkCard),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Total Levantado", color = FormaTextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (totalVolumeKg > 1000) "%.1f t".format(totalVolumeKg / 1000) else "%.0f kg".format(totalVolumeKg),
                                color = FormaEmerald,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = FormaDarkCard),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Sessões Concluídas", color = FormaTextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalSessions",
                                color = FormaTextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Load Evolution Section
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShowChart, contentDescription = null, tint = FormaEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "EVOLUÇÃO DE CARGA REAL",
                        color = FormaTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (exercisesWithLoads.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(exercisesWithLoads) { exName ->
                            val isSelected = selectedExercise == exName
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) FormaEmerald else FormaDarkCard)
                                    .border(1.dp, if (isSelected) FormaEmerald else FormaDarkBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedExercise = exName }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = exName,
                                    color = if (isSelected) FormaDarkBg else FormaTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Real progression visualizer
                    LoadProgressionChartCard(
                        exerciseName = selectedExercise,
                        history = selectedExerciseLoads
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = FormaDarkCard),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Sem dados de carga ainda",
                                color = FormaTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Complete sua primeira sessão para que o gráfico de sobrecarga progressiva seja traçado com suas marcas reais.",
                                color = FormaTextSecondary,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Sessions History List
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = FormaEmerald, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SESSÕES ANTERIORES",
                        color = FormaTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            if (logs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhuma sessão finalizada até o momento.",
                            color = FormaTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(logs) { log ->
                    WorkoutLogCard(log = log, formattedDate = dateFormat.format(Date(log.timestamp)))
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun LoadProgressionChartCard(
    exerciseName: String,
    history: List<ExerciseLoadHistoryEntity>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FormaDarkCardElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exerciseName,
                    color = FormaTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                val lastRecord = history.lastOrNull()
                if (lastRecord != null) {
                    Text(
                        text = "Última: ${lastRecord.weightKg}kg • 1RM est: ${lastRecord.estimatedOneRm.toInt()}kg",
                        color = FormaEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Line Chart
            val weights = history.map { it.weightKg }
            if (weights.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val maxWeight = maxOf(weights.maxOrNull() ?: 1.0, 1.0)
                        val minWeight = minOf(weights.minOrNull() ?: 0.0, maxWeight * 0.7)
                        val range = if (maxWeight > minWeight) maxWeight - minWeight else 1.0

                        val w = size.width
                        val h = size.height

                        // Horizontal guide line
                        drawLine(
                            color = Color(0xFF26334D),
                            start = Offset(0f, h / 2),
                            end = Offset(w, h / 2),
                            strokeWidth = 1f
                        )

                        if (weights.size == 1) {
                            // Single point
                            drawCircle(
                                color = Color(0xFF00E599),
                                radius = 8f,
                                center = Offset(w / 2, h / 2)
                            )
                        } else {
                            val path = Path()
                            val stepX = w / (weights.size - 1)

                            weights.forEachIndexed { i, weight ->
                                val x = i * stepX
                                val normalizedY = ((maxWeight - weight) / range).toFloat()
                                val y = 16f + (normalizedY * (h - 32f))

                                if (i == 0) {
                                    path.moveTo(x, y)
                                } else {
                                    path.lineTo(x, y)
                                }
                                drawCircle(
                                    color = Color(0xFF00E599),
                                    radius = 6f,
                                    center = Offset(x, y)
                                )
                            }

                            drawPath(
                                path = path,
                                color = Color(0xFF00E599),
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutLogCard(log: WorkoutLogEntity, formattedDate: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FormaDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.sessionTitle,
                    color = FormaTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formattedDate,
                    color = FormaTextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = FormaEmerald, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${log.durationMinutes} min", color = FormaTextSecondary, fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = FormaEmerald, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "%.0f kg volume".format(log.totalVolumeKg), color = FormaTextSecondary, fontSize = 12.sp)
                }
            }

            if (log.exercisesSummaryJson.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = log.exercisesSummaryJson,
                    color = FormaTextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            if (log.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Obs: \"${log.notes}\"",
                    color = FormaEmerald,
                    fontSize = 11.sp
                )
            }
        }
    }
}
