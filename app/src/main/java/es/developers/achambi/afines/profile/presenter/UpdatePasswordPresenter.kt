package es.developers.achambi.afines.profile.presenter

import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.CoreError
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.profile.ui.UpdatePasswordScreen
import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.utils.EventLogger

class UpdatePasswordPresenter(screen: UpdatePasswordScreen,
                              lifecycle: Lifecycle,
                              executor: MainExecutor,
                              private val useCase: ProfileUseCase,
                              private val analytics: EventLogger)
    :Presenter<UpdatePasswordScreen>(screen, lifecycle, executor) {

    fun userSaved(currentPassword: String, newPassword: String,
                  confirmationPassword: String) {
        screen.hideSoftKeyboard()
        if(currentPassword.isEmpty() ||newPassword.isEmpty() || confirmationPassword.isEmpty()) {
            return screen.showMissingFieldsError()
        }

        if( newPassword.length < 6 ) {
            return screen.showWeakPasswordError()
        }

        if(newPassword != confirmationPassword) {
            return screen.showConfirmationNotMatching()
        }

        analytics.publishPasswordUpdated()
        screen.showHeaderProgress()
        val responseHandler = object : ResponseHandler<Any?> {
            override fun onSuccess(response: Any?) {
                screen.showHeaderProgressFinished()
                screen.showPasswordUpdateSuccess()
                screen.showFieldsCleared()
            }

            override fun onError(error: CoreError) {
                super.onError(error)
                screen.showHeaderProgressFinished()
                screen.showInvalidPasswordError()
            }
        }
        val request = object : Request<Any?> {
            override fun perform(): Any? {
                return useCase.updatePassword(currentPassword, newPassword)
            }
        }
        request(request, responseHandler)
    }
}