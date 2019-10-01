package es.developers.achambi.afines.repositories.model

data class FirebaseInvoice(var id: Long = 0,
                           var name: String = "",
                           var trimester: String? = null,
                           var fileReference: String? = null,
                           var deliveredDate: Long = 0,
                           var processedDate: Long? = null,
                           var failedStatus: Boolean = false)