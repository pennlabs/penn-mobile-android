import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.ui.theme.ColorPrimary
import com.pennapps.labs.pennmobile.ui.theme.SecondaryTextColor
import com.pennapps.labs.pennmobile.ui.theme.TextColor

public val GilroyExtraBold = FontFamily(Font(R.font.gilroy_extra_bold))
val GilroyBold = FontFamily(Font(R.font.gilroy_bold))
val GilroyLight = FontFamily(Font(R.font.gilroy_light))
val SFProDisplayRegular = FontFamily(Font(R.font.sf_pro_display_regular))
val SFProDisplayMedium = FontFamily(Font(R.font.sf_pro_display_medium))
val SFProRoundedBold = FontFamily(Font(R.font.sf_pro_rounded_bold))

val AppTypography =
    Typography(
        // Main header in dining insights card
        titleLarge =
            TextStyle(
                fontFamily = GilroyExtraBold, // matches header style
                fontSize = 28.sp, // XML was ~header size
                color = ColorPrimary,
            ),
        // Small note text (like extraNote)
        bodySmall =
            TextStyle(
                fontFamily = GilroyLight,
                fontSize = 11.sp,
                color = TextColor,
            ),
        // Labels inside cards, e.g., "Extra" label
        bodyMedium =
            TextStyle(
                fontFamily = GilroyLight,
                fontSize = 14.sp, // XML did not specify, 14sp looks good
                color = SecondaryTextColor,
            ),
        // Main numbers (like extraAmount)
        headlineMedium =
            TextStyle(
                fontFamily = GilroyBold,
                fontSize = 20.sp, // matches XML extraAmount
                color = TextColor,
            ),
        // For optional secondary labels (like small descriptions)
        labelMedium =
            TextStyle(
                fontFamily = SFProDisplayRegular,
                fontSize = 12.sp,
                color = SecondaryTextColor,
            ),
    )
