package es.developers.achambi.afines.home

import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import es.developer.achambi.coreframework.threading.CoreError
import es.developer.achambi.coreframework.threading.ExecutorInterface
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.Navigation
import es.developers.achambi.afines.OverviewScreen
import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.repositories.model.NotificationType
import es.developers.achambi.afines.repositories.model.UserOverview
import es.developers.achambi.afines.ui.OverviewPresentationBuilder
import es.developers.achambi.afines.utils.EventLogger

class OverviewPresenter(notificationsScreen: OverviewScreen,
                        lifecycle: Lifecycle,
                        executor: ExecutorInterface,
                        private val profileUseCase: ProfileUseCase,
                        private val broadcastManager: LocalBroadcastManager,
                        private val presentationBuilder: OverviewPresentationBuilder,
                        private val analytics: EventLogger)
    : Presenter<OverviewScreen>(notificationsScreen, lifecycle, executor) {

    fun onViewSetup() {
        screen.showLoading()
        val responseHandler= object : ResponseHandler<UserOverview> {
            @SuppressLint("DefaultLocale")
            override fun onSuccess(response: UserOverview) {
                screen.showLoadingFinished()
                val presentation = presentationBuilder.build(response)
                screen.showInvoicesCount( presentation.approvedCount,
                    presentation.pendingCount,
                    presentation.rejectedCount)
                if(presentation.ccc.trim().isNotEmpty()) screen.showCCCValue(presentation.ccc)
                if(presentation.naf.trim().isNotEmpty()) screen.showNAFValue(presentation.naf)
                if(presentation.iban.trim().isNotEmpty()) screen.showIbanValue(presentation.iban)

                if(presentation.notification.isNotEmpty()) {
                    screen.showNotification(presentation.notification, presentation.type)
                }
            }

            override fun onError(error: CoreError) {
                super.onError(error)
                screen.showLoadingFinished()
                screen.showLoadingFailed(error)
            }
        }

        val request= object : Request<UserOverview> {
            override fun perform(): UserOverview {
                return profileUseCase.getUserOverview()
            }
        }
        request(request, responseHandler)
    }

    fun notificationGoToSelected(type: NotificationType) {
        when(type) {
            NotificationType.INVOICE_REJECTED -> navigateToInvoices()
            NotificationType.PASS_NOT_UPDATED -> navigateToProfile()
            NotificationType.TAX_DATE_REMINDER -> {}
            NotificationType.NONE -> {}
        }
    }

    fun navigateToProfile() {
        broadcastManager.sendBroadcast(Intent(Navigation.PROFILE_DEEP_LINK.toString()))
        analytics.publishProfileDeeplinkSelected()
    }

    fun navigateToInvoices() {
        broadcastManager.sendBroadcast(Intent(Navigation.INVOICES_DEEP_LINK.toString()))
        analytics.publishInvoicesDeeplinkSelected()
    }
}