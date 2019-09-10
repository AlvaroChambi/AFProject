package es.developers.achambi.afines

import android.content.Context
import android.net.Uri
import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.Error
import es.developer.achambi.coreframework.threading.ExecutorInterface
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developer.achambi.coreframework.utils.URIMetadata
import es.developer.achambi.coreframework.utils.URIUtils

class InvoicePresenter(screenInterface: InvoicesScreenInterface,
                       lifecycle : Lifecycle,
                       executor: ExecutorInterface,
                       private val invoicePresentationBuilder: InvoicePresentationBuilder)
    : Presenter<InvoicesScreenInterface>(screenInterface,lifecycle,executor){
    private val invoiceUseCase = InvoiceUseCase(FirebaseRepository())

    fun uploadFile(context: Context, uri: Uri) {
        val responseHandler = object: ResponseHandler<Any> {
            override fun onSuccess(response: Any) {
                screen.onInvoiceUploaded()
            }

            override fun onError(error: Error) {
                super.onError(error)
            }
        }
        val request = object : Request<Any> {
            override fun perform(): Any {
                return invoiceUseCase.uploadUserFiles(uri, URIUtils.retrieveFileMetadata(context,uri))
            }
        }
        request(request, responseHandler)
    }

    fun showInvoices() {
        val responseHandler = object: ResponseHandler<ArrayList<Invoice>> {
            override fun onSuccess(response: ArrayList<Invoice>) {
                screen.onInvoicesRetrieved( invoicePresentationBuilder.build(response) )
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
}