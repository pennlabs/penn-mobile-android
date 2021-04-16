import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test

// Instrumentation tests, written using Kaspresso https://github.com/KasperskyLab/Kaspresso
class MainActivityTest : TestCase() {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Test
    fun testMainActivity() {
        before {
            activityTestRule.launchActivity(null)
            testLogger.i("Starting test")
        }.after {
        }.run {
            step("Test setup") {
                MainScreen {
                    button.hasTag("0")
                }
            }
            step("Test for change on click") {
                MainScreen {
                    button.click()
                    textResult.hasText("Hello World")
                }
            }
        }
    }
}
