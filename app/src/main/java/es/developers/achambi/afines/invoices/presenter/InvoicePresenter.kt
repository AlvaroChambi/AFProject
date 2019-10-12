package es.developers.achambi.afines.invoices.presenter

import android.net.Uri
import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.Error
import es.developer.achambi.coreframework.threading.ExecutorInterface
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.ui.InvoicePresentationBuilder
import es.developers.achambi.afines.invoices.ui.InvoicesScreenInterface
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase

class InvoicePresenter(screenInterface: InvoicesScreenInterface,
                       lifecycle : Lifecycle,
                       executor: ExecutorInterface,
                       private val invoiceUseCase: InvoiceUseCase,
                       private val invoicePresentationBuilder: InvoicePresentationBuilder
)
    : Presenter<InvoicesScreenInterface>(screenInterface,lifecycle,executor){

    fun uploadFile(uri: Uri, invoiceUpload: InvoiceUpload) {
        screen.showProgress()
        val responseHandler = object: ResponseHandler<Any> {
            override fun onSuccess(response: Any) {
                screen.showProgressFinished()
                refreshInvoices()
            }

            override fun onError(error: Error) {
                super.onError(error)
                screen.showProgressFinished()
                screen.onUploadError()
            }
        }
        val request = object : Request<Any> {
            override fun perform(): Any {
                return invoiceUseCase.uploadUserFiles(uri, invoiceUpload)
            }
        }
        request(request, responseHandler)
    }

    fun showInvoices() {
        screen.showFullScreenProgress()
        val responseHandler = object: ResponseHandler<ArrayList<Invoice>> {
            override fun onSuccess(response: ArrayList<Invoice>) {
                screen.showFullScreenProgressFinished()
                screen.showInvoices( invoicePresentationBuilder.build(response) )
            }

            override fun onError(error: Error) {
                super.onError(error)
                screen.showFullScreenProgressFinished()
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

    fun deleteRequested(invoiceId: Long) {
        screen.showProgress()
        val responseHandler = object : ResponseHandler<Any> {
            override fun onSuccess(response: Any) {
                screen.showInvoiceDeleted()
                screen.showProgressFinished()
                refreshInvoices()
            }

            override fun onError(error: Error) {
                screen.showProgressFinished()
                screen.showInvoiceDeleteError()
            }
        }
        val request = object : Request<Any> {
            override fun perform(): Any {
                return invoiceUseCase.deleteInvoice(invoiceId)
            }
        }
        request(request, responseHandler)
    }

    fun refreshInvoices() {
        screen.showProgress()
        val responseHandler = object: ResponseHandler<ArrayList<Invoice>> {
            override fun onSuccess(response: ArrayList<Invoice>) {
                screen.showProgressFinished()
                screen.showInvoices( invoicePresentationBuilder.build(response) )
            }

            override fun onError(error: Error) {
                super.onError(error)
                screen.showProgressFinished()
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