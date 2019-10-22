package es.developers.achambi.afines.home

import es.developers.achambi.afines.repositories.FirebaseRepository
import es.developers.achambi.afines.repositories.model.FirebaseNotification

class NotificationsUseCase(private val firebaseRepository: FirebaseRepository) {
    fun getNotifications(): List<FirebaseNotification> {
        return firebaseRepository.retrieveNotifications()
    }
}