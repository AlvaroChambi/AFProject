package es.developers.achambi.afines.invoices.presenter

import es.developers.achambi.afines.OverviewScreen
import es.developers.achambi.afines.home.OverviewPresenter
import es.developers.achambi.afines.home.ui.TaxPresentationBuilder
import es.developers.achambi.afines.home.usecase.TaxesUseCase
import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import es.developers.achambi.afines.repositories.model.UserOverview
import es.developers.achambi.afines.utils.EventLogger
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*

class OverviewPresenterTest: BasePresenterTest() {
    private lateinit var presenter: OverviewPresenter
    @Mock
    private lateinit var screen: OverviewScreen
    @Mock
    private lateinit var profileUseCase: ProfileUseCase
    @Mock
    private lateinit var logger: EventLogger

    override fun setup() {
        super.setup()
        presenter = OverviewPresenter(screen, lifecycle, executor, profileUseCase,
            broadcastManager, logger)
    }

    @Test
    fun `test invalid ccc`() {
        `when`(profileUseCase.getUserOverview()).thenReturn( UserOverview( null,
            FirebaseProfile(iban = "iban", naf = "naf") ))

        presenter.onViewSetup()

        verify(screen, never()).showCCCValue("")
        verify(screen, times(1)).showIbanValue("IBAN")
        verify(screen, times(1)).showNAFValue("NAF")
    }

    @Test
    fun `test invalid naf`() {
        `when`(profileUseCase.getUserOverview()).thenReturn( UserOverview( null,
            FirebaseProfile(iban = "iban", ccc = "ccc")))

        presenter.onViewSetup()

        verify(screen, times(0)).showNAFValue("")
        verify(screen, times(1)).showIbanValue("IBAN")
        verify(screen, times(1)).showCCCValue("CCC")
    }

    @Test
    fun `test invalid iban`() {
        `when`(profileUseCase.getUserOverview()).thenReturn( UserOverview(null,
            FirebaseProfile(ccc = "ccc", naf = "naf")))

        presenter.onViewSetup()

        verify(screen, times(0)).showIbanValue("")
        verify(screen, times(1)).showCCCValue("CCC")
        verify(screen, times(1)).showNAFValue("NAF")
    }
}