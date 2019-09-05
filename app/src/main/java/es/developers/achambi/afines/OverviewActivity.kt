package es.developers.achambi.afines

import android.os.Bundle
import es.developer.achambi.coreframework.ui.BaseActivity
import es.developer.achambi.coreframework.ui.BaseFragment

class OverviewActivity : BaseActivity() {
    override fun getScreenTitle(): Int {
        return R.string.overview_activity
    }

    override fun getFragment(args: Bundle?): BaseFragment? {
        return OverviewFragment.newInstance()
    }
}
