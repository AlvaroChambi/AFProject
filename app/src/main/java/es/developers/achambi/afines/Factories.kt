package es.developers.achambi.afines

import androidx.lifecycle.Lifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.utils.URIUtils
import es.developers.achambi.afines.home.NotificationsPresenter
import es.developers.achambi.afines.home.NotificationsUseCase
import es.developers.achambi.afines.invoices.presenter.*
import es.developers.achambi.afines.invoices.ui.*
import es.developers.achambi.afines.profile.ui.ProfileScreenInterface
import es.developers.achambi.afines.profile.ui.presentations.ProfilePresentationBuilder
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase
import es.developers.achambi.afines.login.LoginPresenter
import es.developers.achambi.afines.login.LoginScreenInterface
import es.developers.achambi.afines.login.RetrievePasswordPresenter
import es.developers.achambi.afines.login.RetrievePasswordScreen
import es.developers.achambi.afines.login.usecase.LoginUseCase
import es.developers.achambi.afines.profile.presenter.ProfilePresenter
import es.developers.achambi.afines.profile.presenter.UpdatePasswordPresenter
import es.developers.achambi.afines.profile.ui.UpdatePasswordScreen
import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.services.NotificationServicePresenter

class InvoicePresenterFactory(private val executor: MainExecutor,
                              private val invoiceUseCase: InvoiceUseCase,
                              private val presentationBuilder: InvoicePresentationBuilder,
                              private val localBroadcastManager: LocalBroadcastManager) {
    fun build( invoicesScreenInterface: InvoicesScreenInterface, lifecycle: Lifecycle ): InvoicePresenter {
        return InvoicePresenter( invoicesScreenInterface, lifecycle, executor, invoiceUseCase,
            presentationBuilder, localBroadcastManager )
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
        return ProfilePresenter(
            screen,
            lifecycle,
            executor,
            useCase,
            presentationBuilder
        )
    }
}

class NotificationsPresenterFactory(private val executor: MainExecutor,
                                    private val useCase: NotificationsUseCase){
    fun build(screen: NotificationsScreen, lifecycle: Lifecycle): NotificationsPresenter {
        return NotificationsPresenter(screen, lifecycle, executor, useCase)
    }
}

class UpdatePasswordPresenterFactory(private val executor: MainExecutor, private val useCase: ProfileUseCase) {
    fun build(screen: UpdatePasswordScreen, lifecycle: Lifecycle): UpdatePasswordPresenter {
        return UpdatePasswordPresenter(screen, lifecycle, executor, useCase)
    }
}

class LoginPresenterFactory(private val executor: MainExecutor, private val useCase: LoginUseCase) {
    fun build(screen: LoginScreenInterface, lifecycle: Lifecycle): LoginPresenter {
        return LoginPresenter(screen, lifecycle, executor, useCase)
    }
}

class RetrievePasswordPresenterFactory(private val executor: MainExecutor, private val useCase: LoginUseCase) {
    fun build(screen: RetrievePasswordScreen, lifecycle: Lifecycle): RetrievePasswordPresenter {
        return RetrievePasswordPresenter(screen, lifecycle, executor, useCase)
    }
}

class MessagingServicePresenterFactory(private val executor: MainExecutor,
                                       private val useCase: ProfileUseCase,
                                       private val broadcastManager: LocalBroadcastManager) {
    fun build(): NotificationServicePresenter {
        return NotificationServicePresenter(executor, useCase, broadcastManager)
    }
}