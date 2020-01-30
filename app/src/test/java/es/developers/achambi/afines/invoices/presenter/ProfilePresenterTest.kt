package es.developers.achambi.afines.invoices.presenter

import es.developers.achambi.afines.profile.presenter.ProfilePresenter
import es.developers.achambi.afines.profile.ui.ProfileScreenInterface
import es.developers.achambi.afines.profile.ui.presentations.ProfilePresentation
import es.developers.achambi.afines.profile.ui.presentations.ProfilePresentationBuilder
import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import es.developer.achambi.coreframework.threading.CoreError
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.regex.Matcher
import java.util.regex.Pattern

@RunWith(MockitoJUnitRunner::class)
class ProfilePresenterTest: BasePresenterTest() {
    companion object {
        const val VALID_EMAIL = "test@gmail.com"
        const val VALID_IBAN = "ES6621000418401234567891"
    }
    private lateinit var presenter: ProfilePresenter
    @Mock
    private lateinit var screen: ProfileScreenInterface
    @Mock
    private lateinit var profileUseCase: ProfileUseCase
    @Mock
    private lateinit var profilePresentationBuilder: ProfilePresentationBuilder
    @Mock
    private lateinit var pattern: Pattern

    override fun setup() {
        super.setup()
        presenter = ProfilePresenter(screen, lifecycle, executor, profileUseCase,
            profilePresentationBuilder, pattern)
    }

    @Test
    fun `test initial profile load successful `() {
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

    @Test
    fun `test initial profile load failed`() {
        doThrow(CoreError()).`when`(profileUseCase).getUserProfile(false)

        presenter.onDataSetup()

        verify(screen, times(1)).showFullScreenProgress()
        verify(screen, times(1)).showProfileFieldsError()
    }

    @Test
    fun `test valid iban and email`() {
        val matcher = mock(Matcher::class.java)
        `when`(pattern.matcher(VALID_EMAIL)).thenReturn(matcher)
        `when`(matcher.matches()).thenReturn(true)

        presenter.validateFields(VALID_IBAN, VALID_EMAIL)

        verify(screen, times(1)).showIbanValidated()
        verify(screen, times(1)).showShowEmailValidated()
        verify(screen, times(1)).showSaveAvailability(true)
    }

    @Test
    fun `test valid iban and invalid email`() {
        val matcher = mock(Matcher::class.java)
        `when`(pattern.matcher("")).thenReturn(matcher)
        `when`(matcher.matches()).thenReturn(false)

        presenter.validateFields(VALID_IBAN, "")

        verify(screen, times(1)).showIbanValidated()
        verify(screen, times(1)).showEmailRejected()
        verify(screen, times(1)).showSaveAvailability(false)
    }

    @Test
    fun `test invalid iban and valid email`() {
        val matcher = mock(Matcher::class.java)
        `when`(pattern.matcher(VALID_EMAIL)).thenReturn(matcher)
        `when`(matcher.matches()).thenReturn(true)

        presenter.validateFields("", VALID_EMAIL)
        verify(screen, times(1)).showIbanRejected()
        verify(screen, times(1)).showShowEmailValidated()
        verify(screen, times(1)).showSaveAvailability(false)
    }

    @Test
    fun `test invalid iban and invalid email`() {
        val matcher = mock(Matcher::class.java)
        `when`(pattern.matcher("")).thenReturn(matcher)
        `when`(matcher.matches()).thenReturn(false)

        presenter.validateFields("", "")

        verify(screen, times(1)).showIbanRejected()
        verify(screen, times(1)).showEmailRejected()
        verify(screen, times(1)).showSaveAvailability(false)
    }
}