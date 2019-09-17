package es.developers.achambi.afines.invoices.ui

import android.net.Uri
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.invoices.model.InvoiceUpload

interface UploadScreenInterface : Screen {
    fun onURIUpdated(uri: Uri?, fileName: String)
    fun onInvoicePreparedToSave(invoiceUpload: InvoiceUpload)
    fun onCannotSaveInvoice()
}