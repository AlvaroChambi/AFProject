package es.developers.achambi.afines.home

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.Lifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import es.developer.achambi.coreframework.threading.ExecutorInterface
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.Navigation
import es.developers.achambi.afines.OverviewScreen
import es.developers.achambi.afines.home.model.TaxDate
import es.developers.achambi.afines.home.ui.TaxPresentationBuilder
import es.developers.achambi.afines.home.usecase.TaxesUseCase
import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import es.developers.achambi.afines.services.Notifications
import es.developers.achambi.afines.utils.EventLogger

class OverviewPresenter(notificationsScreen: OverviewScreen,
                        lifecycle: Lifecycle,
                        executor: ExecutorInterface,
                        private val profileUseCase: ProfileUseCase,
                        private val taxesUseCase: TaxesUseCase,
                        private val broadcastManager: LocalBroadcastManager,
                        private val taxesPresentationBuilder: TaxPresentationBuilder,
                        private val analytics: EventLogger)
    : Presenter<OverviewScreen>(notificationsScreen, lifecycle, executor) {

    fun onViewSetup() {
        val responseHandler= object : ResponseHandler<FirebaseProfile?> {
            override fun onSuccess(response: FirebaseProfile?) {
                response?.let {
                    if(!it.passwordChanged) {
                        screen.showUpdatePasswordNotification()
                    }

                    if(it.rejected > 0) {
                        screen.showRejectInvoicesNotification()
                    }
                }
            }
        }

        val request= object : Request<FirebaseProfile?> {
            override fun perform(): FirebaseProfile? {
                return profileUseCase.getUserProfile(false)
            }
        }
        request(request, responseHandler)

        val taxesResponse= object : ResponseHandler<List<TaxDate>> {
            override fun onSuccess(response: List<TaxDate>) {
                screen.showTaxDates(taxesPresentationBuilder.build(response))
            }
        }
        val taxesRequest = object : Request<List<TaxDate>> {
            override fun perform(): List<TaxDate> {
                return taxesUseCase.getTaxDates()
            }
        }
        request(taxesRequest, taxesResponse)
    }

    fun navigateToProfile() {
        broadcastManager.sendBroadcast(Intent(Navigation.PROFILE_DEEP_LINK.toString()))
        analytics.publishProfileDeeplinkSelected()
    }

    fun navigateToInvoices() {
        broadcastManager.sendBroadcast(Intent(Navigation.INVOICES_DEEP_LINK.toString()))
        analytics.publishInvoicesDeeplinkSelected()
    }

    fun registerBroadcast(broadcastReceiver: BroadcastReceiver) {
        broadcastManager.registerReceiver(broadcastReceiver,
            IntentFilter(Notifications.INVOICE_REJECTED.toString())
        )
    }

    fun unregisterBroadcast(broadcastReceiver: BroadcastReceiver) {
        broadcastManager.unregisterReceiver(broadcastReceiver)
    }
}