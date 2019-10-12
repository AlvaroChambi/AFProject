package es.developers.achambi.afines.invoices.presenter

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.Error
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.invoices.model.DetailedInvoice
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.ui.InvoiceDetailsPresentationBuilder
import es.developers.achambi.afines.invoices.ui.InvoiceDetailsScreen
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase
import java.io.IOException

class InvoiceDetailsPresenter(invoiceDetailsScreen: InvoiceDetailsScreen,
                              lifecycle: Lifecycle,
                              executor: MainExecutor,
                              private val useCase: InvoiceUseCase,
                              private val presentationBuilder: InvoiceDetailsPresentationBuilder)
    : Presenter<InvoiceDetailsScreen>(invoiceDetailsScreen, lifecycle, executor) {

    fun onViewCreated(invoiceId: Long) {
        screen.showDetailsLoading()
        val responseHandler = object : ResponseHandler<Invoice?> {
            override fun onSuccess(response: Invoice?) {
                if(response != null) {
                    screen.showDetailsLoadingFinished()
                    screen.showInvoice(presentationBuilder.build(response))
                }
            }

            override fun onError(error: Error) {
                super.onError(error)
                screen.showDetailsLoadingFinished()
                screen.showDetailsError(presentationBuilder.buildError())
            }
        }
        val request = object : Request<Invoice?> {
            override fun perform(): Invoice? {
                return useCase.getInvoice(invoiceId)
            }
        }
        request(request, responseHandler)
    }

    fun onUserDownloadClicked(invoiceId: Long) {
        screen.showDownloadInProgress()
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

    fun onUserFileBytesRequired(invoiceId: Long, uri: Uri?, activity: Activity?) {
        val responseHandler = object : ResponseHandler<ByteArray?> {
            override fun onSuccess(response: ByteArray?) {
                if(response != null && uri != null) {
                    val os = activity?.contentResolver?.openOutputStream(uri)
                    try {
                        if (os != null) {
                            os.write(response)
                            os.close()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        screen.showDownloadError()
                    }
                    screen.showDownloadSuccess()
                } else{
                    screen.showDownloadError()
                }
                screen.showDownloadFinished()
            }

            override fun onError(error: Error) {
                super.onError(error)
                screen.showDownloadError()
                screen.showDownloadSuccess()
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