package turmaA.grupoB.LinkStage.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Original Colors
val DarkBlue = Color(0xFF0E1572)
val LightBlue = Color(0xFF30C9CD)
val Red = Color(0xFFFF383C)
val BorderGrey = Color(0xFFD9D9D9)
val DarkGrey = Color(0xFF737373)
val MediumBlue = Color(0xFF326B9B)
val BackgroundLight = Color(0xFFF5F5F5)
val Green = Color(0xFF4CAF50)

// Aliases for compatibility with other screens
val BlueDark = DarkBlue
val BlueLight = LightBlue
val RedAccent = Red
val GrayBorder = BorderGrey
val GrayDark = DarkGrey

// New Colors for Registration
val AdvisorPurple = Color(0xFF8E44AD)
val CompanyGreen = Color(0xFF27AE60)

// Intro Sliders Colors
val IntroBackground = Color(0xFF111E29)
val SliderGreen = Color(0xFF96E6A1)
val SliderBlue = Color(0xFF4481EB)

val ButtonGradient = Brush.horizontalGradient(
    colors = listOf(SliderGreen, SliderBlue)
)

val Fade1 = Brush.horizontalGradient(
    colors = listOf(LightBlue, DarkBlue)
)

val Fade2 = Brush.horizontalGradient(
    colorStops = arrayOf(
        0.0f to LightBlue,
        0.27f to DarkBlue,
        1.0f to DarkBlue
    )
)

val Fade3 = Brush.horizontalGradient(
    colorStops = arrayOf(
        0.0f to DarkBlue,
        0.65f to DarkBlue,
        0.89f to MediumBlue,
        1.0f to LightBlue
    )
)
