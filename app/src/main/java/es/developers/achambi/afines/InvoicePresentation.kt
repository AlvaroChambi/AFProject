package es.developers.achambi.afines

import android.content.Context
import es.developer.achambi.coreframework.ui.presentation.SearchListData

data class InvoicePresentation(val id: Int,
    val name: String): SearchListData {
    override fun getId(): Long {
        return id.toLong()
    }
}
class InvoicePresentationBuilder(private val context: Context) {
    private fun build(invoice: Invoice): InvoicePresentation {
        return InvoicePresentation( invoice.id, invoice.name )
    }

    fun build(invoices: ArrayList<Invoice>): ArrayList<InvoicePresentation> {
        val result = ArrayList<InvoicePresentation>()
        invoices.forEach{
            result.add( build(it) )
        }
        return result
    }
}