package es.developers.achambi.afines.invoices.presenter

import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.Error
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.invoices.ui.profile.presentations.ProfilePresentation
import es.developers.achambi.afines.invoices.ui.profile.presentations.ProfilePresentationBuilder
import es.developers.achambi.afines.invoices.usecase.ProfileUseCase
import es.developers.achambi.afines.repositories.model.FirebaseProfile

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
                }
            }

            override fun onError(error: Error) {
                super.onError(error)
                screen.showProfileFieldsError()
            }
        }

        val request = object : Request<FirebaseProfile?> {
            override fun perform(): FirebaseProfile? {
                return useCase.getUserProfile()
            }
        }

        request(request, responseHandler)
    }
}

interface ProfileScreenInterface: Screen {
    fun showProfileFields(presentation: ProfilePresentation)
    fun showProfileFieldsError()

    fun showFullScreenProgress()
    fun showFullScreenProgressFinished()
}