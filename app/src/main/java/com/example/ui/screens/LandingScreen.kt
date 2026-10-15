package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.FormaDarkBg
import com.example.ui.theme.FormaDarkBorder
import com.example.ui.theme.FormaDarkCard
import com.example.ui.theme.FormaDarkCardElevated
import com.example.ui.theme.FormaEmerald
import com.example.ui.theme.FormaTextMuted
import com.example.ui.theme.FormaTextPrimary
import com.example.ui.theme.FormaTextSecondary

@Composable
fun LandingScreen(
    onStartAnamnese: () -> Unit,
    onLoginClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FormaDarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // App Logo & Badge
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(FormaDarkCardElevated)
                .border(1.dp, FormaEmerald.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_formaai_logo),
                contentDescription = "Logo FormaAI",
                modifier = Modifier.size(68.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tagline Pill
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(FormaEmerald.copy(alpha = 0.12f))
                .border(1.dp, FormaEmerald.copy(alpha = 0.3f), CircleShape)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = FormaEmerald,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "PERSONAL TRAINER COM IA",
                    color = FormaEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Treinos Sob Medida.\nEvolução Comprovada.",
            color = FormaTextPrimary,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "O FormaAI analisa sua anamnese, limitações e equipamentos para prescrever fichas inteligentes, guiar séries em tempo real e garantir sobrecarga progressiva.",
            color = FormaTextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Main CTA Button
        Button(
            onClick = onStartAnamnese,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("start_anamnese_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = FormaEmerald,
                contentColor = FormaDarkBg
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Começar Minha Anamnese",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("login_button"),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = FormaTextPrimary
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Já possuo conta cadastrada",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Features Grid
        Text(
            text = "POR QUE O FORMAAI É DIFERENTE",
            color = FormaTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LandingFeatureCard(
            icon = Icons.Default.Security,
            title = "Anamnese Clínica e Segura",
            description = "Filtro biomecânico para dores articulares e histórico de lesões. Treino seguro sem arriscar a sua saúde."
        )

        Spacer(modifier = Modifier.height(10.dp))

        LandingFeatureCard(
            icon = Icons.Default.FitnessCenter,
            title = "Execução Guiada & Rest Timer",
            description = "Marque séries concluídas, registre cargas e use o temporizador de descanso sincronizado."
        )

        Spacer(modifier = Modifier.height(10.dp))

        LandingFeatureCard(
            icon = Icons.Default.TrendingUp,
            title = "Progressão Real de Cargas",
            description = "Acompanhe seu volume semanal levantado e a evolução de 1RM estimada em cada levantamento."
        )

        Spacer(modifier = Modifier.height(10.dp))

        LandingFeatureCard(
            icon = Icons.Default.Psychology,
            title = "Coach Contextual com Gemini",
            description = "Tire dúvidas sobre substituições se o aparelho estiver ocupado, consulte sua última carga ou peça ajustes."
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Trust statement
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(FormaDarkCard)
                .border(1.dp, FormaDarkBorder, RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = FormaEmerald,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Seus dados de saúde e treino ficam protegidos no seu dispositivo com persistência segura.",
                color = FormaTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun LandingFeatureCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = FormaDarkCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, FormaDarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(FormaEmerald.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = FormaEmerald,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = FormaTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = FormaTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
