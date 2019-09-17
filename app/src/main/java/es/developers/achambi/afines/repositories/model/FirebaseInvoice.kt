package es.developers.achambi.afines.repositories.model

data class FirebaseInvoice(var id: Long = 0,
                           var name: String = "",
                           var trimester: String? = null,
                           var downloadUrl: String? = null,
                           var deliveredDate: Long? = null,
                           var processedDate: Long? = null,
                           var failedStatus: Boolean? = null)