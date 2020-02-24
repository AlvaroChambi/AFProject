package es.developers.achambi.afines.repositories.model

data class InvoiceCounters(var pending: Int = 0,
                           var approved: Int = 0,
                           var rejected: Int = 0)

data class UserOverview( var counters: InvoiceCounters?,
                         var profile: FirebaseProfile?)