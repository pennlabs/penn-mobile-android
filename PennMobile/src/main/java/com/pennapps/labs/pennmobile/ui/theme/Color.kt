package com.pennapps.labs.pennmobile.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Basic colors
val White = Color(0xFFFFFFFF)
val WindowBackground = Color(0xFFFFFFFF) // white
val Yellow = Color(0xFFFFFF00)
val Fuchsia = Color(0xFFFF00FF)
val Red = Color(0xFFFF0000)
val Silver = Color(0xFFC0C0C0)
val Gray = Color(0xFF808080)
val DarkGray = Color(0xFF696969)
val Olive = Color(0xFF808000)
val Purple = Color(0xFF800080)
val Maroon = Color(0xFF800000)
val PennRed = Color(0xFF990000)
val Aqua = Color(0xFF00FFFF)
val Lime = Color(0xFF00FF00)
val Teal = Color(0xFF008080)
val Green = Color(0xFF008000)
val Blue = Color(0xFF0000FF)
val Navy = Color(0xFF000080)
val Black = Color(0xFF000000)
val LightGray = Color(0xFFEEEEEE)
val Pink = Color(0xFFE91E63)

// Theme colors
val ColorPrimary = Color(0xFF12274B)
val ColorPrimaryDark = Color(0xFF12274B)
val ColorPrimaryLight = Color(0xFFE8F4FC)
val ColorSecondary = Color(0xFF41C0EE)
val ColorAccent = Color(0xFF2B9BE5)

// Misc
val SecondaryTextMaterialLight = Color(0x8A000000)
val SplashDark = Color(0xFFFFFFFF)
val LogoDarkBlue = Color(0xFF12274B)
val LogoLightBlue = Color(0xFF2B9BE5)
val TextColor = Color(0xFF1A1A1A)
val SecondaryTextColor = Color(0xFFADADAD)
val SettingsGrey = Color(0xFF737373)
val ColorToolbarText = Color(0xFF1A1A1A)
val ColorBottomNavSelected = Color(0xFF1D79CE)
val ColorBottomNavUnselected = Color(0xFFB5B6B6)

val StarColorOff = Color(0xFFCCCCCC)
val StarColorOn = Color(0xFFF4B400)

val ColorBackground = Color(0xFFFFFFFF)
val AvailColorGreen = Color(0xFF8BC34A)
val AvailColorRed = Color(0xFFF44336)

val GsrWhite = Color(0xFFFEFEFE)
val GsrGreen = Color(0xFF6DB786)
val GsrInsideGreen = Color(0x196DB786) // ARGB: 0x19 alpha
val GsrBackgroundGray = Color(0xFFF1F1F1)
val GsrTextGray = Color(0xFFDFDFDF)
val WasherBlue = Color(0xFF3498DB)
val DryerRed = Color(0xFFD35400)
val GsrCancelRed = Color(0xFFE25152)

val PennmobileBlue = Color(0xFF1D79CE)
val FloatingBottomBarSelected = Color(0xFF257FE2)
val PennMobileGrey = Color(0xFFADADAD)
val NewsCardBlurColorOverlay = Color(0x66000000)
val SneakerBlurColorOverlay = Color(0xD913284B)
val SneakerWarningOverlay = Color(0xBFFB8C00)
val DialogBlurColorOverlay = Color(0x41FFFFFF)

val DiningGreen = Color(0xFFBADFB8)
val DiningBlue = Color(0xFF99BCF7)

val DarkRedBackground = Color(0xFFD9534F)
val LightBlue50 = Color(0xFFE1F5FE)
val LightBlue200 = Color(0xFF81D4FA)
val LightBlue600 = Color(0xFF039BE5)
val LightBlue900 = Color(0xFF01579B)

val LightColors =
    lightColorScheme(
        primary = ColorPrimary,
        onPrimary = White,
        secondary = ColorAccent,
        onSecondary = White,
        background = WindowBackground,
        onBackground = TextColor,
        surface = WindowBackground,
        onSurface = TextColor,
    )

val DarkColors =
    darkColorScheme(
        primary = ColorPrimary,
        onPrimary = White,
        secondary = ColorAccent,
        onSecondary = White,
        background = Color(0xFF000000),
        onBackground = White,
        surface = Color(0xFF121212),
        onSurface = White,
    )
