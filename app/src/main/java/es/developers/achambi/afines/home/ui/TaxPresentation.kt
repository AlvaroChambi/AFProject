package es.developers.achambi.afines.home.ui

import es.developers.achambi.afines.home.model.TaxDate

class TaxPresentation(val name: String,
                      val trimester: String,
                      val daysLeft: String,
                      val date: String)

class TaxPresentationBuilder {
    fun build(taxes: ArrayList<TaxDate>): ArrayList<TaxPresentation> {
        return ArrayList()
    }
    fun build(taxDate: TaxDate) {

    }
}