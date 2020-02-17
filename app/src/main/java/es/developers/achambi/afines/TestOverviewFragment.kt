package es.developers.achambi.afines

import android.view.View
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developers.achambi.afines.invoices.ui.UploadInvoiceActivity
import kotlinx.android.synthetic.main.test_overview_layout.*

class TestOverviewFragment: BaseFragment() {
    override fun onViewSetup(view: View) {
        floatingActionButton3.setOnClickListener {
            startActivity(activity?.let { it1 -> UploadInvoiceActivity.getStartIntent(it1) })
        }
    }

    override val layoutResource: Int
        get() = R.layout.test_overview_layout
}