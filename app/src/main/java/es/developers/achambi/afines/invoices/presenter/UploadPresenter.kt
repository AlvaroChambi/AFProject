package es.developers.achambi.afines.invoices.presenter

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.CoreError
import es.developer.achambi.coreframework.threading.ExecutorInterface
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.utils.DateFormatUtils
import es.developer.achambi.coreframework.utils.URIUtils
import es.developers.achambi.afines.BuildConfig
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.ui.InvoiceUploadPresentationBuilder
import es.developers.achambi.afines.invoices.ui.UploadScreenInterface
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase
import es.developers.achambi.afines.utils.EventLogger
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.util.*

class UploadPresenter(screenInterface: UploadScreenInterface,
                      lifecycle : Lifecycle,
                      executor: ExecutorInterface,
                      private val uriUtils: URIUtils,
                      private val invoiceUseCase: InvoiceUseCase,
                      private val invoiceUploadPresentationBuilder: InvoiceUploadPresentationBuilder,
                      private val analytics: EventLogger)
    : Presenter<UploadScreenInterface>(screenInterface, lifecycle, executor) {

    fun onDataSetup(invoiceId: Long?) {
        if(invoiceId != null && invoiceId != 0L) {
            screen.showScreenProgress()
            val responseHandler = object: ResponseHandler<Invoice?> {
                override fun onSuccess(response: Invoice?) {
                    screen.showScreenProgressFinished()
                    response?.let { invoiceUploadPresentationBuilder.build(it) }?.
                        let { screen.showEditableInvoice(it) }
                }

                override fun onError(error: CoreError) {
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

            val handler = object : ResponseHandler<Uri?> {
                override fun onSuccess(response: Uri?) {
                    response?.let {
                        screen.showInvoiceUriImage(response)
                    }
                }
            }
            val resp = object : Request<Uri?> {
                override fun perform(): Uri? {
                    return invoiceUseCase.getDownloadUrl(invoiceId)
                }
            }
            request(resp, handler)
        }
    }

    fun userSelectedGallery() {
        analytics.publishGallerySelected()
        screen.showGallery()
    }

    fun userSelectedURI(context: Context, uri: Uri) {
        val uriMetadata =  uriUtils.retrieveFileMetadata(context, uri)
        val displayName = uriMetadata.displayName
        if(displayName != null) {
            screen.onURIUpdated(uri, displayName)
        } else {
            screen.onURIUpdated(uri, "")
        }

        //TODO Check in performance snapshots the cost of decoding in the main thread
        try {
            screen.showInvoiceBitmap(MediaStore.Images.Media.getBitmap(context.contentResolver, uri))
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun userSaveSelected(context: Context, uri: Uri?, name: String) {
        if( uri != null && name.isNotEmpty() ) {
            val invoiceUpload = InvoiceUpload(
                uriUtils.retrieveFileMetadata(context, uri),
                name)
            screen.onInvoicePreparedToSave(invoiceUpload)
        } else {
            screen.onCannotSaveInvoice()
        }
    }

    fun userOverrideSelected(context: Context, uri: Uri?, name: String) {
        if(name.isNotEmpty()) {
            //Uri will be ignored on the update if  it's null
            val invoiceUpload = InvoiceUpload(uriUtils.retrieveFileMetadata(context, uri),
                name)
            screen.onInvoicePreparedToEdit(invoiceUpload)
        } else {
            screen.onCannotSaveInvoice()
        }
    }

    fun userPhotoFileRequested(context: Context) {
        analytics.publishScannerSelected()
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