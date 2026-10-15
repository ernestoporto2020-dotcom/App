package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.ActiveWorkoutScreen
import com.example.ui.screens.AnamneseScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CheckInScreen
import com.example.ui.screens.CoachChatScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WorkoutSheetScreen
import com.example.ui.theme.FormaAITheme
import com.example.ui.theme.FormaDarkBg
import com.example.ui.theme.FormaDarkBorder
import com.example.ui.theme.FormaDarkCard
import com.example.ui.theme.FormaEmerald
import com.example.ui.theme.FormaTextPrimary
import com.example.ui.theme.FormaTextSecondary
import com.example.ui.viewmodel.FormaScreen
import com.example.ui.viewmodel.FormaViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FormaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FormaAITheme {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

data class BottomNavItem(
    val screen: FormaScreen,
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun MainContent(viewModel: FormaViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val workoutLogs by viewModel.workoutLogs.collectAsState()
    val totalSessions by viewModel.totalSessions.collectAsState()
    val totalVolume by viewModel.totalVolume.collectAsState()
    val recentLoads by viewModel.recentLoads.collectAsState()

    val anamneseStep by viewModel.anamneseStep.collectAsState()
    val anamneseData by viewModel.anamneseData.collectAsState()
    val isGeneratingPlan by viewModel.isGeneratingPlan.collectAsState()
    val safetyBlockedMessage by viewModel.safetyBlockedMessage.collectAsState()

    val activeSessionDay by viewModel.activeSessionDay.collectAsState()
    val activeExercises by viewModel.activeExercises.collectAsState()
    val workoutNotes by viewModel.workoutNotes.collectAsState()
    val workoutStartTime by viewModel.workoutStartTime.collectAsState()
    val restSecondsRemaining by viewModel.restSecondsRemaining.collectAsState()
    val restTimerTotal by viewModel.restTimerTotal.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()

    val chatMessages by viewModel.chatMessages.collectAsState()
    val isCoachTyping by viewModel.isCoachTyping.collectAsState()

    val isCheckInSubmitting by viewModel.isCheckInSubmitting.collectAsState()
    val lastCheckInFeedback by viewModel.lastCheckInFeedback.collectAsState()

    val activePlan = viewModel.getActivePlan()
    val savedAnamnese = viewModel.getAnamnese() ?: anamneseData

    // Hardware Back Navigation
    BackHandler(enabled = currentScreen != FormaScreen.LANDING && currentScreen != FormaScreen.DASHBOARD) {
        when (currentScreen) {
            FormaScreen.ANAMNESE -> viewModel.prevAnamneseStep()
            FormaScreen.AUTH -> viewModel.navigateTo(FormaScreen.LANDING)
            FormaScreen.WORKOUT_SHEET, FormaScreen.HISTORY, FormaScreen.COACH_CHAT, FormaScreen.CHECK_IN, FormaScreen.PROFILE -> {
                viewModel.navigateTo(FormaScreen.DASHBOARD)
            }
            FormaScreen.ACTIVE_WORKOUT -> {
                // Keep inside workout to prevent accidental progress loss
            }
            else -> {}
        }
    }

    val bottomNavItems = listOf(
        BottomNavItem(FormaScreen.DASHBOARD, "Hoje", Icons.Default.FitnessCenter, "nav_today"),
        BottomNavItem(FormaScreen.WORKOUT_SHEET, "Ficha", Icons.Default.FormatListBulleted, "nav_sheet"),
        BottomNavItem(FormaScreen.HISTORY, "Evolução", Icons.Default.TrendingUp, "nav_history"),
        BottomNavItem(FormaScreen.COACH_CHAT, "Coach IA", Icons.Default.Psychology, "nav_coach"),
        BottomNavItem(FormaScreen.PROFILE, "Perfil", Icons.Default.Person, "nav_profile")
    )

    val showBottomBar = currentScreen in listOf(
        FormaScreen.DASHBOARD,
        FormaScreen.WORKOUT_SHEET,
        FormaScreen.HISTORY,
        FormaScreen.COACH_CHAT,
        FormaScreen.PROFILE
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = FormaDarkBg,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = FormaDarkCard,
                    contentColor = FormaEmerald,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentScreen == item.screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(item.screen) },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    color = if (isSelected) FormaEmerald else FormaTextSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = FormaEmerald,
                                unselectedIconColor = FormaTextSecondary,
                                indicatorColor = FormaEmerald.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                FormaScreen.LANDING -> {
                    LandingScreen(
                        onStartAnamnese = { viewModel.navigateTo(FormaScreen.ANAMNESE) },
                        onLoginClick = { viewModel.navigateTo(FormaScreen.AUTH) }
                    )
                }

                FormaScreen.AUTH -> {
                    AuthScreen(
                        onBack = { viewModel.navigateTo(FormaScreen.LANDING) },
                        onSuccess = { name, email ->
                            viewModel.setUserName(name)
                            viewModel.setUserEmail(email)
                            viewModel.navigateTo(FormaScreen.ANAMNESE)
                        }
                    )
                }

                FormaScreen.ANAMNESE -> {
                    AnamneseScreen(
                        currentStep = anamneseStep,
                        anamnese = anamneseData,
                        isGenerating = isGeneratingPlan,
                        safetyBlockedMessage = safetyBlockedMessage,
                        onUpdateAnamnese = { viewModel.updateAnamnese(it) },
                        onNextStep = { viewModel.nextAnamneseStep() },
                        onPrevStep = { viewModel.prevAnamneseStep() },
                        onDismissSafety = { viewModel.dismissSafetyWarning() }
                    )
                }

                FormaScreen.DASHBOARD -> {
                    DashboardScreen(
                        profile = userProfile,
                        anamnese = savedAnamnese,
                        plan = activePlan,
                        totalSessions = totalSessions,
                        totalVolumeKg = totalVolume ?: 0.0,
                        onStartWorkout = { day -> viewModel.startWorkoutSession(day) },
                        onViewWorkoutSheet = { viewModel.navigateTo(FormaScreen.WORKOUT_SHEET) },
                        onOpenCoachChat = { viewModel.navigateTo(FormaScreen.COACH_CHAT) },
                        onOpenCheckIn = { viewModel.navigateTo(FormaScreen.CHECK_IN) },
                        onOpenHistory = { viewModel.navigateTo(FormaScreen.HISTORY) }
                    )
                }

                FormaScreen.WORKOUT_SHEET -> {
                    WorkoutSheetScreen(
                        plan = activePlan,
                        onBack = { viewModel.navigateTo(FormaScreen.DASHBOARD) },
                        onStartWorkout = { day -> viewModel.startWorkoutSession(day) }
                    )
                }

                FormaScreen.ACTIVE_WORKOUT -> {
                    ActiveWorkoutScreen(
                        sessionDay = activeSessionDay,
                        exercises = activeExercises,
                        workoutStartTime = workoutStartTime,
                        restSecondsRemaining = restSecondsRemaining,
                        restTimerTotal = restTimerTotal,
                        isTimerRunning = isTimerRunning,
                        notes = workoutNotes,
                        onUpdateSet = { exIdx, setIdx, w, r, done ->
                            viewModel.updateSetRecord(exIdx, setIdx, w, r, done)
                        },
                        onStartRestTimer = { sec -> viewModel.startRestTimer(sec) },
                        onAddRestTime = { sec -> viewModel.addRestTime(sec) },
                        onSkipRestTimer = { viewModel.skipRestTimer() },
                        onNotesChange = { viewModel.setWorkoutNotes(it) },
                        onFinishWorkout = { viewModel.finishWorkoutSession() },
                        onCancelWorkout = { viewModel.navigateTo(FormaScreen.DASHBOARD) }
                    )
                }

                FormaScreen.HISTORY -> {
                    HistoryScreen(
                        logs = workoutLogs,
                        recentLoads = recentLoads,
                        totalSessions = totalSessions,
                        totalVolumeKg = totalVolume ?: 0.0,
                        onBack = { viewModel.navigateTo(FormaScreen.DASHBOARD) }
                    )
                }

                FormaScreen.COACH_CHAT -> {
                    CoachChatScreen(
                        messages = chatMessages,
                        isTyping = isCoachTyping,
                        onSendMessage = { viewModel.sendChatMessage(it) },
                        onBack = { viewModel.navigateTo(FormaScreen.DASHBOARD) }
                    )
                }

                FormaScreen.CHECK_IN -> {
                    CheckInScreen(
                        plannedDays = savedAnamnese.daysPerWeek,
                        isSubmitting = isCheckInSubmitting,
                        lastFeedback = lastCheckInFeedback,
                        onSubmitCheckIn = { viewModel.submitWeeklyCheckIn(it) },
                        onResetFeedback = {
                            viewModel.resetCheckInFeedback()
                            viewModel.navigateTo(FormaScreen.DASHBOARD)
                        },
                        onBack = { viewModel.navigateTo(FormaScreen.DASHBOARD) }
                    )
                }

                FormaScreen.PROFILE -> {
                    ProfileScreen(
                        profile = userProfile,
                        anamnese = savedAnamnese,
                        plan = activePlan,
                        onRedoAnamnese = { viewModel.navigateTo(FormaScreen.ANAMNESE) },
                        onResetAccount = { viewModel.resetAccountData() },
                        onBack = { viewModel.navigateTo(FormaScreen.DASHBOARD) }
                    )
                }
            }
        }
    }
}
