package es.developers.achambi.afines.home

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import es.developer.achambi.coreframework.threading.Error
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.Navigation
import es.developers.achambi.afines.NotificationsScreen
import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.repositories.model.FirebaseNotification
import es.developers.achambi.afines.repositories.model.FirebaseProfile

class OverviewPresenter(notificationsScreen: NotificationsScreen,
                        lifecycle: Lifecycle,
                        executor: MainExecutor,
                        private val profileUseCase: ProfileUseCase,
                        private val broadcastManager: LocalBroadcastManager)
    : Presenter<NotificationsScreen>(notificationsScreen, lifecycle, executor) {

    fun onViewSetup() {
        val responseHandler= object : ResponseHandler<FirebaseProfile?> {
            override fun onSuccess(response: FirebaseProfile?) {
                response?.let {
                    if(!it.passwordChanged) {
                        screen.showUpdatePasswordNotification()
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
    }

    fun navigateToProfile() {
        broadcastManager.sendBroadcast(Intent(Navigation.PROFILE_DEEP_LINK.toString()))
    }
}