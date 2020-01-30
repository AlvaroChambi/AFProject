package es.developers.achambi.afines.login

import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.CoreError
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.login.usecase.LoginUseCase

class RetrievePasswordPresenter(screen: RetrievePasswordScreen,
                                lifecycle: Lifecycle,
                                executor: MainExecutor,
                                private val useCase: LoginUseCase)
    : Presenter<RetrievePasswordScreen>(screen, lifecycle, executor) {

    fun retrievePassword(email: String?) {
        if(email.isNullOrEmpty()) {
            return screen.showInvalidUser()
        }
        val responseHandler = object: ResponseHandler<Any> {
            override fun onSuccess(response: Any) {
                screen.showEmailSentSuccess()
            }

            override fun onError(error: CoreError) {
                screen.showInvalidUser()
            }
        }

        val request = object: Request<Any> {
            override fun perform(): Any {
                return useCase.retrievePassword(email)
            }
        }
        request(request, responseHandler)
    }
}