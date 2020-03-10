package es.developers.achambi.afines

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import org.junit.Rule
import org.junit.Test

class ProfileUITest: BaseUITest() {
    @get:Rule
    public val customActivityTestRule: TestRule<OverviewActivity> = TestRule(
        OverviewActivity::class.java, this)

    override fun setup() {
        super.setup()
        MockSetup.setProfile(FirebaseProfile(userId = "id", name = "User",
            email = "test@gmail.com", iban = "ES9121000418450200051332", rejected = 1))
    }

    @Test
    fun testProfile() {
        onView(withId(R.id.navigation_menu_profile)).perform(click())

        onView(withId(R.id.profile_user_name_text)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.profile_email_edit_frame)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.profile_address_edit_frame)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.profile_dni_edit_frame)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.profile_naf_edit_frame)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.profile_ccc_edit_frame)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.profile_account_edit_frame)).perform(scrollTo()).check(matches(isDisplayed()))
    }

    @Test
    fun testProfileUpdate() {
        onView(withId(R.id.navigation_menu_profile)).perform(click())
        onView(withId(R.id.action_edit_profile)).perform(click())
        onView(withId(R.id.profile_address_edit_text)).perform(clearText(), typeText("addresss"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.action_save_text)).perform(click())

        onView(withId(R.id.action_edit_profile)).check(matches(isDisplayed()))
    }
}