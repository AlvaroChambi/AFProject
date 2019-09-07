package es.developers.achambi.afines

import es.developer.achambi.coreframework.ui.Screen

interface InvoicesScreenInterface: Screen{
    fun onInvoicesRetrieved(invoices: ArrayList<InvoicePresentation>)
}