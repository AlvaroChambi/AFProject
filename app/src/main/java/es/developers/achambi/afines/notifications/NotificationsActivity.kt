package es.developers.achambi.afines.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import es.developer.achambi.coreframework.ui.BaseActivity
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developers.achambi.afines.R

class NotificationsActivity: BaseActivity() {
    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, NotificationsActivity::class.java)
        }
    }
    override fun getFragment(args: Bundle?): BaseFragment {
        return NotificationsFragment.newInstance()
    }

    override fun getScreenTitle(): Int {
        return R.string.overview_notification_header_text
    }
}