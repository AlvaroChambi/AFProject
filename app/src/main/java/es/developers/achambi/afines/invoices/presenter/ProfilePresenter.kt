package es.developers.achambi.afines.invoices.presenter

import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.Error
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.invoices.ui.profile.presentations.ProfilePresentation

class ProfilePresenter(screen: ProfileScreenInterface,
                       lifecycle: Lifecycle,
                       executor: MainExecutor )
    : Presenter<ProfileScreenInterface>(screen, lifecycle, executor) {

    fun onDataSetup() {
        screen.showFullScreenProgress()
        val responseHandler = object : ResponseHandler<Any?> {
            override fun onSuccess(response: Any?) {
                screen.showFullScreenProgressFinished()
                val presentation = ProfilePresentation(
                    "Pepito Perez",
                    "pepito@gmail.com",
                    "calle de pepito 25",
                    "34085487H",
                    "1234567689",
                    "98776545431",
                    "ES450654845698456"
                )
                screen.showProfileFields(presentation)
            }

            override fun onError(error: Error) {
                super.onError(error)
                screen.showProfileFieldsError()
            }
        }

        val request = object : Request<Any?> {
            override fun perform(): Any? {
                Thread.sleep(1000)
                return null
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