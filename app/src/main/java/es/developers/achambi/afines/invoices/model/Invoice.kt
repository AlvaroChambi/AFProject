package es.developers.achambi.afines.invoices.model

import es.developers.achambi.afines.invoices.ui.Trimester
import java.util.*

data class Invoice(val id: Int,
    val name: String,
    val trimester: Trimester,
    val state: InvoiceState,
    val date: Date
)

enum class InvoiceState {
    DELIVERED,
    PROCESSED,
    FAILED
}