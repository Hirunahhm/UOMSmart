package com.example.uomsmart.ui.theme

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

private val LightColorScheme =
        lightColorScheme(
                primary = UOMBlue,
                onPrimary = OnBlue,
                primaryContainer = UOMPrimaryContainer,
                onPrimaryContainer = UOMBlueDark,
                secondary = UOMGold,
                onSecondary = OnGold,
                secondaryContainer = UOMGoldLight,
                onSecondaryContainer = UOMGoldDark,
                tertiary = UOMTeal,
                onTertiary = OnTeal,
                tertiaryContainer = UOMTealLight,
                onTertiaryContainer = UOMTeal,
                background = UOMSurface,
                onBackground = OnSurface,
                surface = UOMSurface,
                onSurface = OnSurface,
                surfaceVariant = UOMSurfaceVariant,
                onSurfaceVariant = OnSurfaceVariant
        )

private val DarkColorScheme =
        darkColorScheme(
                primary = UOMBlueLight,
                onPrimary = OnBlue,
                primaryContainer = UOMBlueDark,
                onPrimaryContainer = UOMPrimaryContainer,
                secondary = UOMGoldLight,
                onSecondary = OnGold,
                secondaryContainer = UOMGoldDark,
                onSecondaryContainer = UOMGoldLight,
                tertiary = UOMTealLight,
                onTertiary = OnTeal,
                tertiaryContainer = UOMTeal,
                onTertiaryContainer = UOMTealLight
        )

@Composable
fun UOMSmartTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
