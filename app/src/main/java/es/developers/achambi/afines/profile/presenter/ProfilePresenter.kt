package es.developers.achambi.afines.profile.presenter

import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.Error
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.profile.ui.ProfileScreenInterface
import es.developers.achambi.afines.profile.ui.presentations.ProfilePresentationBuilder
import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import es.developers.achambi.afines.utils.IBANUtil

class ProfilePresenter(screen: ProfileScreenInterface,
                       lifecycle: Lifecycle,
                       executor: MainExecutor,
                       private val useCase: ProfileUseCase,
                       private val presentationBuilder: ProfilePresentationBuilder)
    : Presenter<ProfileScreenInterface>(screen, lifecycle, executor) {

    fun onDataSetup() {
        screen.showFullScreenProgress()
        val responseHandler = object : ResponseHandler<FirebaseProfile?> {
            override fun onSuccess(response: FirebaseProfile?) {
                screen.showFullScreenProgressFinished()
                response?.let {
                    screen.showProfileFields(presentationBuilder.buildPresentation(it))
                    screen.showEditAvailable()
                }
            }

            override fun onError(error: Error) {
                super.onError(error)
                screen.showProfileFieldsError()
            }
        }

        val request = object : Request<FirebaseProfile?> {
            override fun perform(): FirebaseProfile? {
                return useCase.getUserProfile(false)
            }
        }

        request(request, responseHandler)
    }

    fun validateIban(iban: String) {
        if(IBANUtil.isIbanValid(iban)) {
            screen.showIbanValidated()
        } else {
            screen.showIbanRejected()
        }
    }

    fun logout() {
        val responseHandler = object: ResponseHandler<Any?> {
            override fun onSuccess(response: Any?) {
                screen.exit()
            }
        }
        val request = object : Request<Any?> {
            override fun perform(): Any? {
                return useCase.logout()
            }
        }
        request(request, responseHandler)
    }

    fun saveProfile( email: String, address: String, dni: String,
                     naf: String, ccc: String, account: String) {
        screen.showUpdateProgress()
        screen.showSaveAvailability(false)
        val upload = ProfileUpload.Builder()
            .email(email)
            .address(address)
            .dni(dni)
            .naf(naf)
            .ccc(ccc)
            .account(account).build()
        val responseHandler = object : ResponseHandler<Any?> {
            override fun onSuccess(response: Any?) {
                screen.showUpdateProgressFinished()
                screen.showProfileUpdateSuccess()
                screen.showSaveAvailability(true)
                screen.showEditStateDisabled()
                refreshProfile()
            }

            override fun onError(error: Error) {
                super.onError(error)
                screen.showUpdateProgressFinished()
                screen.showProfileUpdateError()
                screen.showSaveAvailability(true)
                screen.showEditStateDisabled()
            }
        }
        val request = object : Request<Any?> {
            override fun perform(): Any? {
                return useCase.updateProfile(upload)
            }
        }

        request(request, responseHandler)
    }

    fun refreshProfile() {
        screen.showUpdateProgress()
        val responseHandler = object : ResponseHandler<FirebaseProfile?> {
            override fun onSuccess(response: FirebaseProfile?) {
                screen.showUpdateProgressFinished()
                response?.let {
                    screen.showProfileFields(presentationBuilder.buildPresentation(it))
                }
            }

            override fun onError(error: Error) {
                super.onError(error)
                screen.showUpdateProgressFinished()
            }
        }

        val request = object : Request<FirebaseProfile?> {
            override fun perform(): FirebaseProfile? {
                return useCase.getUserProfile(true)
            }
        }

        request(request, responseHandler)
    }
}

data class ProfileUpload(val email: String,
                         val address: String,
                         val dni: String,
                         val naf: String,
                         val ccc: String,
                         val account: String) {
    data class Builder(var email: String = "",
                       var address: String = "",
                       var dni: String = "",
                       var naf: String = "",
                       var ccc: String = "",
                       var account: String = ""){
        fun email(email: String) = apply { this.email = email }
        fun address(address: String) = apply { this.address = address }
        fun dni(dni: String) = apply { this.dni = dni }
        fun naf(naf: String) = apply { this.naf = naf }
        fun ccc(ccc: String) = apply { this.ccc = ccc }
        fun account(account: String) = apply { this.account = account }
        fun build() =
            ProfileUpload(email, address, dni, naf, ccc, account)
    }
}