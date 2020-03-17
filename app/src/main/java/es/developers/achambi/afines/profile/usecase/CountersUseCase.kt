package es.developers.achambi.afines.profile.usecase

import es.developer.achambi.coreframework.threading.CoreError
import es.developers.achambi.afines.invoices.ui.TrimesterUtils
import es.developers.achambi.afines.repositories.FirebaseRepository
import es.developers.achambi.afines.repositories.model.FirebaseCounters
import java.util.*

class CountersUseCase(private val respository: FirebaseRepository) {
    private var counters: FirebaseCounters? = null

    /** Fetch counters if available and creates them with default values when they're not **/
    @Throws(CoreError::class)
    fun setupCounters(): FirebaseCounters? {
        val trimester = TrimesterUtils.getCurrentTrimester().toString()
        val year = Calendar.getInstance().get(Calendar.YEAR).toString()
        counters = respository.getCounters(trimester = trimester, year = year)
        if(counters == null) {
            counters = respository.createCounters(trimester = trimester, year = year)
        }
        return counters
    }

    @Throws(CoreError::class)
    fun getCounters(): FirebaseCounters? {
        val trimester = TrimesterUtils.getCurrentTrimester().toString()
        val year = Calendar.getInstance().get(Calendar.YEAR).toString()
        counters = respository.getCounters(trimester = trimester, year = year)
        return counters
    }

    fun countersReference(): String? {
        return counters?.reference
    }

    fun clearCache() {
        counters = null
    }
}