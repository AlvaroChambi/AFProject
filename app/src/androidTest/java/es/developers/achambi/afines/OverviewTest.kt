package es.developers.achambi.afines

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class OverviewTest: BaseTest() {
    @get:Rule
    public val customActivityTestRule: ActivityTestRule<OverviewActivity> = ActivityTestRule(
        OverviewActivity::class.java
    )

    @Test
    fun testRejectedNotification() {
        onView(withId(R.id.overview_tax_dates_header))
        onView(withId(R.id.taxes_recycler_view)).check(matches(isDisplayed()))
        onView(withId(R.id.rejected_invoice_action_button)).perform(click())
        onView(withText(R.string.invoices_screen_title)).check(matches(isDisplayed()))
    }

    @Test
    fun testPasswordNotification() {
        onView(withId(R.id.overview_tax_dates_header))
        onView(withId(R.id.taxes_recycler_view)).check(matches(isDisplayed()))
        onView(withId(R.id.password_notification_action_button)).perform(click())
        onView(withText(R.string.profile_activity_title)).check(matches(isDisplayed()))
    }
}