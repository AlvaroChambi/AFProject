package es.developers.achambi.afines.profile.usecase

import android.annotation.SuppressLint
import android.content.SharedPreferences
import es.developer.achambi.coreframework.threading.CoreError
import es.developers.achambi.afines.home.usecase.TaxesUseCase
import es.developers.achambi.afines.invoices.ui.TrimesterUtils
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase
import es.developers.achambi.afines.profile.presenter.ProfileUpload
import es.developers.achambi.afines.repositories.FirebaseRepository
import es.developers.achambi.afines.repositories.model.*
import java.util.*

class ProfileUseCase(private val firebaseRepository: FirebaseRepository,
                     private val invoicesUseCase: InvoiceUseCase,
                     private val taxesUseCase: TaxesUseCase,
                     private val preferences: SharedPreferences) {
    companion object {
        const val DEVICE_TOKEN_KEY = "DEVICE_TOKEN"
    }
    private var firebaseProfile: FirebaseProfile? = null
    private var counters: InvoiceCounters? = null

    @Throws(CoreError::class)
    fun getUserProfile(refresh: Boolean): FirebaseProfile? {
        if(refresh || firebaseProfile == null) {
            firebaseProfile = firebaseRepository.retrieveCurrentUser()
        }
        return firebaseProfile
    }

    @Throws(CoreError::class)
    fun getUserOverview(): UserOverview {
        val profile = getUserProfile(false)
        val counters = getUserInvoiceCounters()

        var notification: OverviewNotification? = null
        if(taxesUseCase.getTaxDates().isNotEmpty()) {
            val nexTaxDate = taxesUseCase.getTaxDates()[0]
            notification = OverviewNotification(NotificationType.TAX_DATE_REMINDER, nexTaxDate.name,
                nexTaxDate.date)
        }
        profile?.let {
            if(!profile.passwordChanged) notification = OverviewNotification(
                NotificationType.PASS_NOT_UPDATED)
        }
        counters?.let {
            if(counters.rejected > 0) notification = OverviewNotification(
                NotificationType.INVOICE_REJECTED)
        }

        return UserOverview(counters, profile, notification)
    }

    private fun getUserInvoiceCounters(): InvoiceCounters? {
        if(counters == null) {
            val trimester = TrimesterUtils.getCurrentTrimester()
            val year = Calendar.getInstance().get(Calendar.YEAR)
            counters = firebaseRepository.getCounters(trimester.toString(), year.toString())
        }
        return counters
    }

    fun updateProfile(profileUpload: ProfileUpload) {
        firebaseRepository.updateUserProfile(profileUpload)
    }

    fun updatePassword(currentPassword: String, newPassword: String) {
        firebaseRepository.updateUserPassword(currentPassword, newPassword)
        firebaseProfile?.let {
            if(!it.passwordChanged) {
                firebaseRepository.checkProfilePasswordFlag()
                it.passwordChanged = true
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    fun saveDeviceToken(deviceToken: String){
        preferences.edit().putString(DEVICE_TOKEN_KEY, deviceToken).commit()
    }

    @SuppressLint("ApplySharedPref")
    fun updateProfileToken(userToken: String) {
        val preferenceToken = preferences.getString(DEVICE_TOKEN_KEY, "")
        if(!preferenceToken.isNullOrEmpty() && userToken != preferenceToken) {
            firebaseRepository.updateProfileToken(preferenceToken)
        }
    }

    fun clearProfileCache() {
        firebaseProfile = null
    }

    fun logout() {
        firebaseProfile = null
        firebaseRepository.updateProfileToken("")
        invoicesUseCase.clearCache()
        firebaseRepository.logout()
    }
}