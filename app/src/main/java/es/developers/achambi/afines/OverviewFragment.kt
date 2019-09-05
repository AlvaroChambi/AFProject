package es.developers.achambi.afines

import android.view.View
import es.developer.achambi.coreframework.ui.BaseFragment

class OverviewFragment : BaseFragment() {
    override val layoutResource: Int
        get() = R.layout.base_activity

    companion object{
        fun newInstance() : OverviewFragment{
            return OverviewFragment()
        }
    }
    override fun onViewSetup(view: View) {
    }
}
