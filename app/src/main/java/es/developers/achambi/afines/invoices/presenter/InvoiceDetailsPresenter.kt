package es.developers.achambi.afines.invoices.presenter

import android.net.Uri
import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.invoices.model.DetailedInvoice
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.ui.InvoiceDetailsPresentationBuilder
import es.developers.achambi.afines.invoices.ui.InvoiceDetailsScreen
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase

class InvoiceDetailsPresenter(invoiceDetailsScreen: InvoiceDetailsScreen,
                              lifecycle: Lifecycle,
                              executor: MainExecutor,
                              private val useCase: InvoiceUseCase,
                              private val presentationBuilder: InvoiceDetailsPresentationBuilder)
    : Presenter<InvoiceDetailsScreen>(invoiceDetailsScreen, lifecycle, executor) {

    fun onViewCreated(invoiceId: Int) {
        val responseHandler = object : ResponseHandler<Invoice?> {
            override fun onSuccess(response: Invoice?) {
                if(response != null) {
                    screen.showInvoice(presentationBuilder.build(response))
                }
            }
        }
        val request = object : Request<Invoice?> {
            override fun perform(): Invoice? {
                return useCase.getInvoice(invoiceId)
            }
        }
        request(request, responseHandler)
    }

    fun onUserDownloadClicked(invoiceId: Int) {
        val responseHandler = object : ResponseHandler<DetailedInvoice?> {
            override fun onSuccess(response: DetailedInvoice?) {
                if(response != null) {
                    screen.createFile(response.mimeType, response.fileName)
                }
            }
        }
        val request = object : Request<DetailedInvoice?> {
            override fun perform(): DetailedInvoice? {
                return useCase.getDetailedInvoice(invoiceId)
            }
        }
        request(request, responseHandler)
    }

    fun onUserFileBytesRequired(invoiceId: Int, uri: Uri?) {
        val responseHandler = object : ResponseHandler<ByteArray?> {
            override fun onSuccess(response: ByteArray?) {
                if(response != null) {
                    screen.populateFile(uri, response)
                }
            }
        }
        val request = object : Request<ByteArray?> {
            override fun perform(): ByteArray? {
                return useCase.getFileBytes(invoiceId)
            }
        }
        request(request, responseHandler)
    }
}