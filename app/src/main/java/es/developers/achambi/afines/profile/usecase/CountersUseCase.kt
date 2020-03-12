package es.developers.achambi.afines.profile.usecase

import es.developer.achambi.coreframework.threading.CoreError
import es.developers.achambi.afines.invoices.ui.TrimesterUtils
import es.developers.achambi.afines.repositories.FirebaseRepository
import es.developers.achambi.afines.repositories.model.InvoiceCounters
import java.util.*

class CountersUseCase(private val respository: FirebaseRepository) {
    private var counters: InvoiceCounters? = null

    /** Fetch counters if available and creates them with default values when they're not **/
    @Throws(CoreError::class)
    fun setupCounters() {
        val trimester = TrimesterUtils.getCurrentTrimester().toString()
        val year = Calendar.getInstance().get(Calendar.YEAR).toString()
        counters = respository.getCounters(trimester = trimester, year = year)
        if(counters == null) {
            counters = respository.createCounters(trimester = trimester, year = year)
        }
    }

    @Throws(CoreError::class)
    fun getCounters(): InvoiceCounters? {
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