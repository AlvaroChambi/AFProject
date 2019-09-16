package es.developers.achambi.afines.invoices.ui

import es.developer.achambi.coreframework.ui.Screen

interface InvoicesScreenInterface: Screen{
    fun onInvoicesRetrieved(invoices: ArrayList<InvoicePresentation>)
    fun onInvoiceUploaded()

    fun onInvoicesLoadingError()
    fun onUploadError()

    fun startLoadingInvoices()
    fun startUploadingInvoice()
    fun finishedLoadingInvoices()
    fun finishedUploadingInvoice()
}