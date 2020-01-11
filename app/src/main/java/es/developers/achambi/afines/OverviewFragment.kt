package es.developers.achambi.afines

import android.os.Bundle
import android.view.View
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.home.OverviewPresenter
import kotlinx.android.synthetic.main.notification_item_layout.*

class OverviewFragment : BaseFragment(), NotificationsScreen {
    private lateinit var presenter: OverviewPresenter

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

    override fun onViewSetup(view: View) {
        activity?.setTitle(R.string.app_name)
        presenter.onViewSetup()
        card_view_action_button.setOnClickListener { presenter.navigateToProfile() }
    }

    override fun showUpdatePasswordNotification() {
        update_password_frame.visibility = View.VISIBLE
    }
}

interface NotificationsScreen : Screen {
    fun showUpdatePasswordNotification()
}