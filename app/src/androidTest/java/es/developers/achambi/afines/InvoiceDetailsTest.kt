package es.developers.achambi.afines

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import es.developer.achambi.coreframework.ui.BaseActivity
import es.developers.achambi.afines.invoices.ui.InvoiceFullScreenActivity
import es.developers.achambi.afines.invoices.ui.InvoiceFullScreenFragment
import es.developers.achambi.afines.repositories.model.FirebaseInvoice
import es.developers.achambi.afines.repositories.model.InvoiceState
import org.hamcrest.core.IsNot.not
import org.junit.Rule
import org.junit.Test

class InvoiceDetailsTest: BaseTest() {
    @get:Rule
    public val customActivityTestRule: TestRule<InvoiceFullScreenActivity> = TestRule(
        InvoiceFullScreenActivity::class.java, this)

    override fun provideActivityIntent(): Intent {
        return Intent().putExtra(BaseActivity.BASE_ARGUMENTS,
            InvoiceFullScreenFragment.getArguments(1, "invoice"))
    }

    @Test
    fun testPendingInvoice() {
        val list = ArrayList<FirebaseInvoice>()
        list.add(FirebaseInvoice(id = 1, name = "pending_invoice", state = InvoiceState.SENT.toString()))
        MockSetup.setInvoices(list)

        onView(withId(R.id.action_more_options)).perform(click())

        onView(withText("pending_invoice")).check(matches(isDisplayed()))
        onView(withId(R.id.download_invoice_button)).check(matches(isDisplayed()))
        onView(withId(R.id.delete_invoice_button)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_invoice_button)).check(matches(isDisplayed()))
    }
}

class AcceptedInvoiceTest: BaseTest() {
    @get:Rule
    public val customActivityTestRule: TestRule<InvoiceFullScreenActivity> = TestRule(
        InvoiceFullScreenActivity::class.java, this)

    override fun provideActivityIntent(): Intent {
        return Intent().putExtra(BaseActivity.BASE_ARGUMENTS,
            InvoiceFullScreenFragment.getArguments(2, "invoice"))
    }

    @Test
    fun testApprovedInvoice() {
        val list = ArrayList<FirebaseInvoice>()
        list.add(FirebaseInvoice(id = 2, name = "accepted_invoice", state = InvoiceState.ACCEPTED.toString()))
        MockSetup.setInvoices(list)

        onView(withId(R.id.action_more_options)).perform(click())

        onView(withText("accepted_invoice")).check(matches(isDisplayed()))
        onView(withId(R.id.download_invoice_button)).check(matches(isDisplayed()))
        onView(withId(R.id.delete_invoice_button)).check(matches(not(isDisplayed())))
        onView(withId(R.id.edit_invoice_button)).check(matches(not(isDisplayed())))
    }
}

class RejectedInvoiceTest: BaseTest() {
    @get:Rule
    public val customActivityTestRule: TestRule<InvoiceFullScreenActivity> = TestRule(
        InvoiceFullScreenActivity::class.java, this)

    override fun provideActivityIntent(): Intent {
        return Intent().putExtra(BaseActivity.BASE_ARGUMENTS,
            InvoiceFullScreenFragment.getArguments(3, "invoice"))
    }

    @Test
    fun testRejectedInvoice() {
        val list = ArrayList<FirebaseInvoice>()
        list.add(FirebaseInvoice(id = 3, name = "rejected_invoice", state = InvoiceState.REJECTED.toString()))
        MockSetup.setInvoices(list)

        onView(withId(R.id.action_more_options)).perform(click())

        onView(withText("rejected_invoice")).check(matches(isDisplayed()))
        onView(withId(R.id.download_invoice_button)).check(matches(not(isDisplayed())))
        onView(withId(R.id.delete_invoice_button)).check(matches(not(isDisplayed())))
        onView(withId(R.id.edit_invoice_button)).check(matches(isDisplayed()))
    }
}