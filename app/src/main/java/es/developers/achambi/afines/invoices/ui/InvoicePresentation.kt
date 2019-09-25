package es.developers.achambi.afines.invoices.ui

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.format.DateUtils
import androidx.core.content.ContextCompat
import es.developer.achambi.coreframework.ui.presentation.SearchListData
import es.developers.achambi.afines.R
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceState
import kotlin.collections.ArrayList

data class InvoicePresentation(
    val id: Int,
    val name: String,
    val trimester: String,
    val stateMessage: String,
    val stateColor: Int,
    val stateDetails: String,
    val showFailedDetails: Boolean)
    : SearchListData, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun getId(): Long {
        return id.toLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(trimester)
        parcel.writeString(stateMessage)
        parcel.writeInt(stateColor)
        parcel.writeString(stateDetails)
        parcel.writeByte(if (showFailedDetails) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InvoicePresentation> {
        override fun createFromParcel(parcel: Parcel): InvoicePresentation {
            return InvoicePresentation(parcel)
        }

        override fun newArray(size: Int): Array<InvoicePresentation?> {
            return arrayOfNulls(size)
        }
    }
}

data class InvoiceDetailsPresentation(val id: Int,
                                      val name: String,
                                      val trimester: String,
                                      val stateMessage: String,
                                      val stateMessageColor: Int,
                                      val failed: Boolean,
                                      val processed: Boolean)

class InvoiceDetailsPresentationBuilder(
    private val context: Context, private val invoicePresentationBuilder: InvoicePresentationBuilder){
    fun build(invoice: Invoice): InvoiceDetailsPresentation {
        val basePresentation = invoicePresentationBuilder.build(invoice)
        return InvoiceDetailsPresentation(
            basePresentation.id,
            basePresentation.name,
            basePresentation.trimester,
            basePresentation.stateMessage + basePresentation.stateDetails,
            basePresentation.stateColor,
            basePresentation.showFailedDetails,
            invoice.state == InvoiceState.PROCESSED
        )
    }
}

class InvoicePresentationBuilder(private val context: Context) {
     fun build(invoice: Invoice): InvoicePresentation {
        return InvoicePresentation(invoice.id,
            invoice.name,
            buildTrimesterText(context, invoice.trimester),
            buildStateMessage(context, invoice.state),
            buildStateMessageColor(context, invoice.state),
            buildStateDetails(context, invoice.state, invoice.date),
            invoice.state == InvoiceState.FAILED)
    }

    fun build(invoices: ArrayList<Invoice>): ArrayList<InvoicePresentation> {
        val result = ArrayList<InvoicePresentation>()
        invoices.forEach{
            result.add( build(it) )
        }
        return result
    }

    private fun buildTrimesterText(context: Context, trimester: Trimester): String {
        return when(trimester) {
            Trimester.FIRST_TRIMESTER -> context.getString(R.string.upload_first_trimester_text)
            Trimester.SECOND_TRIMESTER -> context.getString(R.string.upload_second_trimester_text)
            Trimester.THIRD_TRIMESTER -> context.getString(R.string.upload_third_trimester_text)
            Trimester.FORTH_TRIMESTER -> context.getString(R.string.upload_forth_trimester_text)
            Trimester.EMPTY -> context.getString(R.string.upload_empty_trimester_text)
        }
    }

    private fun buildStateMessage(context: Context, state: InvoiceState): String {
        return when(state) {
            InvoiceState.DELIVERED -> context.getString(R.string.invoice_state_delivered)
            InvoiceState.PROCESSED -> context.getString(R.string.invoice_state_processed)
            InvoiceState.FAILED -> context.getString(R.string.invoice_state_failed)
        }
    }

    private fun buildStateMessageColor(context: Context, invoiceState: InvoiceState): Int {
        return when(invoiceState) {
            InvoiceState.DELIVERED -> ContextCompat.getColor(context, R.color.color_state_pending)
            InvoiceState.PROCESSED -> ContextCompat.getColor(context, R.color.color_state_approved)
            InvoiceState.FAILED -> ContextCompat.getColor(context, R.color.color_state_failed)
        }
    }

    private fun buildStateDetails(context: Context, invoiceState: InvoiceState, date: Long): String {
        return when(invoiceState) {
            InvoiceState.PROCESSED,
            InvoiceState.DELIVERED,
            InvoiceState.FAILED -> DateUtils.formatDateTime(context, date,
                DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE or
                        DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_ABBREV_ALL)
        }
    }
}