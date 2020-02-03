package es.developers.achambi.afines.login

import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.*
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.login.usecase.LoginUseCase
import es.developers.achambi.afines.repositories.RepositoryError

class LoginPresenter(screen: LoginScreenInterface, lifecycle: Lifecycle,
                     executor: ExecutorInterface,
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

            override fun onError(error: CoreError) {
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