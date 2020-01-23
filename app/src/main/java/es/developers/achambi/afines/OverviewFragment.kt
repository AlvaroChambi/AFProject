package es.developers.achambi.afines

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.home.OverviewPresenter
import kotlinx.android.synthetic.main.notification_item_layout.*

class OverviewFragment : BaseFragment(), NotificationsScreen {
    private lateinit var presenter: OverviewPresenter
    private lateinit var broadcastReceiver: BroadcastReceiver

    companion object{
        fun newInstance() : OverviewFragment{
            return OverviewFragment()
        }
    }

    override val layoutResource: Int
        get() = R.layout.notification_item_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AfinesApplication.overviewPresenterFactory.build(this, lifecycle)
    }

    override fun onStart() {
        super.onStart()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                showRejectInvoicesNotification()
            }
        }
        presenter.registerBroadcast(broadcastReceiver)
    }

    override fun onStop() {
        super.onStop()
        presenter.unregisterBroadcast(broadcastReceiver)
    }

    override fun onViewSetup(view: View) {
        activity?.setTitle(R.string.app_name)
        presenter.onViewSetup()
        card_view_action_button.setOnClickListener { presenter.navigateToProfile() }
        rejected_invoice_action_button.setOnClickListener { presenter.navigateToInvoices() }
    }

    override fun showUpdatePasswordNotification() {
        update_password_frame.visibility = View.VISIBLE
    }

    override fun showRejectInvoicesNotification() {
        rejected_invoices_frame.visibility = View.VISIBLE
    }
}

interface NotificationsScreen : Screen {
    fun showUpdatePasswordNotification()
    fun showRejectInvoicesNotification()
}