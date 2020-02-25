package es.developers.achambi.afines

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import es.developers.achambi.afines.home.model.TaxDate
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import es.developers.achambi.afines.repositories.model.InvoiceCounters
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList

class OverviewTest: BaseUITest() {
    @get:Rule
    public val customActivityTestRule: TestRule<OverviewActivity> = TestRule(
        OverviewActivity::class.java, this)

    override fun beforeActivity() {
        super.beforeActivity()
        MockSetup.setCOunters(InvoiceCounters())
        MockSetup.setProfile(FirebaseProfile(iban = "iban", ccc = "ccc", naf = "naf"))
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

    @Test
    fun testOverviewError() {
        onView(withId(R.id.base_request_error_message)).check(matches(isDisplayed()))
    }
}