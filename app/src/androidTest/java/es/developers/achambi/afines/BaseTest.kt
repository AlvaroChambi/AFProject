package es.developers.achambi.afines

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingResource
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
open class BaseTest {
    private lateinit var idlingResource : IdlingResource
    @Before
    fun setup() {
        idlingResource = ExecutorIdlingResource(AfinesApplication.executor)
        Espresso.registerIdlingResources(idlingResource)
    }
}