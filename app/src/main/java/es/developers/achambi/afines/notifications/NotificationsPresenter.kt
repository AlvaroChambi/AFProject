package es.developers.achambi.afines.notifications

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import es.developer.achambi.coreframework.threading.CoreError
import es.developer.achambi.coreframework.threading.ExecutorInterface
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.Navigation
import es.developers.achambi.afines.OverviewActivity
import es.developers.achambi.afines.home.NotificationPresentation
import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.repositories.model.OverviewNotification
import es.developers.achambi.afines.ui.NotificationPresentationBuilder

class NotificationsPresenter(private val profileUseCase: ProfileUseCase,
                             private val presentationBuilder: NotificationPresentationBuilder,
                             private val broadcastManager: LocalBroadcastManager,
                             screen: NotificationsScreen,
                             lifecycle: Lifecycle,
                             executor: ExecutorInterface)
    : Presenter<NotificationsScreen>(screen, lifecycle, executor) {
    fun onDataSetup() {
        screen.showProgress()
        val request = object : Request<ArrayList<OverviewNotification>> {
            override fun perform(): ArrayList<OverviewNotification> {
                return profileUseCase.getUserNotifications()
            }
        }
        val handler = object : ResponseHandler<ArrayList<OverviewNotification>> {
            override fun onSuccess(response: ArrayList<OverviewNotification>) {
                screen.showProgressFinished()
                val presentations = ArrayList<NotificationPresentation>()
                response.forEach {
                    presentations.add(presentationBuilder.build(it))
                }
                screen.showNotifications(presentations)
            }

            override fun onError(error: CoreError) {
                super.onError(error)
                screen.showProgressFinished()
            }
        }

        request(request, handler)
    }

    fun navigateToProfile(activity: Activity) {
        activity.startActivity(OverviewActivity.getStartIntent(activity,
            Navigation.PROFILE_DEEP_LINK.toString()))
    }

    fun navigateToInvoices(activity: Activity) {
        activity.startActivity(OverviewActivity.getStartIntent(activity,
            Navigation.INVOICES_DEEP_LINK.toString()))
    }
}