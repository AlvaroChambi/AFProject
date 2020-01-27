package es.developers.achambi.afines.invoices.presenter

import android.content.Context
import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.MockExecutor
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
abstract class BasePresenterTest {
    @Mock
    protected lateinit var context: Context
    protected val executor = MockExecutor()
    @Mock
    protected lateinit var lifecycle: Lifecycle
    @Mock
    private lateinit var state : Lifecycle.State

    @Before
    open fun setup() {
        `when`(lifecycle.currentState).thenReturn(state)
        `when`(state.isAtLeast(Lifecycle.State.STARTED)).thenReturn(true)
    }
}