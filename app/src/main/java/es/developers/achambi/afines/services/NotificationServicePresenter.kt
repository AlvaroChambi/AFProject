package es.developers.achambi.afines.services

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.threading.Request
import es.developers.achambi.afines.profile.usecase.ProfileUseCase

class NotificationServicePresenter(private val executor: MainExecutor,
                                   private val useCase: ProfileUseCase,
                                   private val broadcastManager: LocalBroadcastManager) {
    fun saveDeviceToken(token: String) {
        val request = object : Request<Any> {
            override fun perform(): Any {
                return useCase.saveDeviceToken(token)
            }
        }
        executor.executeRequest(request, null)
    }

    fun publishNotification() {
        val intent = Intent(Notifications.INVOICE_REJECTED.toString())
        broadcastManager.sendBroadcast(intent)
    }
}