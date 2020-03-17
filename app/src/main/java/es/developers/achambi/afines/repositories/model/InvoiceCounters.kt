package es.developers.achambi.afines.repositories.model

import java.util.*

data class UserOverview( var counters: FirebaseCounters?,
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