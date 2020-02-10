package es.developers.achambi.afines.invoices.presenter

import es.developer.achambi.coreframework.threading.CoreError
import es.developers.achambi.afines.login.LoginPresenter
import es.developers.achambi.afines.login.LoginScreenInterface
import es.developers.achambi.afines.login.usecase.LoginUseCase
import es.developers.achambi.afines.repositories.RepositoryError
import es.developers.achambi.afines.utils.EventLogger
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

class LoginPresenterTest: BasePresenterTest() {
    private lateinit var presenter: LoginPresenter
    @Mock
    private lateinit var screen: LoginScreenInterface
    @Mock
    private lateinit var useCase: LoginUseCase
    @Mock
    private lateinit var logger: EventLogger

    override fun setup() {
        super.setup()
        presenter = LoginPresenter(screen, lifecycle, executor, useCase, logger)
    }

    @Test
    fun `test empty-null email parameter`() {
        presenter.login("", "0123456")

        verify(screen, times(1)).showProgress()
        verify(screen, times(1)).showInvalidEmail()
        verify(screen, times(1)).finishProgress()
    }

    @Test
    fun `test empty-null password parameter`() {
        presenter.login("email@something.com", null)

        verify(screen, times(1)).showProgress()
        verify(screen, times(1)).showInvalidPassword()
        verify(screen, times(1)).finishProgress()
    }

    @Test
    fun `test error generic`() {
        doThrow(CoreError(type = RepositoryError.GENERIC_ERROR.toString()))
            .`when`(useCase).login("email@generic.com", "0123456")

        presenter.login("email@generic.com", "0123456")

        verify(screen, times(1)).showProgress()
        verify(screen, times(1)).showGenericError()
        verify(screen, times(1)).finishProgress()
    }

    @Test
    fun `test error email bad format`() {
        doThrow(CoreError(type = RepositoryError.ERROR_INVALID_EMAIL.toString()))
            .`when`(useCase).login("email@generic.com", "0123456")

        presenter.login("email@generic.com", "0123456")

        verify(screen, times(1)).showProgress()
        verify(screen, times(1)).showInvalidEmail()
        verify(screen, times(1)).finishProgress()
    }

    @Test
    fun `test error user with email not found`() {
        doThrow(CoreError(type = RepositoryError.INVALID_USER.toString()))
            .`when`(useCase).login("email@generic.com", "0123456")

        presenter.login("email@generic.com", "0123456")

        verify(screen, times(1)).showProgress()
        verify(screen, times(1)).showInvalidUser()
        verify(screen, times(1)).finishProgress()
    }

    @Test
    fun `test error invalid password`() {
        doThrow(CoreError(type = RepositoryError.ERROR_WRONG_PASSWORD.toString()))
            .`when`(useCase).login("email@generic.com", "0123456")

        presenter.login("email@generic.com", "0123456")

        verify(screen, times(1)).showProgress()
        verify(screen, times(1)).showInvalidPassword()
        verify(screen, times(1)).finishProgress()
    }
}