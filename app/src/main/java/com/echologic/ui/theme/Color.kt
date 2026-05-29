package com.echologic.ui.theme

import androidx.compose.ui.graphics.Color

// ── Neo-Brutalist Core Palette ──
val ElectricYellow = Color(0xFFF4FF4D)
val DeepBlack = Color(0xFF000000)
val DarkSurface = Color(0xFF0A0A0A)
val DarkCard = Color(0xFF151515)
val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFFAAAAAA)
val TextMuted = Color(0xFF666666)
val MintGreen = Color(0xFF4DFFB4)

// ── Cyber-Vibrant (Neon Precision) ──
val CyberPrimary = Color(0xFF00FFFF)      // Neon Cyan
val CyberSecondary = Color(0xFFFF00FF)    // Neon Pink
val CyberBackground = Color(0xFF000000)   // Absolute Black
val CyberSurface = Color(0xFF121212)
val CyberOnPrimary = Color(0xFF0D0D0D)

// ── Glassmorphism (Translucent Depth) ──
val GlassPrimary = Color(0xFF6366F1)      // Indigo-Blue
val GlassSecondary = Color(0xFFA855F7)    // Purple
val GlassBackground = Color(0xFF060E20)   // Cosmic Indigo
val GlassSurface = Color(0xFFFFFFFF).copy(alpha = 0.1f) // Frosted base
val GlassAccent = Color(0xFFF472B6)       // Pink

// ── Editorial Luxury (Typographic Sophistication) ──
val EditorialPaper = Color(0xFFFAF9F7)    // Premium Off-White
val EditorialInk = Color(0xFF121212)      // Elegant Charcoal
val EditorialMuted = Color(0xFF474747)
val EditorialSurface = Color(0xFFF4F3F1)

// Hacker Mode Colors
val TerminalGreen = Color(0xFF00FF41)
val TerminalDark = Color(0xFF0D0D0D)
val TerminalGlow = Color(0xFF008F11)
val TerminalTextMuted = Color(0xFF005500)

val AccentPink = Color(0xFFFF6B9D)
val AccentCyan = Color(0xFF00F5FF)

// ── Vibe Category Colors ──
val VibeLogicLoops = Color(0xFFFFFF00)     // Electric Yellow
val VibeShowerThoughts = Color(0xFF00F5FF) // Cyan
val VibeAbsurd = Color(0xFFFF6B9D)         // Pink
val VibeDeepDumb = Color(0xFFBFFFC7)       // Mint Green
val VibeAccidentalPhilosophy = Color(0xFFE040FB) // Purple
val VibeBrainrot = Color(0xFFFF5722)       // Neon Orange

/**
 * Returns the accent color for a given quote category.
 */
fun categoryColor(category: String): Color {
    return when (category) {
        "Aura & Vibe" -> Color(0xFF00F5FF)        // Cyber Cyan
        "Mindset Growth" -> Color(0xFF4DFFB4)    // Mint Green
        "Skibidi Chaos" -> Color(0xFFFF5722)     // Neon Orange
        "Let Him Cook" -> Color(0xFFF4FF4D)      // Electric Yellow
        "NPC vs Main Character" -> Color(0xFFAAAAAA) // Gray
        "Rizz & Social" -> Color(0xFFFF00FF)     // Neon Pink
        "Fanum Tax" -> Color(0xFFFFC107)         // Amber
        "Crash Out" -> Color(0xFFFF1744)         // Radical Red
        "Peace & Detachment" -> Color(0xFFE040FB) // Electric Purple
        "Life Simulation" -> Color(0xFF6366F1)    // Indigo
        "Logic Loops" -> VibeLogicLoops
        "Shower Thoughts" -> VibeShowerThoughts
        "Absurd" -> VibeAbsurd
        "Deep Dumb" -> VibeDeepDumb
        "Accidental Philosophy" -> VibeAccidentalPhilosophy
        "Brainrot" -> VibeBrainrot
        else -> ElectricYellow
    }
}
