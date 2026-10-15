package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = FormaEmerald,
    onPrimary = FormaDarkBg,
    primaryContainer = FormaDarkCardElevated,
    onPrimaryContainer = FormaEmeraldLight,
    secondary = FormaCyan,
    onSecondary = FormaDarkBg,
    secondaryContainer = FormaCyanDark,
    onSecondaryContainer = FormaTextPrimary,
    background = FormaDarkBg,
    onBackground = FormaTextPrimary,
    surface = FormaDarkSurface,
    onSurface = FormaTextPrimary,
    surfaceVariant = FormaDarkCard,
    onSurfaceVariant = FormaTextSecondary,
    outline = FormaDarkBorder,
    error = FormaAlertRed
)

private val LightColorScheme = lightColorScheme(
    primary = FormaEmeraldDark,
    onPrimary = FormaLightSurface,
    primaryContainer = FormaLightCard,
    onPrimaryContainer = FormaLightTextPrimary,
    secondary = FormaCyanDark,
    onSecondary = FormaLightSurface,
    background = FormaLightBg,
    onBackground = FormaLightTextPrimary,
    surface = FormaLightSurface,
    onSurface = FormaLightTextPrimary,
    surfaceVariant = FormaLightCard,
    onSurfaceVariant = FormaLightTextSecondary,
    outline = FormaLightBorder,
    error = FormaAlertRed
)

@Composable
fun FormaAITheme(
    darkTheme: Boolean = true, // FormaAI default signature is high-contrast dark fitness aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
