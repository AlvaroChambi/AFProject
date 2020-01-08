package es.developers.achambi.afines.login.usecase

import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.repositories.FirebaseRepository

class LoginUseCase(private val firebaseRepository: FirebaseRepository,
                   private val profileUseCase: ProfileUseCase) {
    fun login(email: String, password: String) {
        firebaseRepository.login(email, password)
        profileUseCase.updateProfileToken()
    }

    fun retrievePassword(email: String) {
        firebaseRepository.retrievePassword(email)
    }

    fun isSessionAlive(): Boolean {
        return firebaseRepository.getCurrentUser() != null
    }
}