package com.pennapps.labs.pennmobile.compose.presentation.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.pennapps.labs.pennmobile.R

val provider =
    GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs,
    )

// Define the GoogleFont references
private val CabinFont = GoogleFont("Cabin")
private val GoogleSansFont = GoogleFont("Google Sans")

// Cabin Font Family
val cabinFontFamily =
    FontFamily(
        androidx.compose.ui.text.googlefonts.Font(googleFont = CabinFont, fontProvider = provider, weight = FontWeight.Normal), // Regular
        androidx.compose.ui.text.googlefonts.Font(googleFont = CabinFont, fontProvider = provider, weight = FontWeight.Medium),
        androidx.compose.ui.text.googlefonts.Font(googleFont = CabinFont, fontProvider = provider, weight = FontWeight.SemiBold),
    )

// Google Sans Font Family
val googleSansFontFamily =
    FontFamily(
        androidx.compose.ui.text.googlefonts.Font(googleFont = GoogleSansFont, fontProvider = provider, weight = FontWeight.Normal), // Regular
        androidx.compose.ui.text.googlefonts.Font(googleFont = GoogleSansFont, fontProvider = provider, weight = FontWeight.Medium),
        androidx.compose.ui.text.googlefonts.Font(googleFont = GoogleSansFont, fontProvider = provider, weight = FontWeight.SemiBold),
    )

val GilroyFontFamily =
    FontFamily(
        Font(R.font.gilroy_light, FontWeight.Normal),
        Font(R.font.gilroy_bold, FontWeight.Medium),
        Font(R.font.gilroy_extra_bold, FontWeight.Bold),
        Font(R.font.gilroy_extra_bold, FontWeight.ExtraBold),
    )

val sfProFontFamily =
    FontFamily(
        Font(R.font.sf_pro_display_regular, FontWeight.Normal),
        Font(R.font.sf_pro_display_medium, FontWeight.Medium),
        Font(R.font.sf_pro_display_bold, FontWeight.Bold),
    )

val sfProRoundedFontFamily =
    FontFamily(
        Font(R.font.sf_pro_rounded_medium, FontWeight.Medium),
        Font(R.font.sf_pro_rounded_bold, FontWeight.Bold),
    )

val segoeFontFamily =
    FontFamily(
        Font(R.font.segoe_ui, FontWeight.Normal),
        Font(R.font.segoe_ui_bold, FontWeight.Bold),
        Font(R.font.segoe_ui_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.segoe_ui_bold_italic, FontWeight.Bold, FontStyle.Italic),
    )
