package es.developers.achambi.afines

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginTest {
    private lateinit var idlingResource : IdlingResource
    @get:Rule
    public val customActivityTestRule: ActivityTestRule<LoginActivity> = ActivityTestRule(
        LoginActivity::class.java
    )

    @Before
    fun setup() {
        idlingResource = ExecutorIdlingResource(AfinesApplication.executor)
        Espresso.registerIdlingResources(idlingResource)
    }

    @Test
    fun test() {
        onView(withId(R.id.login_email_edit_text)).perform(
            clearText(), typeText("test@gmail.com"))
        onView(withId(R.id.login_pass_edit_text)).perform(
            clearText(), typeText("0123456"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.login_button)).perform(click())

        onView(withText(R.string.app_name)).check(matches(isDisplayed()))
    }
}