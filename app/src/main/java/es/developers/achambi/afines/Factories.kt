package es.developers.achambi.afines

import androidx.lifecycle.Lifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.utils.URIUtils
import es.developers.achambi.afines.home.OverviewPresenter
import es.developers.achambi.afines.home.ui.TaxPresentationBuilder
import es.developers.achambi.afines.home.usecase.TaxesUseCase
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
import es.developers.achambi.afines.notifications.NotificationsPresenter
import es.developers.achambi.afines.notifications.NotificationsScreen
import es.developers.achambi.afines.profile.presenter.ProfilePresenter
import es.developers.achambi.afines.profile.presenter.UpdatePasswordPresenter
import es.developers.achambi.afines.profile.ui.UpdatePasswordScreen
import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.services.NotificationServicePresenter
import es.developers.achambi.afines.ui.NotificationPresentationBuilder
import es.developers.achambi.afines.ui.OverviewPresentationBuilder
import es.developers.achambi.afines.utils.BaseUIPresenter
import es.developers.achambi.afines.utils.EventLogger
import java.util.regex.Pattern
import kotlin.math.log

class InvoicePresenterFactory(private val executor: MainExecutor,
                              private val invoiceUseCase: InvoiceUseCase,
                              private val presentationBuilder: InvoicePresentationBuilder,
                              private val localBroadcastManager: LocalBroadcastManager,
                              private val logger: EventLogger) {
    fun build( invoicesScreenInterface: InvoicesScreenInterface, lifecycle: Lifecycle ): InvoicePresenter {
        return InvoicePresenter( invoicesScreenInterface, lifecycle, executor, invoiceUseCase,
            presentationBuilder, localBroadcastManager, logger )
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
                                    private val uriUtils: URIUtils,
                                    private val logger: EventLogger) {
    fun build(uploadScreenInterface: UploadScreenInterface, lifecycle: Lifecycle): UploadPresenter {
        return UploadPresenter(uploadScreenInterface, lifecycle, executor, uriUtils, invoiceUseCase, presentationBuilder, logger)
    }
}

class ProfilePresenterFactory(private val executor: MainExecutor,
                              private val useCase: ProfileUseCase,
                              private val presentationBuilder: ProfilePresentationBuilder,
                              private val emailPattern: Pattern,
                              private val logger: EventLogger) {
    fun build(screen: ProfileScreenInterface, lifecycle: Lifecycle): ProfilePresenter {
        return ProfilePresenter(
            screen,
            lifecycle,
            executor,
            useCase,
            presentationBuilder,
            emailPattern,
            logger
        )
    }
}

class OverviewPresenterFactory(private val executor: MainExecutor,
                               private val useCase: ProfileUseCase,
                               private val broadcastManager: LocalBroadcastManager,
                               private val builder: OverviewPresentationBuilder,
                               private val logger: EventLogger){
    fun build(screen: OverviewScreen, lifecycle: Lifecycle): OverviewPresenter {
        return OverviewPresenter(screen, lifecycle, executor, useCase,
            broadcastManager, builder, logger)
    }
}

class UpdatePasswordPresenterFactory(private val executor: MainExecutor, private val useCase: ProfileUseCase,
                                     private val logger: EventLogger) {
    fun build(screen: UpdatePasswordScreen, lifecycle: Lifecycle): UpdatePasswordPresenter {
        return UpdatePasswordPresenter(screen, lifecycle, executor, useCase, logger)
    }
}

class LoginPresenterFactory(private val executor: MainExecutor, private val useCase: LoginUseCase,
                            private val eventLogger: EventLogger) {
    fun build(screen: LoginScreenInterface, lifecycle: Lifecycle): LoginPresenter {
        return LoginPresenter(screen, lifecycle, executor, useCase, eventLogger)
    }
}

class RetrievePasswordPresenterFactory(private val executor: MainExecutor,
                                       private val useCase: LoginUseCase,
                                       private val logger: EventLogger) {
    fun build(screen: RetrievePasswordScreen, lifecycle: Lifecycle): RetrievePasswordPresenter {
        return RetrievePasswordPresenter(screen, lifecycle, executor, useCase, logger)
    }
}

class MessagingServicePresenterFactory(private val executor: MainExecutor,
                                       private val useCase: ProfileUseCase,
                                       private val broadcastManager: LocalBroadcastManager) {
    fun build(): NotificationServicePresenter {
        return NotificationServicePresenter(executor, useCase, broadcastManager)
    }
}

class InvoiceFullScreenPresenterFactory(private val executor: MainExecutor,
                                        private val useCase: InvoiceUseCase) {
    fun build(screen: InvoiceFullScreenInterface, lifecycle: Lifecycle)
            : InvoiceFullScreenPresenter {
        return InvoiceFullScreenPresenter(screen, lifecycle, executor, useCase)
    }
}

class NotificationsPresenterFactory(private val executor: MainExecutor,
                                    private val useCase: ProfileUseCase,
                                    private val builder: NotificationPresentationBuilder) {
    fun build(screen: NotificationsScreen, lifecycle: Lifecycle): NotificationsPresenter {
        return NotificationsPresenter(useCase, builder, screen, lifecycle, executor)
    }
}

class BaseTestPresenterFactory(private val profileUseCase: ProfileUseCase) {
    fun build(): BaseUIPresenter {
        return BaseUIPresenter(profileUseCase)
    }
}
