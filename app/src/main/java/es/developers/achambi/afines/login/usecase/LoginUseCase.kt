package es.developers.achambi.afines.login.usecase

import es.developer.achambi.coreframework.threading.CoreError
import es.developers.achambi.afines.profile.usecase.CountersUseCase
import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.repositories.FirebaseRepository

class LoginUseCase(private val firebaseRepository: FirebaseRepository,
                   private val profileUseCase: ProfileUseCase,
                   private val countersUseCase: CountersUseCase) {
    @Throws(CoreError::class)
    fun login(email: String, password: String) {
        firebaseRepository.login(email, password)
        profileUseCase.updateProfileToken(profileUseCase.getUserProfile(false)!!.token)
        val counters = countersUseCase.setupCounters()
        counters?.reference?.let { profileUseCase.updateCountersReference(it) }
    }

    @Throws(CoreError::class)
    fun retrievePassword(email: String) {
        firebaseRepository.retrievePassword(email)
    }

    fun isSessionAlive(): Boolean {
        return firebaseRepository.getCurrentUser() != null
    }
}