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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FormaDarkBg
import com.example.ui.theme.FormaDarkBorder
import com.example.ui.theme.FormaDarkCard
import com.example.ui.theme.FormaDarkCardElevated
import com.example.ui.theme.FormaEmerald
import com.example.ui.theme.FormaTextMuted
import com.example.ui.theme.FormaTextPrimary
import com.example.ui.theme.FormaTextSecondary

@Composable
fun AuthScreen(
    onBack: () -> Unit,
    onSuccess: (name: String, email: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Cadastro
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isRecoveringPassword by remember { mutableStateOf(false) }
    var recoverySuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FormaDarkBg)
            .padding(20.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = FormaTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isRecoveringPassword) "Recuperação de Acesso" else if (selectedTab == 0) "Bem-vindo de volta" else "Crie sua conta FormaAI",
            color = FormaTextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isRecoveringPassword) "Digite seu e-mail para receber as instruções de redefinição." else if (selectedTab == 0) "Acesse seus treinos e histórico de evolução." else "Comece sua jornada com treino inteligente baseado em ciência.",
            color = FormaTextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!isRecoveringPassword) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = FormaDarkCard,
                contentColor = FormaEmerald,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = FormaEmerald
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, FormaDarkBorder, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; errorMessage = null },
                    text = {
                        Text(
                            text = "Entrar",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) FormaEmerald else FormaTextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; errorMessage = null },
                    text = {
                        Text(
                            text = "Cadastrar",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) FormaEmerald else FormaTextSecondary
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Form Fields
        if (!isRecoveringPassword && selectedTab == 1) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome completo") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FormaEmerald) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FormaEmerald,
                    unfocusedBorderColor = FormaDarkBorder,
                    focusedLabelColor = FormaEmerald,
                    unfocusedLabelColor = FormaTextSecondary,
                    focusedTextColor = FormaTextPrimary,
                    unfocusedTextColor = FormaTextPrimary,
                    focusedContainerColor = FormaDarkCard,
                    unfocusedContainerColor = FormaDarkCard
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = FormaEmerald) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_email_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FormaEmerald,
                unfocusedBorderColor = FormaDarkBorder,
                focusedLabelColor = FormaEmerald,
                unfocusedLabelColor = FormaTextSecondary,
                focusedTextColor = FormaTextPrimary,
                unfocusedTextColor = FormaTextPrimary,
                focusedContainerColor = FormaDarkCard,
                unfocusedContainerColor = FormaDarkCard
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (!isRecoveringPassword) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = FormaEmerald) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FormaEmerald,
                    unfocusedBorderColor = FormaDarkBorder,
                    focusedLabelColor = FormaEmerald,
                    unfocusedLabelColor = FormaTextSecondary,
                    focusedTextColor = FormaTextPrimary,
                    unfocusedTextColor = FormaTextPrimary,
                    focusedContainerColor = FormaDarkCard,
                    unfocusedContainerColor = FormaDarkCard
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedTab == 0) {
                Text(
                    text = "Esqueci minha senha",
                    color = FormaEmerald,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { isRecoveringPassword = true }
                        .padding(vertical = 4.dp)
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = errorMessage ?: "",
                color = androidx.compose.ui.graphics.Color(0xFFEF4444),
                fontSize = 12.sp
            )
        }

        if (recoverySuccess) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Link de recuperação enviado com sucesso para seu e-mail!",
                color = FormaEmerald,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isRecoveringPassword) {
                    if (email.isBlank() || !email.contains("@")) {
                        errorMessage = "Informe um e-mail válido."
                    } else {
                        errorMessage = null
                        recoverySuccess = true
                    }
                } else if (selectedTab == 0) {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Preencha e-mail e senha."
                    } else {
                        onSuccess(if (name.isBlank()) "Atleta FormaAI" else name, email)
                    }
                } else {
                    if (name.isBlank() || email.isBlank() || password.length < 6) {
                        errorMessage = "Preencha todos os campos (senha mín. 6 dígitos)."
                    } else {
                        onSuccess(name, email)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("submit_auth_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = FormaEmerald,
                contentColor = FormaDarkBg
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isRecoveringPassword) "Enviar link de recuperação" else if (selectedTab == 0) "Entrar no FormaAI" else "Criar Minha Conta",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        if (isRecoveringPassword) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { isRecoveringPassword = false; recoverySuccess = false },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = FormaDarkCard, contentColor = FormaTextSecondary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Voltar ao Login")
            }
        }
    }
}
