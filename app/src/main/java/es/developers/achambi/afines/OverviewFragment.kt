package es.developers.achambi.afines

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.home.NotificationsPresenter
import kotlinx.android.synthetic.main.notification_item_layout.*

class OverviewFragment : BaseFragment(), NotificationsScreen {
    private lateinit var presenter: NotificationsPresenter

    companion object{
        fun newInstance() : OverviewFragment{
            return OverviewFragment()
        }
    }

    override val layoutResource: Int
        get() = R.layout.notification_item_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AfinesApplication.notificationsPresenterFactory.build(this, lifecycle)
    }

    override fun onViewSetup(view: View) {
        presenter.onViewSetup()
        card_view_action_button.setOnClickListener {
            activity?.let { it1 ->
                LocalBroadcastManager.getInstance(it1).sendBroadcast(Intent("NAVIGATE")) }
        }
    }

    override fun showUpdatePasswordNotification() {
        update_password_frame.visibility = View.VISIBLE
    }
}

interface NotificationsScreen : Screen {
    fun showUpdatePasswordNotification()
}