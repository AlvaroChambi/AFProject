package es.developers.achambi.afines

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class LoginTest: BaseTest() {
    @get:Rule
    public val customActivityTestRule: ActivityTestRule<LoginActivity> = ActivityTestRule(
        LoginActivity::class.java
    )

    @Test
    fun loginTest() {
        MockSetup.setLoginState(true)
        onView(withId(R.id.login_email_edit_text)).perform(
            clearText(), typeText("test@gmail.com"))
        onView(withId(R.id.login_pass_edit_text)).perform(
            clearText(), typeText("0123456"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.login_button)).perform(click())

        onView(withText(R.string.app_name)).check(matches(isDisplayed()))
    }

    @Test
    fun retrievePasswordTest() {
        MockSetup.setLoginState(true)
        onView(withId(R.id.login_forgotten_password_text)).perform(click())
        onView(withId(R.id.retrieve_pass_email_edit_text)).perform(clearText(),
            typeText("test@gmail.com"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.retrieve_pass_send_button)).perform(click())
        onView(withText(R.string.retrieve_pass_confirmation_text)).check(matches(isDisplayed()))
    }
}