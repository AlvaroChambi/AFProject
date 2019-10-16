package es.developers.achambi.afines.invoices.usecase

import es.developers.achambi.afines.repositories.FirebaseRepository
import es.developers.achambi.afines.repositories.model.FirebaseProfile

class ProfileUseCase(private val firebaseRepository: FirebaseRepository) {
    private var firebaseProfile: FirebaseProfile? = null

    fun getUserProfile(): FirebaseProfile? {
        if(firebaseProfile == null) {
            firebaseProfile = firebaseRepository.retrieveCurrentUser()
        }
        return firebaseProfile
    }
}