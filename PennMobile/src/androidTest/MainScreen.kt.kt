import com.agoda.kakao.text.KButton
import com.kaspersky.kaspresso.screens.KScreen

object `MainScreen.kt` : KScreen<`MainScreen.kt`>() {

    override val layoutId: Int? = R.layout.activity_main
    override val viewClass: Class<*>? = MainActivity::class.java

    val button = KButton { withId(R.id.button) }
    val textResult = KButton { withId(R.id.textResult) }

}