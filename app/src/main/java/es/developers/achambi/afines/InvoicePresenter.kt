package es.developers.achambi.afines

import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.ExecutorInterface
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter

class InvoicePresenter(screenInterface: InvoicesScreenInterface,
                       lifecycle : Lifecycle,
                       executor: ExecutorInterface,
                       private val invoicePresentationBuilder: InvoicePresentationBuilder)
    : Presenter<InvoicesScreenInterface>(screenInterface,lifecycle,executor){
    private val invoiceUseCase = InvoiceUseCase()

    fun showInvoices() {
        val responseHandler = object: ResponseHandler<ArrayList<Invoice>> {
            override fun onSuccess(response: ArrayList<Invoice>) {
                screen.onInvoicesRetrieved( invoicePresentationBuilder.build(response) )
            }
        }
        val request = object : Request<ArrayList<Invoice>>{
            override fun perform(): ArrayList<Invoice> {
                return invoiceUseCase.queryUserInvoices()
            }

        }
        request(request , responseHandler)
    }
}