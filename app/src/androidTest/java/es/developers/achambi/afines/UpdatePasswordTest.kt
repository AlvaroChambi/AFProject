package es.developers.achambi.afines
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import es.developers.achambi.afines.profile.ui.UpdatePasswordActivity
import org.junit.Rule
import org.junit.Test

class UpdatePasswordTest:BaseUITest() {
    @get:Rule
    public val customActivityTestRule: TestRule<UpdatePasswordActivity> = TestRule(
        UpdatePasswordActivity::class.java, this
    )

    @Test
    fun testPasswordUpdate() {
        onView(withId(R.id.current_pass_edit_text)).perform(clearText(), typeText("0123456"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.new_password_edit_text)).perform(clearText(), typeText("6543210"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.new_passwordconfimation_edit_text)).perform(clearText(), typeText("6543210"))
        Espresso.closeSoftKeyboard()

        onView(withId(R.id.action_save_password)).perform(click())

        onView(withText(R.string.password_update_success_text)).check(matches(isDisplayed()))
    }
}