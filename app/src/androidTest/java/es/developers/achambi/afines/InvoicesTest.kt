package es.developers.achambi.afines

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import es.developers.achambi.afines.invoices.ui.Holder
import es.developers.achambi.afines.repositories.model.FirebaseInvoice
import org.junit.Rule
import org.junit.Test

class InvoicesTest: BaseUITest() {
    @get:Rule
    public val customActivityTestRule: TestRule<OverviewActivity> = TestRule(
        OverviewActivity::class.java, this)

    @Test
    fun testInvoice() {
        val list = ArrayList<FirebaseInvoice>()
        list.add(FirebaseInvoice(name = "invoice"))
        MockSetup.setInvoices(list)

        onView(withId(R.id.navigation_menu_invoice)).perform(click())
        onView(withId(R.id.base_search_recycler_view)).
                perform(actionOnItemAtPosition<Holder>(0, click()))
        onView(withText("invoice")).check(matches(isDisplayed()))
    }

    @Test
    fun testInvoicesError() {
        MockSetup.setInvoices(null)

        onView(withId(R.id.navigation_menu_invoice)).perform(click())
        onView(withText(R.string.invoices_overview_error_message)).check(matches(isDisplayed()))
    }
}