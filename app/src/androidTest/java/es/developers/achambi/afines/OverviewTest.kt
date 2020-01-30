package es.developers.achambi.afines

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import es.developers.achambi.afines.home.model.TaxDate
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList

class OverviewTest: BaseTest() {
    @get:Rule
    public val customActivityTestRule: TestRule<OverviewActivity> = TestRule(
        OverviewActivity::class.java, this
    )

    override fun beforeActivity() {
        super.beforeActivity()
        val list = ArrayList<TaxDate>()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        list.add(TaxDate(date = calendar.time))
        MockSetup.setTaxDates(list)

        MockSetup.setProfile(FirebaseProfile(rejected = 1))
    }

    @Test
    fun testRejectedNotification() {
        onView(withId(R.id.rejected_invoice_action_button)).perform(click())
        onView(withText(R.string.invoices_screen_title)).check(matches(isDisplayed()))
    }

    @Test
    fun testPasswordNotification() {
        onView(withId(R.id.password_notification_action_button)).perform(click())
        onView(withId(R.id.profile_user_name_text)).check(matches(isDisplayed()))
    }

    @Test
    fun testTaxDates() {
        onView(withId(R.id.overview_tax_dates_header))
        onView(withId(R.id.taxes_recycler_view)).check(matches(isDisplayed()))
    }
}

class OverviewErrorTest: BaseTest() {
    @get:Rule
    public val customActivityTestRule: TestRule<OverviewActivity> = TestRule(
        OverviewActivity::class.java, this
    )

    override fun beforeActivity() {
        super.beforeActivity()
        MockSetup.setTaxDates(null)
    }

    @Test
    fun testTaxDatesError() {
        onView(withId(R.id.overview_tax_dates_header)).check(matches(not(isDisplayed())))
        onView(withId(R.id.taxes_recycler_view)).check(matches(not(isDisplayed())))
    }
}