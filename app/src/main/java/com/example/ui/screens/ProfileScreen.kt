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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.local.UserProfileEntity
import com.example.data.model.AnamneseData
import com.example.data.model.WorkoutPlan
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
fun ProfileScreen(
    profile: UserProfileEntity?,
    anamnese: AnamneseData?,
    plan: WorkoutPlan?,
    onRedoAnamnese: () -> Unit,
    onResetAccount: () -> Unit,
    onBack: () -> Unit
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = "Excluir Conta e Dados?",
                    color = FormaAlertRed,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Esta ação apagará permanentemente todo o seu perfil, anamnese, ficha de treinos, histórico de cargas e check-ins do dispositivo (LGPD). Esta ação não pode ser desfeita.",
                    color = FormaTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onResetAccount()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FormaAlertRed,
                        contentColor = FormaDarkBg
                    )
                ) {
                    Text("Sim, Excluir Tudo", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FormaDarkCard,
                        contentColor = FormaTextSecondary
                    )
                ) {
                    Text("Cancelar")
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
        // Header
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
            Text(
                text = "Perfil & Configurações",
                color = FormaTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FormaDarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(FormaEmerald.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = FormaEmerald,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = profile?.name ?: "Atleta FormaAI",
                            color = FormaTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = profile?.email ?: "atleta@formaai.app",
                            color = FormaTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Anamnese Summary
            Text(
                text = "RESUMO DA ANAMNESE ATIVA",
                color = FormaTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FormaDarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProfileInfoRow(label = "Objetivo", value = anamnese?.primaryGoal?.label ?: "Hipertrofia")
                    ProfileInfoRow(label = "Nível de Treino", value = anamnese?.experienceLevel?.label ?: "Intermediário")
                    ProfileInfoRow(label = "Frequência", value = "${anamnese?.daysPerWeek ?: 4} dias por semana")
                    ProfileInfoRow(label = "Duração Média", value = "${anamnese?.sessionDurationMinutes ?: 60} minutos")
                    ProfileInfoRow(label = "Local", value = anamnese?.location?.label ?: "Academia")
                    if (!anamnese?.exercisesToAvoid.isNullOrBlank()) {
                        ProfileInfoRow(label = "A Evitar", value = anamnese?.exercisesToAvoid ?: "")
                    }
                }
            }

            // Action: Redo Anamnese
            Button(
                onClick = onRedoAnamnese,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FormaDarkCard,
                    contentColor = FormaEmerald
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Refazer Anamnese & Gerar Novo Plano", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }

            // Privacy & LGPD
            Text(
                text = "PRIVACIDADE & LGPD",
                color = FormaTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FormaDarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = FormaEmerald, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seus dados são seus", color = FormaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Suas informações de saúde, histórico de treino e cargas são armazenadas localmente no seu dispositivo. Você tem controle total para exportar ou excluir todos os registros a qualquer momento.",
                        color = FormaTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Delete Account Button
            Button(
                onClick = { showDeleteConfirmDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("delete_account_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FormaAlertRed.copy(alpha = 0.15f),
                    contentColor = FormaAlertRed
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Excluir Conta e Todos os Dados", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = FormaTextSecondary, fontSize = 12.sp)
        Text(text = value, color = FormaTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
