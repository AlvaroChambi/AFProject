package es.developers.achambi.afines.invoices.ui

import android.net.Uri
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload

interface UploadScreenInterface : Screen {
    fun onURIUpdated(uri: Uri?, fileName: String)
    fun onInvoicePreparedToSave(invoiceUpload: InvoiceUpload)
    fun onCannotSaveInvoice()

    fun onInvoicePreparedToEdit(invoiceUpload: InvoiceUpload)

    fun showScreenProgress()
    fun showScreenProgressFinished()

    fun showEditableInvoice(invoice: InvoiceUploadPresentation)
    fun showErrorRetrievingInvoice()

    fun showPhotoCaptureError()
    fun showCamera(uri: Uri)
    fun onPhotoUriCreated(uri: Uri)
    fun showScannerScreen(uri: Uri)
}