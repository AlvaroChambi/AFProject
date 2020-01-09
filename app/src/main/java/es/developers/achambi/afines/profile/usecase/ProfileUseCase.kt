package es.developers.achambi.afines.profile.usecase

import android.annotation.SuppressLint
import android.content.SharedPreferences
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase
import es.developers.achambi.afines.profile.presenter.ProfileUpload
import es.developers.achambi.afines.repositories.FirebaseRepository
import es.developers.achambi.afines.repositories.model.FirebaseProfile

class ProfileUseCase(private val firebaseRepository: FirebaseRepository,
                     private val invoicesUseCase: InvoiceUseCase,
                     private val preferences: SharedPreferences) {
    companion object {
        const val DEVICE_TOKEN_KEY = "DEVICE_TOKEN"
    }
    private var firebaseProfile: FirebaseProfile? = null

    fun getUserProfile(refresh: Boolean): FirebaseProfile? {
        if(refresh || firebaseProfile == null) {
            firebaseProfile = firebaseRepository.retrieveCurrentUser()
        }
        return firebaseProfile
    }

    fun updateProfile(profileUpload: ProfileUpload) {
        firebaseRepository.updateUserProfile(profileUpload)
    }

    fun updatePassword(currentPassword: String, newPassword: String) {
        firebaseRepository.updateUserPassword(currentPassword, newPassword)
    }

    @SuppressLint("ApplySharedPref")
    fun saveDeviceToken(deviceToken: String){
        preferences.edit().putString(DEVICE_TOKEN_KEY, deviceToken).commit()
    }

    @SuppressLint("ApplySharedPref")
    fun updateProfileToken() {
        val preferenceToken = preferences.getString(DEVICE_TOKEN_KEY, "")
        if(!preferenceToken.isNullOrEmpty()) {
            val profiles = firebaseRepository.queryUserProfileByToken(preferenceToken)
            if(profiles.isNotEmpty()) {
                val profile = profiles[0]
                if(profile.token != preferenceToken) {
                    firebaseRepository.updateProfileToken(preferenceToken)
                }
            }
        }
    }

    fun logout() {
        firebaseProfile = null
        invoicesUseCase.clearCache()
        firebaseRepository.logout()
    }
}