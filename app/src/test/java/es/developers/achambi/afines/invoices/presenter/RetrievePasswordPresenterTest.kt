package es.developers.achambi.afines.invoices.presenter

import es.developer.achambi.coreframework.threading.CoreError
import es.developers.achambi.afines.login.RetrievePasswordPresenter
import es.developers.achambi.afines.login.RetrievePasswordScreen
import es.developers.achambi.afines.login.usecase.LoginUseCase
import es.developers.achambi.afines.utils.EventLogger
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

class RetrievePasswordPresenterTest: BasePresenterTest() {
    private lateinit var presenter: RetrievePasswordPresenter
    @Mock
    private lateinit var screen: RetrievePasswordScreen
    @Mock
    private lateinit var useCase: LoginUseCase
    @Mock
    private lateinit var logger: EventLogger

    override fun setup() {
        super.setup()
        presenter = RetrievePasswordPresenter(screen, lifecycle, executor, useCase, logger)
    }

    @Test
    fun `test null-empty email`() {
        presenter.retrievePassword(null)

        verify(screen, times(1)).showInvalidUser()
    }

    @Test
    fun `test retrieve request success`() {
        presenter.retrievePassword("test@email.com")
        verify(screen, times(1)).showEmailSentSuccess()
    }

    @Test
    fun `test invalid email (bad format or user not found)`() {
        doThrow(CoreError()).`when`(useCase).retrievePassword("test@gmail.com")

        presenter.retrievePassword("test@gmail.com")

        verify(screen, times(1)).showInvalidUser()
    }
}