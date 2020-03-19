package es.developers.achambi.afines.repositories.model

data class FirebaseCounters(var pending: Int = 0,
                           var approved: Int = 0,
                           var rejected: Int = 0,
                           var accounted: Int = 0,
                           var reference: String = "",
                           var year: String = "",
                           var trimester: String = "")