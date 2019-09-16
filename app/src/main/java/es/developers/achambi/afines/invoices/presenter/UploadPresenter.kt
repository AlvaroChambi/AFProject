package es.developers.achambi.afines.invoices.presenter

import android.content.Context
import android.net.Uri
import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.ExecutorInterface
import es.developer.achambi.coreframework.ui.Presenter
import es.developer.achambi.coreframework.utils.URIUtils
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.ui.Trimester
import es.developers.achambi.afines.invoices.ui.UploadScreenInterface

class UploadPresenter(screenInterface: UploadScreenInterface,
                      lifecycle : Lifecycle,
                      executor: ExecutorInterface)
    : Presenter<UploadScreenInterface>(screenInterface, lifecycle, executor) {

    fun userSelectedURI(context: Context, uri: Uri) {
        val uriMetadata =  URIUtils.retrieveFileMetadata(context, uri)
        uriMetadata.displayName?.let { screen.onURIUpdated(uri, it) }
    }

    fun userClearedURI() {
        screen.onURIUpdated(null, "")
    }

    fun userSaveSelected(context: Context, uri: Uri?, name: String, trimester: Trimester) {
        if( uri != null ) {
            val invoiceUpload = InvoiceUpload(
                URIUtils.retrieveFileMetadata(context, uri),
                name, trimester
            )
            screen.onInvoicePreparedToSave(invoiceUpload)
        } else {
            screen.onSaveInvoiceFailed()
        }
    }
}