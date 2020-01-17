package es.developers.achambi.afines.invoices.presenter

import android.net.Uri
import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.Error
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.ui.InvoiceFullScreenInterface
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase

class InvoiceFullScreenPresenter(screenInterface: InvoiceFullScreenInterface,
                                 lifecycle: Lifecycle,
                                 executor: MainExecutor,
                                 private val useCase: InvoiceUseCase)
    : Presenter<InvoiceFullScreenInterface>(screenInterface, lifecycle, executor) {

    fun downloadImage(id: Long) {
        val handler = object : ResponseHandler<Uri?> {
            override fun onSuccess(response: Uri?) {
                response?.let {
                    screen.showImage(response)
                }
            }
        }
        val resp = object : Request<Uri?> {
            override fun perform(): Uri? {
                return useCase.getDownloadUrl(id)
            }
        }
        request(resp, handler)
    }
}