package es.developers.achambi.afines

import android.os.Bundle
import android.view.View
import es.developer.achambi.coreframework.threading.CoreError
import es.developer.achambi.coreframework.ui.BaseRequestFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.home.OverviewPresenter
import es.developers.achambi.afines.repositories.model.NotificationType
import kotlinx.android.synthetic.main.overview_card_item_invoices_layout.*
import kotlinx.android.synthetic.main.overview_card_item_personal_layout.*
import kotlinx.android.synthetic.main.overview_fragment_layout.*
import kotlinx.android.synthetic.main.overview_notification_layout.*

class OverviewFragment : BaseRequestFragment(), OverviewScreen {
    private lateinit var presenter: OverviewPresenter
    private lateinit var notificationType: NotificationType

    companion object{
        const val NOTIFICATION_TYPE_SAVED_STATE = "NOTIFICATION_TYPE_SAVED_STATE"
        fun newInstance() : OverviewFragment{
            return OverviewFragment()
        }
    }

    override val layoutResource: Int
        get() = R.layout.overview_fragment_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            notificationType = NotificationType.values()[it.getInt(NOTIFICATION_TYPE_SAVED_STATE)]
        }
        presenter = AfinesApplication.overviewPresenterFactory.build(this, lifecycle)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(NOTIFICATION_TYPE_SAVED_STATE, notificationType.ordinal)
    }

    override fun getLoadingFrame(): Int {
        return R.id.overview_content_layout
    }

    override fun onViewSetup(view: View) {
        presenter.onViewSetup()
        invoices_trimester_card_action_text.setOnClickListener { presenter.navigateToInvoices() }
        personal_card_action_text.setOnClickListener { presenter.navigateToProfile() }
        overview_notification_go_to_button.setOnClickListener {
            presenter.notificationGoToSelected(notificationType) }
    }

    override fun showInvoicesCount(approved: String, sent: String, rejected: String) {
        trimester_card_sent_count_text.text = sent
        trimester_card_approved_count_text.text = approved
        trimester_card_rejected_count_text.text = rejected
    }

    override fun showIbanValue(iban: String) {
        iban_group.visibility = View.VISIBLE
        overview_card_personal.visibility = View.VISIBLE
        personal_card_iban_value_text.text = iban
    }

    override fun showCCCValue(ccc: String) {
        ccc_group.visibility = View.VISIBLE
        overview_card_personal.visibility = View.VISIBLE
        personal_card_ccc_value_text.text = ccc
    }

    override fun showNAFValue(naf: String) {
        naf_group.visibility = View.VISIBLE
        overview_card_personal.visibility = View.VISIBLE
        personal_card_naf_value_text.text = naf
    }

    override fun showNotification(message: String, type: NotificationType) {
        overview_notification_message_text.text = message
        overview_notification_go_to_button.visibility = View.VISIBLE
        notificationType = type
    }

    override fun showLoading() {
        startLoading()
    }

    override fun showLoadingFinished() {
        hideLoading()
    }

    override fun showLoadingFailed(error: CoreError) {
        showError(error)
    }
}

interface OverviewScreen : Screen {
    fun showInvoicesCount(approved: String, sent: String, rejected: String)
    fun showIbanValue(iban: String)
    fun showCCCValue(ccc: String)
    fun showNAFValue(naf: String)
    fun showLoading()
    fun showLoadingFinished()
    fun showLoadingFailed(error: CoreError)
    fun showNotification(message: String, type: NotificationType)
}