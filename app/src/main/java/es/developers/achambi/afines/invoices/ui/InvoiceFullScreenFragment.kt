package es.developers.achambi.afines.invoices.ui

import android.os.Bundle
import android.view.View
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.R

class InvoiceFullScreenFragment: BaseFragment(), InvoiceFullScreenInterface {
    companion object {
        const val INVOICE_EXTRA_KEY = "INVOICE_EXTRA_KEY"
        private const val INVOICE_ID_EXTRA = "invoice_id_extra"
        fun newInstance(invoiceId: Long): InvoiceFullScreenFragment {
            val fragment = InvoiceFullScreenFragment()
            val bundle = Bundle()
            bundle.putLong(INVOICE_ID_EXTRA, invoiceId)
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun onViewSetup(view: View) {

    }

    override val layoutResource: Int
        get() = R.layout.invoice_full_screen_layout
}

interface InvoiceFullScreenInterface : Screen