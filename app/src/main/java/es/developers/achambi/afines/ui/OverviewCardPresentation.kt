package es.developers.achambi.afines.ui

import android.annotation.SuppressLint
import android.content.Context
import es.developers.achambi.afines.R
import es.developers.achambi.afines.repositories.model.NotificationType
import es.developers.achambi.afines.repositories.model.UserOverview
import java.util.*
import java.util.concurrent.TimeUnit

data class OverviewPresentation(val ccc: String, val iban: String, val naf: String,
                                val pendingCount: String,
                                val rejectedCount: String,
                                val approvedCount: String,
                                val notification: String,
                                val type: NotificationType)

class OverviewPresentationBuilder(val context: Context) {
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
            notification = when(it.type) {
                NotificationType.INVOICE_REJECTED ->
                    context.getString(R.string.overview_rejected_invoices_message_text)
                NotificationType.PASS_NOT_UPDATED ->
                    context.getString(R.string.overview_password_change_message)
                NotificationType.TAX_DATE_REMINDER -> {
                    val left = it.date!!.time - Date().time
                    val daysLeft = TimeUnit.MILLISECONDS.toDays(left)
                    context.resources.getQuantityString(R.plurals.taxes_days_left_text,
                        daysLeft.toInt(), daysLeft.toString(), it.name)
                }
                else -> ""
            }
        }
        return OverviewPresentation( ccc = ccc, naf = naf, iban = iban,
            approvedCount = approved, rejectedCount = rejected, pendingCount = pending,
            notification = notification, type = type)
    }
}