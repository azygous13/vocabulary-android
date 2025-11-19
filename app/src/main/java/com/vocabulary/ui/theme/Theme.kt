package com.vocabulary.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = Primary,

    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = Secondary,

    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = Tertiary,

    error = Error,
    onError = OnPrimary,
    errorContainer = Color(0xFFFFEDEA), // Red-50
    onErrorContainer = Error,

    background = Background,
    onBackground = OnBackground,

    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceTint = Primary,

    outline = OnSurfaceVariant,
    outlineVariant = Color(0xFFE5E7EB) // Gray-200
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDarkTheme,
    onPrimary = Color(0xFF1E1B4B), // Indigo-950
    primaryContainer = Color(0xFF312E81), // Indigo-900
    onPrimaryContainer = PrimaryLight,

    secondary = SecondaryDarkTheme,
    onSecondary = Color(0xFF042F2E), // Teal-950
    secondaryContainer = Color(0xFF134E4A), // Teal-900
    onSecondaryContainer = SecondaryLight,

    tertiary = TertiaryDarkTheme,
    onTertiary = Color(0xFF3B0764), // Purple-950
    tertiaryContainer = Color(0xFF581C87), // Purple-900
    onTertiaryContainer = TertiaryLight,

    error = Error,
    onError = Color(0xFF7F1D1D), // Red-950
    errorContainer = Color(0xFF991B1B), // Red-900
    onErrorContainer = Color(0xFFFECACA), // Red-200

    background = BackgroundDarkTheme,
    onBackground = OnBackgroundDarkTheme,

    surface = SurfaceDarkTheme,
    onSurface = OnSurfaceDarkTheme,
    surfaceVariant = SurfaceVariantDarkTheme,
    onSurfaceVariant = OnSurfaceVariantDarkTheme,
    surfaceTint = PrimaryDarkTheme,

    outline = OnSurfaceVariantDarkTheme,
    outlineVariant = Color(0xFF475569) // Slate-600
)

@Composable
fun VocabularyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use background color for status bar for modern look
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
