package es.developers.achambi.afines.login

import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.Error
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.login.usecase.LoginUseCase
import es.developers.achambi.afines.repositories.RepositoryError

class LoginPresenter(screen: LoginScreenInterface, lifecycle: Lifecycle,
                     executor: MainExecutor,
                     private val useCase: LoginUseCase)
    : Presenter<LoginScreenInterface>(screen, lifecycle, executor) {
    fun onViewSetup() {
        if(useCase.isSessionAlive()) {
            screen.showNextScreen()
        }
    }

    fun login(email: String?, password: String?) {
        screen.showProgress()
        if(email.isNullOrEmpty()) {
            screen.finishProgress()
            return screen.showInvalidEmail()
        }
        if( password.isNullOrEmpty()) {
            screen.finishProgress()
            return screen.showInvalidPassword()
        }
        val responseHandler = object: ResponseHandler<Any> {
            override fun onSuccess(response: Any) {
                screen.finishProgress()
                screen.showNextScreen()
            }

            override fun onError(error: Error) {
                screen.finishProgress()
                val repositoryError : RepositoryError = if(error.type != null) {
                    val typeCode : String = error.type!!
                    RepositoryError.valueOf(typeCode)
                } else {
                    RepositoryError.GENERIC_ERROR
                }

                when(repositoryError) {
                    RepositoryError.INVALID_USER -> screen.showInvalidUser()
                    RepositoryError.ERROR_WRONG_PASSWORD -> screen.showInvalidPassword()
                    RepositoryError.ERROR_INVALID_EMAIL -> screen.showInvalidEmail()
                    else -> screen.showGenericError()
                }
            }
        }
        val request = object : Request<Any> {
            override fun perform(): Any {
                return useCase.login(email, password)
            }
        }
        request(request, responseHandler)
    }
}