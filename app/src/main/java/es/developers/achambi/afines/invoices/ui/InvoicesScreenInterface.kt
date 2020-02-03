package es.developers.achambi.afines.invoices.ui

import es.developer.achambi.coreframework.ui.Screen

interface InvoicesScreenInterface: Screen{
    fun showInvoices(invoices: ArrayList<InvoicePresentation>)

    fun showInvoicesLoadingError()
    fun showUploadError()

    fun showFullScreenProgress()
    fun showProgress()
    fun showProgressFinished()
    fun showFullScreenProgressFinished()

    fun showInvoiceDeleted()
    fun showInvoiceDeleteError()

    fun showEditInvoiceSuccess()
    fun showEditInvoiceError()
}