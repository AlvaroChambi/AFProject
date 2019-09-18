package es.developers.achambi.afines

import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developers.achambi.afines.invoices.presenter.InvoicePresenter
import es.developers.achambi.afines.invoices.ui.InvoicePresentationBuilder
import es.developers.achambi.afines.invoices.ui.InvoicesScreenInterface
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase

class InvoicePresenterFactory(private val executor: MainExecutor,
                              private val invoiceUseCase: InvoiceUseCase,
                              private val presentationBuilder: InvoicePresentationBuilder) {
    fun build( invoicesScreenInterface: InvoicesScreenInterface, lifecycle: Lifecycle ): InvoicePresenter {
        return InvoicePresenter( invoicesScreenInterface, lifecycle, executor, invoiceUseCase, presentationBuilder )
    }
}