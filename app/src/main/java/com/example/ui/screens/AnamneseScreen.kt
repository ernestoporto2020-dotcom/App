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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnamneseData
import com.example.data.model.ExperienceLevel
import com.example.data.model.PrimaryGoal
import com.example.data.model.WorkoutLocation
import com.example.ui.theme.FormaAlertAmber
import com.example.ui.theme.FormaAlertRed
import com.example.ui.theme.FormaDarkBg
import com.example.ui.theme.FormaDarkBorder
import com.example.ui.theme.FormaDarkCard
import com.example.ui.theme.FormaDarkCardElevated
import com.example.ui.theme.FormaEmerald
import com.example.ui.theme.FormaTextMuted
import com.example.ui.theme.FormaTextPrimary
import com.example.ui.theme.FormaTextSecondary

@Composable
fun AnamneseScreen(
    currentStep: Int,
    anamnese: AnamneseData,
    isGenerating: Boolean,
    safetyBlockedMessage: String?,
    onUpdateAnamnese: (AnamneseData.() -> AnamneseData) -> Unit,
    onNextStep: () -> Unit,
    onPrevStep: () -> Unit,
    onDismissSafety: () -> Unit
) {
    val scrollState = rememberScrollState()

    if (safetyBlockedMessage != null) {
        AlertDialog(
            onDismissRequest = onDismissSafety,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = FormaAlertRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aviso de Segurança Clínica",
                        color = FormaTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = safetyBlockedMessage,
                    color = FormaTextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Allow user to revise their answer to no pain or consult
                        onUpdateAnamnese { copy(hasSharpPainOrInjury = false) }
                        onDismissSafety()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FormaEmerald, contentColor = FormaDarkBg)
                ) {
                    Text("Revisar Resposta")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismissSafety,
                    colors = ButtonDefaults.buttonColors(containerColor = FormaDarkCard, contentColor = FormaTextSecondary)
                ) {
                    Text("Entendi")
                }
            },
            containerColor = FormaDarkCardElevated
        )
    }

    if (isGenerating) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FormaDarkBg),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(28.dp)
            ) {
                CircularProgressIndicator(
                    color = FormaEmerald,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "A IA está estruturando sua ficha...",
                    color = FormaTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Calculando volume semanal, faixas de repetições, intervalos de descanso e exercícios compatíveis com seu perfil.",
                    color = FormaTextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FormaDarkBg)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPrevStep,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = FormaTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ANAMNESE FORMAAI",
                    color = FormaEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Etapa $currentStep de 5",
                    color = FormaTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress bar
        LinearProgressIndicator(
            progress = { currentStep / 5f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape),
            color = FormaEmerald,
            trackColor = FormaDarkBorder
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Step Content inside scrollable column
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            when (currentStep) {
                1 -> StepOneGoals(anamnese, onUpdateAnamnese)
                2 -> StepTwoExperienceAndLocation(anamnese, onUpdateAnamnese)
                3 -> StepThreeFrequencyAndDuration(anamnese, onUpdateAnamnese)
                4 -> StepFourHealthAndSafety(anamnese, onUpdateAnamnese)
                5 -> StepFivePreferencesAndHurdles(anamnese, onUpdateAnamnese)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 1) {
                Button(
                    onClick = onPrevStep,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FormaDarkCard,
                        contentColor = FormaTextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .weight(1f)
                ) {
                    Text("Anterior")
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Button(
                onClick = onNextStep,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FormaEmerald,
                    contentColor = FormaDarkBg
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(50.dp)
                    .weight(if (currentStep > 1) 1.5f else 1f)
                    .testTag("anamnese_next_button")
            ) {
                Text(
                    text = if (currentStep == 5) "Gerar Minha Ficha" else "Continuar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun StepOneGoals(
    anamnese: AnamneseData,
    onUpdate: (AnamneseData.() -> AnamneseData) -> Unit
) {
    Text(
        text = "Qual é o seu objetivo principal?",
        color = FormaTextPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Personalizamos a faixa de repetições e o volume para o seu alvo.",
        color = FormaTextSecondary,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
    )

    PrimaryGoal.values().forEach { goal ->
        val isSelected = anamnese.primaryGoal == goal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) FormaEmerald.copy(alpha = 0.12f) else FormaDarkCard)
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) FormaEmerald else FormaDarkBorder,
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable { onUpdate { copy(primaryGoal = goal) } }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.label,
                        color = if (isSelected) FormaEmerald else FormaTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = goal.description,
                        color = FormaTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(FormaEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = FormaDarkBg,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Objetivos Secundários (Opcional):",
        color = FormaTextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(8.dp))

    val secondaryOptions = listOf(
        "Melhorar postura",
        "Aumentar mobilidade articular",
        "Saúde cardiovascular",
        "Reduzir estresse e ansiedade",
        "Densidade óssea"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column {
            secondaryOptions.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { option ->
                        val isChecked = anamnese.secondaryGoals.contains(option)
                        FilterChip(
                            selected = isChecked,
                            onClick = {
                                onUpdate {
                                    val list = secondaryGoals.toMutableList()
                                    if (isChecked) list.remove(option) else list.add(option)
                                    copy(secondaryGoals = list)
                                }
                            },
                            label = { Text(option, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FormaEmerald.copy(alpha = 0.2f),
                                selectedLabelColor = FormaEmerald,
                                containerColor = FormaDarkCard,
                                labelColor = FormaTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isChecked,
                                borderColor = if (isChecked) FormaEmerald else FormaDarkBorder
                            ),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepTwoExperienceAndLocation(
    anamnese: AnamneseData,
    onUpdate: (AnamneseData.() -> AnamneseData) -> Unit
) {
    Text(
        text = "Qual é o seu nível de experiência?",
        color = FormaTextPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Usado para calibrar complexidade motora e seleção de exercícios.",
        color = FormaTextSecondary,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
    )

    ExperienceLevel.values().forEach { level ->
        val isSelected = anamnese.experienceLevel == level
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) FormaEmerald.copy(alpha = 0.12f) else FormaDarkCard)
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) FormaEmerald else FormaDarkBorder,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onUpdate { copy(experienceLevel = level) } }
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = level.label,
                    color = if (isSelected) FormaEmerald else FormaTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = level.description,
                    color = FormaTextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "Onde você vai treinar?",
        color = FormaTextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(10.dp))

    WorkoutLocation.values().forEach { loc ->
        val isSelected = anamnese.location == loc
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) FormaEmerald.copy(alpha = 0.12f) else FormaDarkCard)
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) FormaEmerald else FormaDarkBorder,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onUpdate { copy(location = loc) } }
                .padding(14.dp)
        ) {
            Text(
                text = loc.label,
                color = if (isSelected) FormaEmerald else FormaTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun StepThreeFrequencyAndDuration(
    anamnese: AnamneseData,
    onUpdate: (AnamneseData.() -> AnamneseData) -> Unit
) {
    Text(
        text = "Frequência e Tempo Disponível",
        color = FormaTextPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Ajustamos a divisão semanal (Full Body, Upper/Lower ou ABC) à sua rotina real.",
        color = FormaTextSecondary,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
    )

    Text(
        text = "Quantos dias por semana você vai à academia?",
        color = FormaTextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(2, 3, 4, 5, 6).forEach { days ->
            val isSelected = anamnese.daysPerWeek == days
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) FormaEmerald else FormaDarkCard)
                    .border(1.dp, if (isSelected) FormaEmerald else FormaDarkBorder, RoundedCornerShape(12.dp))
                    .clickable { onUpdate { copy(daysPerWeek = days) } }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$days",
                        color = if (isSelected) FormaDarkBg else FormaTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "dias",
                        color = if (isSelected) FormaDarkBg else FormaTextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(28.dp))

    Text(
        text = "Qual a duração média de cada sessão?",
        color = FormaTextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(30, 45, 60, 75, 90).forEach { mins ->
            val isSelected = anamnese.sessionDurationMinutes == mins
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) FormaEmerald else FormaDarkCard)
                    .border(1.dp, if (isSelected) FormaEmerald else FormaDarkBorder, RoundedCornerShape(12.dp))
                    .clickable { onUpdate { copy(sessionDurationMinutes = mins) } }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$mins",
                        color = if (isSelected) FormaDarkBg else FormaTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "min",
                        color = if (isSelected) FormaDarkBg else FormaTextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StepFourHealthAndSafety(
    anamnese: AnamneseData,
    onUpdate: (AnamneseData.() -> AnamneseData) -> Unit
) {
    Text(
        text = "Saúde e Limitações Físicas",
        color = FormaTextPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Foco em longevidade: não prescrevemos exercícios que agridam suas articulações.",
        color = FormaTextSecondary,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
    )

    // Acute pain switch
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (anamnese.hasSharpPainOrInjury) FormaAlertRed.copy(alpha = 0.15f) else FormaDarkCard)
            .border(
                1.dp,
                if (anamnese.hasSharpPainOrInjury) FormaAlertRed else FormaDarkBorder,
                RoundedCornerShape(14.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Possui dor aguda ou lesão recente ativa?",
                    color = if (anamnese.hasSharpPainOrInjury) FormaAlertRed else FormaTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ex: tendinite aguda, hérnia com irradiação ativa ou trauma não liberado por médico.",
                    color = FormaTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            Switch(
                checked = anamnese.hasSharpPainOrInjury,
                onCheckedChange = { onUpdate { copy(hasSharpPainOrInjury = it) } },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = FormaDarkBg,
                    checkedTrackColor = FormaAlertRed,
                    uncheckedThumbColor = FormaTextMuted,
                    uncheckedTrackColor = FormaDarkCardElevated
                )
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Descreva limitações conhecidas (Opcional):",
        color = FormaTextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(6.dp))

    OutlinedTextField(
        value = anamnese.physicalLimitations,
        onValueChange = { onUpdate { copy(physicalLimitations = it) } },
        placeholder = { Text("Ex: condromalácia patelar grau 1, incômodo no manguito rotador direito em ângulos fechados...") },
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
        minLines = 3
    )
}

@Composable
private fun StepFivePreferencesAndHurdles(
    anamnese: AnamneseData,
    onUpdate: (AnamneseData.() -> AnamneseData) -> Unit
) {
    Text(
        text = "Preferências e Obstáculos",
        color = FormaTextPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Último passo! Adequamos a ficha ao que você gosta e ao seu dia a dia.",
        color = FormaTextSecondary,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
    )

    Text(
        text = "Exercícios ou aparelhos que deseja evitar:",
        color = FormaTextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(6.dp))

    OutlinedTextField(
        value = anamnese.exercisesToAvoid,
        onValueChange = { onUpdate { copy(exercisesToAvoid = it) } },
        placeholder = { Text("Ex: evitar agachamento livre com barra, sem exercícios de impacto, evitar barra fixa...") },
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

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Qual sua maior dificuldade para manter consistência?",
        color = FormaTextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(6.dp))

    val hurdles = listOf(
        "Falta de tempo durante a semana",
        "Cansaço após o expediente de trabalho",
        "Dúvidas sobre a execução correta",
        "Falta de motivação quando treino sozinho"
    )

    hurdles.forEach { hurdle ->
        val isSelected = anamnese.mainHurdles == hurdle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) FormaEmerald.copy(alpha = 0.12f) else FormaDarkCard)
                .border(
                    1.dp,
                    if (isSelected) FormaEmerald else FormaDarkBorder,
                    RoundedCornerShape(12.dp)
                )
                .clickable { onUpdate { copy(mainHurdles = hurdle) } }
                .padding(12.dp)
        ) {
            Text(
                text = hurdle,
                color = if (isSelected) FormaEmerald else FormaTextPrimary,
                fontSize = 13.sp
            )
        }
    }
}
