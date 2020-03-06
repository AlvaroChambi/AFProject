package es.developers.achambi.afines.repositories.model

//TODO Delete trimester explicit reference
data class FirebaseInvoice(var id: Long = 0,
                           var name: String = "",
                           var fileReference: String? = null,
                           var deliveredDate: Long = 0,
                           var processedDate: Long? = null,
                           var state: String? = null,
                           var dbPath: String = "")