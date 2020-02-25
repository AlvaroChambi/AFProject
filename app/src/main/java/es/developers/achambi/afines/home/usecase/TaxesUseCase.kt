package es.developers.achambi.afines.home.usecase

import es.developers.achambi.afines.home.model.TaxDate
import es.developers.achambi.afines.repositories.FirebaseRepository

class TaxesUseCase(private val repository: FirebaseRepository) {
    private var taxDates: List<TaxDate> = ArrayList()

    fun getTaxDates(): List<TaxDate> {
        if(taxDates.isEmpty()) {
            taxDates = repository.getTaxDates()
        }
        return taxDates
    }
}