package es.developers.achambi.afines

import androidx.test.espresso.IdlingResource
import es.developer.achambi.coreframework.threading.MainExecutor

class ExecutorIdlingResource(private val executor: MainExecutor): IdlingResource {
    var isIdle = false
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String? {
        return ExecutorIdlingResource::class.java.name
    }

    override fun isIdleNow(): Boolean {
        isIdle = executor.activeCount == 0
        if (isIdle) {
            resourceCallback?.onTransitionToIdle()
        }
        return isIdle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        resourceCallback = callback
    }
}