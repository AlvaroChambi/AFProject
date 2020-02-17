package es.developers.achambi.afines

import android.view.View
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developers.achambi.afines.invoices.ui.UploadInvoiceActivity
import kotlinx.android.synthetic.main.cards_layout_fragment.*

class TestInvoiceFragment: BaseFragment() {
    override fun onViewSetup(view: View) {
        floatingActionButton2.setOnClickListener {
            startActivity(activity?.let { it1 -> UploadInvoiceActivity.getStartIntent(it1) })
        }
    }

    override val layoutResource: Int
        get() =  R.layout.cards_layout_fragment
}