package es.developers.achambi.afines.utils

import es.developers.achambi.afines.profile.usecase.ProfileUseCase

class BaseTestPresenter(private val profileUseCase: ProfileUseCase) {
    fun clearCache() {
        profileUseCase.logout()
    }
}