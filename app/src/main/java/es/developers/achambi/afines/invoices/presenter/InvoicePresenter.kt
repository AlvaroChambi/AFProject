package es.developers.achambi.afines.invoices.presenter

import android.net.Uri
import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.Error
import es.developer.achambi.coreframework.threading.ExecutorInterface
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developer.achambi.coreframework.utils.URIMetadata
import es.developers.achambi.afines.FirebaseRepository
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.ui.InvoicePresentationBuilder
import es.developers.achambi.afines.invoices.ui.InvoicesScreenInterface
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase

class InvoicePresenter(screenInterface: InvoicesScreenInterface,
                       lifecycle : Lifecycle,
                       executor: ExecutorInterface,
                       private val invoicePresentationBuilder: InvoicePresentationBuilder
)
    : Presenter<InvoicesScreenInterface>(screenInterface,lifecycle,executor){
    private val invoiceUseCase =
        InvoiceUseCase(FirebaseRepository())

    fun uploadFile(uri: Uri, invoiceUpload: InvoiceUpload) {
        screen.startUploadingInvoice()
        val responseHandler = object: ResponseHandler<Any> {
            override fun onSuccess(response: Any) {
                screen.onInvoiceUploaded()
            }

            override fun onError(error: Error) {
                super.onError(error)
                screen.finishedUploadingInvoice()
                screen.onUploadError()
            }
        }
        val request = object : Request<Any> {
            override fun perform(): Any {
                return invoiceUseCase.uploadUserFiles(uri, resolveFileName(invoiceUpload))
            }
        }
        request(request, responseHandler)
    }

    fun showInvoices() {
        screen.startLoadingInvoices()
        val responseHandler = object: ResponseHandler<ArrayList<Invoice>> {
            override fun onSuccess(response: ArrayList<Invoice>) {
                screen.finishedLoadingInvoices()
                screen.onInvoicesRetrieved( invoicePresentationBuilder.build(response) )
            }

            override fun onError(error: Error) {
                super.onError(error)
                screen.finishedLoadingInvoices()
                screen.onInvoicesLoadingError()
            }
        }
        val request = object : Request<ArrayList<Invoice>>{
            override fun perform(): ArrayList<Invoice> {
                return invoiceUseCase.queryUserInvoices(refresh = false)
            }

        }
        request(request , responseHandler)
    }

    fun invoiceAdded() {
        val responseHandler = object: ResponseHandler<ArrayList<Invoice>> {
            override fun onSuccess(response: ArrayList<Invoice>) {
                screen.finishedUploadingInvoice()
                screen.onInvoicesRetrieved( invoicePresentationBuilder.build(response) )
            }
        }
        val request = object : Request<ArrayList<Invoice>>{
            override fun perform(): ArrayList<Invoice> {
                return invoiceUseCase.queryUserInvoices(refresh = true)
            }

        }
        request(request , responseHandler)
    }

    private fun resolveFileName(invoiceUpload: InvoiceUpload): String {
        return if(invoiceUpload.name.isNotEmpty()) {
            invoiceUpload.name
        } else {
            invoiceUpload.uriMetadata.displayName.toString()
        }
    }
}