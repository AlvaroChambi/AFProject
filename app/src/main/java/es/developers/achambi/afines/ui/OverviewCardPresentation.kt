package es.developers.achambi.afines.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import es.developers.achambi.afines.R
import es.developers.achambi.afines.home.NotificationPresentation
import es.developers.achambi.afines.repositories.model.NotificationType
import es.developers.achambi.afines.repositories.model.OverviewNotification
import es.developers.achambi.afines.repositories.model.UserOverview
import java.util.*
import java.util.concurrent.TimeUnit

data class OverviewPresentation(val ccc: String, val iban: String, val naf: String,
                                val pendingCount: String,
                                val rejectedCount: String,
                                val approvedCount: String,
                                val notification: String,
                                val type: NotificationType)

class OverviewPresentationBuilder(val context: Context,
                                  private val notificationBuilder: NotificationPresentationBuilder) {
    @SuppressLint("DefaultLocale")
    fun build(overview: UserOverview): OverviewPresentation {
        var ccc = ""
        var iban = ""
        var naf = ""
        overview.profile?.let {
            ccc = it.ccc.toUpperCase()
            iban = it.iban.toUpperCase()
            naf = it.naf.toUpperCase()
        }
        var approved = "-"
        var rejected = "-"
        var pending = "-"
        overview.counters?.let {
            approved = it.approved.toString()
            rejected = it.rejected.toString()
            pending = it.pending.toString()
        }
        var notification = ""
        var type = NotificationType.NONE
        overview.notification?.let {
            type = it.type
            notification = notificationBuilder.build(it).message
        }
        return OverviewPresentation( ccc = ccc, naf = naf, iban = iban,
            approvedCount = approved, rejectedCount = rejected, pendingCount = pending,
            notification = notification, type = type)
    }
}

class NotificationPresentationBuilder(private val context: Context) {
    fun build(notification: OverviewNotification): NotificationPresentation {
        var goToVisibility = View.VISIBLE
        val notificationText = when(notification.type) {
                NotificationType.INVOICE_REJECTED ->
                    context.getString(R.string.overview_rejected_invoices_message_text)
                NotificationType.PASS_NOT_UPDATED ->
                    context.getString(R.string.overview_password_change_message)
                NotificationType.TAX_DATE_REMINDER -> {
                    goToVisibility = View.GONE
                    val left = notification.date!!.time - Date().time
                    val daysLeft = TimeUnit.MILLISECONDS.toDays(left)
                    context.resources.getQuantityString(R.plurals.taxes_days_left_text,
                        daysLeft.toInt(), daysLeft.toString(), notification.name)
                }
                else -> ""
        }
        return NotificationPresentation(notification.date.time, notificationText, notification.type,
            goToVisibility)
    }
}