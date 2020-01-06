package es.developers.achambi.afines.login.usecase

import es.developers.achambi.afines.repositories.FirebaseRepository

class LoginUseCase(private val firebaseRepository: FirebaseRepository) {
    fun login(email: String, password: String) {
        firebaseRepository.login(email, password)
    }
}