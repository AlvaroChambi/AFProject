package es.developers.achambi.afines.home.model

import es.developers.achambi.afines.invoices.ui.Trimester
import java.util.*

class TaxDate(var id: String = "",
              var date: Date = Date(),
              var name: String = "",
              var trimester: Trimester = Trimester.EMPTY)