package es.developers.achambi.afines.home.usecase

import es.developers.achambi.afines.home.model.TaxDate
import es.developers.achambi.afines.repositories.FirebaseRepository

class TaxesUseCase(private val repository: FirebaseRepository) {
    fun getTaxDates(): List<TaxDate> {
        return repository.getTaxDates()
    }
}