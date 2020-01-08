package es.developers.achambi.afines.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import es.developers.achambi.afines.AfinesApplication

class FirebaseNotificationService: FirebaseMessagingService() {
    private lateinit var presenter: NotificationServicePresenter
    override fun onCreate() {
        super.onCreate()
        presenter = AfinesApplication.messagingServicePresenterFactory.build()
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        presenter.publishNotification()
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        presenter.saveDeviceToken(p0)
    }
}

enum class Notifications {
    INVOICE_REJECTED
}