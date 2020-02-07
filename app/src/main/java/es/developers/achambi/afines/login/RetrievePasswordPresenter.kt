package es.developers.achambi.afines.login

import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.*
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.login.usecase.LoginUseCase
import es.developers.achambi.afines.utils.EventLogger

class RetrievePasswordPresenter(screen: RetrievePasswordScreen,
                                lifecycle: Lifecycle,
                                executor: ExecutorInterface,
                                private val useCase: LoginUseCase,
                                private val analytics: EventLogger
)
    : Presenter<RetrievePasswordScreen>(screen, lifecycle, executor) {

    @Throws(CoreError::class)
    fun retrievePassword(email: String?) {
        if(email.isNullOrEmpty()) {
            return screen.showInvalidUser()
        }
        analytics.publishPasswordRetrieved()
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