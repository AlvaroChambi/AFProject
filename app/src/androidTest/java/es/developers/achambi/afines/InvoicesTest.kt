package es.developers.achambi.afines

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import es.developers.achambi.afines.invoices.ui.Holder
import org.junit.Rule
import org.junit.Test

class InvoicesTest: BaseTest() {
    @get:Rule
    public val customActivityTestRule: ActivityTestRule<OverviewActivity> = ActivityTestRule(
        OverviewActivity::class.java
    )

    @Test
    fun testInvoice() {
        onView(withId(R.id.navigation_menu_invoice)).perform(click())
        onView(withId(R.id.base_search_recycler_view)).
                perform(actionOnItemAtPosition<Holder>(0, click()))
        onView(withText("invoice")).check(matches(isDisplayed()))
    }
}