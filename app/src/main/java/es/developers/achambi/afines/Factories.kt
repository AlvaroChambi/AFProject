package es.developers.achambi.afines

import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.utils.URIUtils
import es.developers.achambi.afines.home.NotificationsPresenter
import es.developers.achambi.afines.home.NotificationsUseCase
import es.developers.achambi.afines.invoices.presenter.*
import es.developers.achambi.afines.invoices.ui.*
import es.developers.achambi.afines.invoices.ui.profile.ProfileScreenInterface
import es.developers.achambi.afines.invoices.ui.profile.presentations.ProfilePresentationBuilder
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase
import es.developers.achambi.afines.invoices.usecase.ProfileUseCase

class InvoicePresenterFactory(private val executor: MainExecutor,
                              private val invoiceUseCase: InvoiceUseCase,
                              private val presentationBuilder: InvoicePresentationBuilder) {
    fun build( invoicesScreenInterface: InvoicesScreenInterface, lifecycle: Lifecycle ): InvoicePresenter {
        return InvoicePresenter( invoicesScreenInterface, lifecycle, executor, invoiceUseCase, presentationBuilder )
    }
}

class InvoiceDetailsPresenterFactory(private val executor: MainExecutor,
                                     private val invoiceUseCase: InvoiceUseCase,
                                     private val presentationBuilder: InvoiceDetailsPresentationBuilder) {
    fun build(invoiceDetailsScreen: InvoiceDetailsScreen, lifecycle: Lifecycle): InvoiceDetailsPresenter {
        return InvoiceDetailsPresenter(invoiceDetailsScreen, lifecycle, executor, invoiceUseCase, presentationBuilder)
    }
}

class InvoiceUploadPresenterFactory(private val executor: MainExecutor,
                                    private val invoiceUseCase: InvoiceUseCase,
                                    private val presentationBuilder: InvoiceUploadPresentationBuilder,
                                    private val uriUtils: URIUtils) {
    fun build(uploadScreenInterface: UploadScreenInterface, lifecycle: Lifecycle): UploadPresenter {
        return UploadPresenter(uploadScreenInterface, lifecycle, executor, uriUtils, invoiceUseCase, presentationBuilder)
    }
}

class ProfilePresenterFactory(private val executor: MainExecutor,
                              private val useCase: ProfileUseCase,
                              private val presentationBuilder: ProfilePresentationBuilder) {
    fun build(screen: ProfileScreenInterface, lifecycle: Lifecycle): ProfilePresenter {
        return ProfilePresenter(screen, lifecycle, executor, useCase, presentationBuilder)
    }
}

class NotificationsPresenterFactory(private val executor: MainExecutor,
                                    private val useCase: NotificationsUseCase){
    fun build(screen: NotificationsScreen, lifecycle: Lifecycle): NotificationsPresenter {
        return NotificationsPresenter(screen, lifecycle, executor, useCase)
    }
}