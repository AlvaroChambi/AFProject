package es.developers.achambi.afines.invoices.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.developers.achambi.afines.R

class InvoiceBottomSheetFragment : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.invoice_details_botton_sheet, container, false)

    companion object {
        const val INVOICE_ID_EXTRA = "invoice_id_extra"
        fun newInstance(invoiceId: Int): InvoiceBottomSheetFragment{
            val fragment = InvoiceBottomSheetFragment()
            fragment.arguments?.putInt(INVOICE_ID_EXTRA, invoiceId)
            return fragment
        }
    }
}