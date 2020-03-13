package es.developers.achambi.afines.profile.usecase

import android.content.SharedPreferences
import es.developers.achambi.afines.home.model.TaxDate
import es.developers.achambi.afines.home.usecase.TaxesUseCase
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase
import es.developers.achambi.afines.repositories.FirebaseRepository
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import es.developers.achambi.afines.repositories.model.InvoiceCounters
import es.developers.achambi.afines.repositories.model.NotificationType
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test

import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ProfileUseCaseTest {
    private lateinit var useCase: ProfileUseCase
    @Mock
    private lateinit var repository: FirebaseRepository
    @Mock
    private lateinit var invoicesUseCase: InvoiceUseCase
    @Mock
    private lateinit var countersUseCase: CountersUseCase
    @Mock
    private lateinit var taxesUseCase: TaxesUseCase
    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        useCase = ProfileUseCase(repository, invoicesUseCase, countersUseCase,
            taxesUseCase, sharedPreferences)
    }

    @Test
    fun `test rejected notification priority`() {
        val taxes = ArrayList<TaxDate>()
        taxes.add( TaxDate() )
        `when`(taxesUseCase.getTaxDates()).thenReturn(taxes)
        `when`(repository.retrieveCurrentUser()).thenReturn(FirebaseProfile(passwordChanged = false))
        `when`(countersUseCase.getCounters()).thenReturn(InvoiceCounters(rejected = 1))

        val result = useCase.getUserOverview()

        assertEquals( NotificationType.INVOICE_REJECTED, result.notification!!.type )
    }

    @Test
    fun `test pending update priority`() {
        val taxes = ArrayList<TaxDate>()
        taxes.add( TaxDate() )
        `when`(taxesUseCase.getTaxDates()).thenReturn(taxes)
        `when`(repository.retrieveCurrentUser()).thenReturn(FirebaseProfile(passwordChanged = false))

        val result = useCase.getUserOverview()

        assertEquals( NotificationType.PASS_NOT_UPDATED, result.notification!!.type )
    }

    @Test
    fun `test default notification`() {
        val taxes = ArrayList<TaxDate>()
        taxes.add( TaxDate() )
        `when`(taxesUseCase.getTaxDates()).thenReturn(taxes)

        val result = useCase.getUserOverview()

        assertEquals( NotificationType.TAX_DATE_REMINDER, result.notification!!.type )
    }
}