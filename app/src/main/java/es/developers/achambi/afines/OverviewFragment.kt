package es.developers.achambi.afines

import android.os.Bundle
import android.view.View
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.home.OverviewPresenter
import kotlinx.android.synthetic.main.overview_card_item_invoices_layout.*
import kotlinx.android.synthetic.main.overview_card_item_personal_layout.*

class OverviewFragment : BaseFragment(), OverviewScreen {
    private lateinit var presenter: OverviewPresenter

    companion object{
        fun newInstance() : OverviewFragment{
            return OverviewFragment()
        }
    }

    override val layoutResource: Int
        get() = R.layout.overview_fragment_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AfinesApplication.overviewPresenterFactory.build(this, lifecycle)
    }

    override fun onViewSetup(view: View) {
        presenter.onViewSetup()
        invoices_trimester_card_action_text.setOnClickListener { presenter.navigateToInvoices() }
        personal_card_action_text.setOnClickListener { presenter.navigateToProfile() }
    }

    override fun showInvoicesCount(approved: String, sent: String, rejected: String) {
        trimester_card_sent_count_text.text = sent
        trimester_card_approved_count_text.text = approved
        trimester_card_rejected_count_text.text = rejected
    }

    override fun showIbanValue(iban: String) {
        iban_group.visibility = View.VISIBLE
        personal_card_iban_value_text.text = iban
    }

    override fun showCCCValue(ccc: String) {
        ccc_group.visibility = View.VISIBLE
        personal_card_ccc_value_text.text = ccc
    }

    override fun showNAFValue(naf: String) {
        naf_group.visibility = View.VISIBLE
        personal_card_naf_value_text.text = naf
    }
}

interface OverviewScreen : Screen {
    fun showInvoicesCount(approved: String, sent: String, rejected: String)
    fun showIbanValue(iban: String)
    fun showCCCValue(ccc: String)
    fun showNAFValue(naf: String)
}