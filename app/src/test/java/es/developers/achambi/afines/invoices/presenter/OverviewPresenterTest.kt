package es.developers.achambi.afines.invoices.presenter

import es.developers.achambi.afines.OverviewScreen
import es.developers.achambi.afines.home.OverviewPresenter
import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import es.developers.achambi.afines.repositories.model.NotificationType
import es.developers.achambi.afines.repositories.model.UserOverview
import es.developers.achambi.afines.ui.OverviewPresentation
import es.developers.achambi.afines.ui.OverviewPresentationBuilder
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
    private lateinit var logger: EventLogger
    @Mock
    private lateinit var builder: OverviewPresentationBuilder
    override fun setup() {
        super.setup()
        presenter = OverviewPresenter(screen, lifecycle, executor, profileUseCase,
            broadcastManager, builder, logger)
    }

    @Test
    fun `test invalid ccc`() {
        val overview = UserOverview( null,
            FirebaseProfile(), null )
        `when`(profileUseCase.getUserOverview()).thenReturn(overview)
        `when`(builder.build(overview)).thenReturn(OverviewPresentation("", "IBAN", "NAF",
            "", "", "", "", NotificationType.NONE))

        presenter.onViewSetup()

        verify(screen, never()).showCCCValue("")
        verify(screen, times(1)).showIbanValue("IBAN")
        verify(screen, times(1)).showNAFValue("NAF")
    }

    @Test
    fun `test invalid naf`() {
        val overview = UserOverview( null,
            FirebaseProfile(), null )
        `when`(profileUseCase.getUserOverview()).thenReturn(overview)
        `when`(builder.build(overview)).thenReturn(OverviewPresentation("CCC", "IBAN", "",
            "", "", "", "", NotificationType.NONE))

        presenter.onViewSetup()

        verify(screen, times(0)).showNAFValue("")
        verify(screen, times(1)).showIbanValue("IBAN")
        verify(screen, times(1)).showCCCValue("CCC")
    }

    @Test
    fun `test invalid iban`() {
        val overview = UserOverview( null,
            FirebaseProfile(), null )
        `when`(profileUseCase.getUserOverview()).thenReturn(overview)
        `when`(builder.build(overview)).thenReturn(OverviewPresentation("CCC", "", "NAF",
            "", "", "", "", NotificationType.NONE))

        presenter.onViewSetup()

        verify(screen, times(0)).showIbanValue("")
        verify(screen, times(1)).showCCCValue("CCC")
        verify(screen, times(1)).showNAFValue("NAF")
    }
}