package com.echologic.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ── Professional Shapes ──
val OriginalShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp)
)

@Composable
fun EchoLogicTheme(
    isDarkMode: Boolean = true,
    accentColor: Color = ElectricYellow,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkMode) {
        darkColorScheme(
            primary = accentColor,
            onPrimary = if (accentColor.luminance() > 0.5f) Color.Black else Color.White,
            secondary = accentColor.copy(alpha = 0.7f),
            onSecondary = Color.White,
            background = DeepBlack,
            onBackground = Color.White,
            surface = DarkSurface,
            onSurface = Color.White,
            surfaceVariant = DarkCard,
            onSurfaceVariant = TextGray,
            outline = accentColor
        )
    } else {
        lightColorScheme(
            primary = accentColor,
            onPrimary = if (accentColor.luminance() > 0.5f) Color.Black else Color.White,
            secondary = accentColor.copy(alpha = 0.7f),
            onSecondary = Color.White,
            background = Color.White,
            onBackground = DeepBlack,
            surface = Color(0xFFF5F5F5),
            onSurface = DeepBlack,
            surfaceVariant = Color(0xFFEEEEEE),
            onSurfaceVariant = Color.DarkGray,
            outline = accentColor
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = OriginalShapes
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            content()
        }
    }
}

private fun Color.luminance(): Float {
    return 0.2126f * red + 0.7152f * green + 0.0722f * blue
}
