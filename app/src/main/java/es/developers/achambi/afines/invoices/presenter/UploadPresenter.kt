package es.developers.achambi.afines.invoices.presenter

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.Error
import es.developer.achambi.coreframework.threading.ExecutorInterface
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developer.achambi.coreframework.utils.DateFormatUtils
import es.developer.achambi.coreframework.utils.URIUtils
import es.developers.achambi.afines.BuildConfig
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.ui.InvoiceUploadPresentationBuilder
import es.developers.achambi.afines.invoices.ui.Trimester
import es.developers.achambi.afines.invoices.ui.UploadScreenInterface
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase
import java.io.File
import java.io.IOException
import java.util.*

class UploadPresenter(screenInterface: UploadScreenInterface,
                      lifecycle : Lifecycle,
                      executor: ExecutorInterface,
                      private val uriUtils: URIUtils,
                      private val invoiceUseCase: InvoiceUseCase,
                      private val invoiceUploadPresentationBuilder: InvoiceUploadPresentationBuilder)
    : Presenter<UploadScreenInterface>(screenInterface, lifecycle, executor) {

    fun onDataSetup(invoiceId: Long?) {
        if(invoiceId != null) {
            screen.showScreenProgress()
            val responseHandler = object: ResponseHandler<Invoice?> {
                override fun onSuccess(response: Invoice?) {
                    screen.showScreenProgressFinished()
                    response?.let { invoiceUploadPresentationBuilder.build(it) }?.let { screen.showEditableInvoice(it) }
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    screen.showScreenProgressFinished()
                    screen.showErrorRetrievingInvoice()
                }
            }
            val request = object : Request<Invoice?> {
                override fun perform(): Invoice? {
                    return invoiceUseCase.getInvoice(invoiceId)
                }
            }
            request(request, responseHandler)
        }
    }

    fun userSelectedURI(context: Context, uri: Uri) {
        val uriMetadata =  uriUtils.retrieveFileMetadata(context, uri)
        val displayName = uriMetadata.displayName
        if(displayName != null) {
            screen.onURIUpdated(uri, displayName)
        } else {
            screen.onURIUpdated(uri, "")
        }
    }

    fun userClearedURI() {
        screen.onURIUpdated(null, "")
    }

    fun userSaveSelected(context: Context, uri: Uri?, name: String, trimester: Trimester) {
        if( uri != null ) {
            val invoiceUpload = InvoiceUpload(
                uriUtils.retrieveFileMetadata(context, uri),
                name, trimester
            )
            screen.onInvoicePreparedToSave(invoiceUpload)
        } else {
            screen.onCannotSaveInvoice()
        }
    }

    fun userOverrideSelected(context: Context, uri: Uri?, name: String, trimester: Trimester) {
        val invoiceUpload = InvoiceUpload(uriUtils.retrieveFileMetadata(context, uri),
            name, trimester)
        screen.onInvoicePreparedToEdit(invoiceUpload)
    }

    fun userPhotoFileRequested(context: Context) {

        try {
            // Create an image file name
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile(
                "FACTURA_${DateFormatUtils.formatDateDetailed(Date())}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            )

            val photoUri = FileProvider.getUriForFile(context,BuildConfig.FILE_PROVIDER,
                file)
            screen.onPhotoUriCreated(photoUri)
            screen.showCamera(photoUri)
        } catch (e: IOException) {
            screen.showPhotoCaptureError()
        }
    }
}