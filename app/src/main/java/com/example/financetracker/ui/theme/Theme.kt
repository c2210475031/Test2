package com.example.financetracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = D_PrimaryBlue,
    secondary = D_AccentIndigo,
    tertiary = D_SubtlePurple,
    background = D_BackgroundDeepNavy,
    surface = D_SurfaceDarkBlueGray,
    onPrimary = D_TextOnPrimaryDark,
    onSecondary = D_TextOnAccentWhite,
    onTertiary = D_TextOnTertiaryWhite,
    onBackground = D_TextOnBackgroundGray,
    onSurface = D_TextOnSurfaceLavender
)

private val LightColorScheme = lightColorScheme(
    primary = L_PrimaryGreen,
    secondary = L_AccentGreen,
    tertiary = L_SubtleGreen,
    background = L_BackgroundGreenishWhite,
    surface = L_SurfaceSoftGreen,
    onPrimary = L_TextOnPrimaryWhite,
    onSecondary = L_TextOnAccentDarkGreen,
    onTertiary = L_TextOnTertiaryDeepGreen,
    onBackground = L_TextOnBackgroundDarkGreen,
    onSurface = L_TextOnSurfaceDeepGreen
)

@Composable
fun FinanceTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )

//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography,
//        content = content
//    )
}