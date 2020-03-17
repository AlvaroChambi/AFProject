package es.developers.achambi.afines.home.usecase

import es.developers.achambi.afines.repositories.model.FirebaseTaxDate
import es.developers.achambi.afines.repositories.FirebaseRepository

class TaxesUseCase(private val repository: FirebaseRepository) {
    private var taxDates: List<FirebaseTaxDate> = ArrayList()

    fun getTaxDates(): List<FirebaseTaxDate> {
        if(taxDates.isEmpty()) {
            taxDates = repository.getTaxDates()
        }
        return taxDates
    }
}