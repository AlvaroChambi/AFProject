package es.developers.achambi.afines.invoices.presenter

import es.developers.achambi.afines.profile.presenter.ProfilePresenter
import es.developers.achambi.afines.profile.ui.ProfileScreenInterface
import es.developers.achambi.afines.profile.ui.presentations.ProfilePresentation
import es.developers.achambi.afines.profile.ui.presentations.ProfilePresentationBuilder
import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ProfilePresenterTest: BasePresenterTest() {
    private lateinit var presenter: ProfilePresenter
    @Mock
    private lateinit var screen: ProfileScreenInterface
    @Mock
    private lateinit var profileUseCase: ProfileUseCase
    @Mock
    private lateinit var profilePresentationBuilder: ProfilePresentationBuilder

    override fun setup() {
        super.setup()
        presenter = ProfilePresenter(screen, lifecycle, executor, profileUseCase,
            profilePresentationBuilder)
    }

    @Test
    fun `initial load successful `() {
        val profile = FirebaseProfile()
        val presentation = ProfilePresentation("", "", "", "", "",
            "", "")
        `when`(profileUseCase.getUserProfile(false)).thenReturn(profile)
        `when`(profilePresentationBuilder.buildPresentation(profile)).thenReturn(presentation)

        presenter.onDataSetup()

        verify(screen, times(1)).showFullScreenProgress()
        verify(screen, times(1)).showFullScreenProgressFinished()

        verify(screen, times(1)).showProfileFields(presentation)
        verify(screen, times(1)).showEditAvailable()
    }
}