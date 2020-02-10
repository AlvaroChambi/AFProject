package es.developers.achambi.afines.invoices.presenter

import es.developers.achambi.afines.OverviewScreen
import es.developers.achambi.afines.home.OverviewPresenter
import es.developers.achambi.afines.home.ui.TaxPresentationBuilder
import es.developers.achambi.afines.home.usecase.TaxesUseCase
import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import es.developers.achambi.afines.utils.EventLogger
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

class OverviewPresenterTest: BasePresenterTest() {
    private lateinit var presenter: OverviewPresenter
    @Mock
    private lateinit var screen: OverviewScreen
    @Mock
    private lateinit var profileUseCase: ProfileUseCase
    @Mock
    private lateinit var taxesUseCase: TaxesUseCase
    @Mock
    private lateinit var builder: TaxPresentationBuilder
    @Mock
    private lateinit var logger: EventLogger

    override fun setup() {
        super.setup()
        presenter = OverviewPresenter(screen, lifecycle, executor, profileUseCase, taxesUseCase,
            broadcastManager, builder, logger)
    }

    @Test
    fun `test rejected notification should be displayed`() {
        `when`(profileUseCase.getUserProfile(false)).thenReturn(FirebaseProfile(rejected = 1))

        presenter.onViewSetup()

        verify(screen, times(1)).showRejectInvoicesNotification()
    }

    @Test
    fun `test password updated pending`() {
        `when`(profileUseCase.getUserProfile(false)).thenReturn(FirebaseProfile(passwordChanged = false))

        presenter.onViewSetup()

        verify(screen, times(1)).showUpdatePasswordNotification()
    }

    @Test
    fun `test tax dates request success`() {
        `when`(taxesUseCase.getTaxDates()).thenReturn(ArrayList())

        presenter.onViewSetup()

        verify(screen, times(1)).showTaxDates(ArrayList())
    }
}