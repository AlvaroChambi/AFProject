package es.developers.achambi.afines.home

import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.Error
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.NotificationsScreen
import es.developers.achambi.afines.repositories.model.FirebaseNotification

class NotificationsPresenter(notificationsScreen: NotificationsScreen,
                             lifecycle: Lifecycle,
                             executor: MainExecutor,
                             private val notificationsUseCase: NotificationsUseCase)
    : Presenter<NotificationsScreen>(notificationsScreen, lifecycle, executor) {

    fun onDataSetup() {
        val responseHandler = object : ResponseHandler<List<FirebaseNotification>> {
            override fun onSuccess(response: List<FirebaseNotification>) {
                val presentations = ArrayList<NotificationPresentation>()
                response.forEach{
                    presentations.add(NotificationPresentation(it.id,
                        it.message))
                }
                screen.showNotifications(presentations)
            }
        }
        val request = object: Request<List<FirebaseNotification>> {
            override fun perform(): List<FirebaseNotification> {
                return notificationsUseCase.getNotifications()
            }
        }
        request(request, responseHandler)
    }
}