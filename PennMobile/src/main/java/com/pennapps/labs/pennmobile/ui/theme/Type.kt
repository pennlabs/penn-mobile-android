import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.ui.theme.ColorPrimary
import com.pennapps.labs.pennmobile.ui.theme.SecondaryTextColor
import com.pennapps.labs.pennmobile.ui.theme.TextColor

val GilroyExtraBold = FontFamily(Font(R.font.gilroy_extra_bold))
val GilroyBold = FontFamily(Font(R.font.gilroy_bold))
val GilroyLight = FontFamily(Font(R.font.gilroy_light))
val SFProDisplayRegular = FontFamily(Font(R.font.sf_pro_display_regular))
val SFProDisplayMedium = FontFamily(Font(R.font.sf_pro_display_medium))
val SFProRoundedBold = FontFamily(Font(R.font.sf_pro_rounded_bold))

val AppTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = GilroyExtraBold,
        fontSize = 28.sp,
        color = ColorPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = GilroyLight,
        fontSize = 11.sp,
        color = TextColor
    ),
    labelMedium = TextStyle(
        fontFamily = SFProDisplayRegular,
        fontSize = 12.sp,
        color = SecondaryTextColor
    ),
    headlineMedium = TextStyle(
        fontFamily = GilroyBold,
        fontSize = 24.sp,
        color = TextColor
    )
)
