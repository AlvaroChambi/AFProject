package es.developers.achambi.afines.repositories.model

import es.developers.achambi.afines.invoices.ui.Trimester
import java.util.*

class FirebaseTaxDate(var id: String = "",
                      var date: Date = Date(),
                      var name: String = "",
                      var trimester: Trimester = Trimester.EMPTY)
