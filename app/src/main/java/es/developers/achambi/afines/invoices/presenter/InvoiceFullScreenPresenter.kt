package es.developers.achambi.afines.invoices.presenter

import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.invoices.ui.InvoiceFullScreenInterface
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase

class InvoiceFullScreenPresenter(screenInterface: InvoiceFullScreenInterface,
                                 lifecycle: Lifecycle,
                                 executor: MainExecutor,
                                 private val useCase: InvoiceUseCase)
    : Presenter<InvoiceFullScreenInterface>(screenInterface, lifecycle, executor) {

}