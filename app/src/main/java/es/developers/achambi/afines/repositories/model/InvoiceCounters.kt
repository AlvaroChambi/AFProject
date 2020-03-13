package es.developers.achambi.afines.repositories.model

import java.util.*

data class InvoiceCounters(var pending: Int = 0,
                           var approved: Int = 0,
                           var rejected: Int = 0,
                           var accounted: Int = 0,
                           var reference: String = "",
                           var year: String = "",
                           var trimester: String = "")

data class UserOverview( var counters: InvoiceCounters?,
                         var profile: FirebaseProfile?,
                         var notification: OverviewNotification?)

data class OverviewNotification( var type: NotificationType,
                                 var name: String = "", var date: Date )

enum class NotificationType {
    INVOICE_REJECTED,
    PASS_NOT_UPDATED,
    TAX_DATE_REMINDER,
    NONE
}