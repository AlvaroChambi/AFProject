package es.developers.achambi.afines

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import es.developers.achambi.afines.repositories.model.InvoiceCounters
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import kotlin.collections.ArrayList

class OverviewTest: BaseUITest() {
    @get:Rule
    public val customActivityTestRule: TestRule<OverviewActivity> = TestRule(
        OverviewActivity::class.java, this)

    override fun beforeActivity() {
        super.beforeActivity()
        MockSetup.setCOunters(InvoiceCounters())
        MockSetup.setProfile(FirebaseProfile(iban = "iban", ccc = "ccc", naf = "naf"))
        MockSetup.setTaxDates(ArrayList())
    }

    @Test
    fun testOverviewInvoicesDisplayed() {
        onView(withId(R.id.overview_card_invoices)).check(matches(isDisplayed()))
        onView(withId(R.id.overview_card_personal)).check(matches(isDisplayed()))
    }
}

class OverviewEmptyTest: BaseUITest() {
    @get:Rule
    public val customActivityTestRule: TestRule<OverviewActivity> = TestRule(
        OverviewActivity::class.java, this)

    override fun beforeActivity() {
        super.beforeActivity()
        MockSetup.setCOunters(InvoiceCounters())
        MockSetup.setProfile(FirebaseProfile())
        MockSetup.setTaxDates(ArrayList())
    }

    @Test
    fun testOverviewPersonalInfoNotAvailable() {
        onView(withId(R.id.overview_card_invoices)).check(matches(isDisplayed()))
        onView(withId(R.id.overview_card_personal)).check(matches(not(isDisplayed())))
    }
}

class OverviewErrorTest: BaseUITest() {
    @get:Rule
    public val customActivityTestRule: TestRule<OverviewActivity> = TestRule(
        OverviewActivity::class.java, this
    )

    override fun setup() {
        super.setup()
        MockSetup.setProfile(null)
        MockSetup.setCOunters(null)
        MockSetup.setTaxDates(null)
    }

    @Test
    fun testOverviewError() {
        onView(withId(R.id.navigation_menu_home)).perform(click())
        onView(withId(R.id.base_request_error_message)).check(matches(isDisplayed()))
    }
}

class RejectedNotification: BaseUITest() {
    @get:Rule
    public val customActivityTestRule: TestRule<OverviewActivity> = TestRule(
        OverviewActivity::class.java, this
    )

    override fun beforeActivity() {
        super.beforeActivity()
        MockSetup.setCOunters(InvoiceCounters(rejected = 1))
        MockSetup.setProfile(FirebaseProfile())
        MockSetup.setTaxDates(ArrayList())
    }

    @Test
    fun testRejectedNotificationNavigation() {
        onView(withId(R.id.overview_notification_go_to_button)).perform(click())
        onView(withId(R.id.invoices_trimester_header_view)).check(matches(isDisplayed()))
    }
}

class UpdatePassword: BaseUITest() {
    @get:Rule
    public val customActivityTestRule: TestRule<OverviewActivity> = TestRule(
        OverviewActivity::class.java, this
    )

    override fun beforeActivity() {
        super.beforeActivity()
        MockSetup.setCOunters(InvoiceCounters())
        MockSetup.setProfile(FirebaseProfile(passwordChanged = false))
        MockSetup.setTaxDates(ArrayList())
    }

    @Test
    fun updatePassNavigation() {
        onView(withId(R.id.overview_notification_go_to_button)).perform(click())
        onView(withId(R.id.profile_user_name_text)).check(matches(isDisplayed()))
    }
}