package es.developers.achambi.afines

import android.app.Activity
import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingResource
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import es.developers.achambi.afines.utils.BaseTestPresenter
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
abstract class BaseTest {
    private lateinit var presenter: BaseTestPresenter
    private lateinit var idlingResource : IdlingResource
    @Before
    open fun setup() {
        presenter = AfinesApplication.baseTestPresenterFactory.build()
        idlingResource = ExecutorIdlingResource(AfinesApplication.executor)
        Espresso.registerIdlingResources(idlingResource)
        presenter.clearCache()
    }

    open fun beforeActivity(){}
    open fun provideActivityIntent(): Intent{
        return Intent()
    }

    @After
    fun after() {
        Espresso.unregisterIdlingResources(idlingResource)
    }
}

class TestRule<T : Activity?>(activityClass: Class<T>?,
                              private val baseTest: BaseTest)
    : ActivityTestRule<T>(activityClass, false) {
    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        baseTest.beforeActivity()
    }

    override fun getActivityIntent(): Intent {
        return baseTest.provideActivityIntent()
    }
}