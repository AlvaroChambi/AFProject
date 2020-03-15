package es.developers.achambi.afines.invoices.ui

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.format.DateUtils
import androidx.core.content.ContextCompat
import es.developer.achambi.coreframework.ui.presentation.SearchListData
import es.developers.achambi.afines.R
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.repositories.model.InvoiceState
import kotlin.collections.ArrayList

data class InvoicePresentation(
    private val keyId: Long,
    val name: String,
    val trimester: String,
    val stateMessage: String,
    val stateColor: Int,
    val stateDetails: String,
    val showFailedDetails: Boolean,
    val sort: Long)
    : SearchListData, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong()
    )

    override fun getId(): Long {
        return keyId
    }

    override fun sortValue(): Long {
        return sort
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(trimester)
        parcel.writeString(stateMessage)
        parcel.writeInt(stateColor)
        parcel.writeString(stateDetails)
        parcel.writeByte(if (showFailedDetails) 1 else 0)
        parcel.writeLong(sort)
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

data class InvoiceDetailsPresentation(val id: Long,
                                      val name: String,
                                      val state: String,
                                      val stateMessage: String,
                                      val stateMessageColor: Int,
                                      val failed: Boolean,
                                      val processed: Boolean,
                                      val error: Boolean)

class InvoiceDetailsPresentationBuilder(
    private val context: Context, private val invoicePresentationBuilder: InvoicePresentationBuilder){
    fun build(invoice: Invoice): InvoiceDetailsPresentation {
        val basePresentation = invoicePresentationBuilder.build(invoice)
        val stateMessage = when(invoice.state) {
            InvoiceState.SENT -> context.getString(R.string.invoice_details_sent_message)
            InvoiceState.ACCEPTED -> context.getString(R.string.invoice_details_approved_message)
            InvoiceState.REJECTED -> context.getString(R.string.invoice_details_rejected_message)
            InvoiceState.ACCOUNTED -> context.getString(R.string.invoice_details_approved_message)
            null -> ""
        }
        return InvoiceDetailsPresentation(
            basePresentation.id,
            basePresentation.name,
            basePresentation.stateMessage,
            stateMessage,
            basePresentation.stateColor,
            basePresentation.showFailedDetails,
            (invoice.state == InvoiceState.ACCEPTED 
                    || invoice.state == InvoiceState.ACCOUNTED),
            false
        )
    }

    fun buildError(): InvoiceDetailsPresentation {
        return InvoiceDetailsPresentation(0,"", "", "",0, false,
            processed = false, error = true)
    }
}

class InvoicePresentationBuilder(private val context: Context) {
     fun build(invoice: Invoice): InvoicePresentation {
        return InvoicePresentation(invoice.id,
            invoice.name.substring(0, invoice.name.indexOf('.')),
            buildTrimesterText(context, invoice.trimester),
            buildStateMessage(context, invoice.state),
            buildStateMessageColor(context, invoice.state),
            buildStateDetails(context, invoice.state, invoice.id),
            invoice.state == InvoiceState.REJECTED,
            buildSortValue(invoice.state))
    }

    private fun buildSortValue(state: InvoiceState?): Long {
        return if(state != null) {
            when(state) {
                InvoiceState.SENT -> 1
                InvoiceState.ACCEPTED -> 2
                InvoiceState.REJECTED -> 0
                InvoiceState.ACCOUNTED -> 2
            }
        } else {
            0
        }
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

    private fun buildStateMessage(context: Context, state: InvoiceState?): String {
        return when(state) {
            InvoiceState.SENT -> context.getString(R.string.invoice_state_delivered)
            InvoiceState.ACCEPTED -> context.getString(R.string.invoice_state_processed)
            InvoiceState.REJECTED -> context.getString(R.string.invoice_state_failed)
            InvoiceState.ACCOUNTED -> context.getString(R.string.invoice_state_accounted)
            else -> ""
        }
    }

    private fun buildStateMessageColor(context: Context, invoiceState: InvoiceState?): Int {
        return when(invoiceState) {
            InvoiceState.SENT -> ContextCompat.getColor(context, R.color.color_state_pending)
            InvoiceState.ACCEPTED -> ContextCompat.getColor(context, R.color.color_state_approved)
            InvoiceState.REJECTED -> ContextCompat.getColor(context, R.color.color_state_failed)
            InvoiceState.ACCOUNTED -> ContextCompat.getColor(context, R.color.color_state_approved)
            else -> ContextCompat.getColor(context, R.color.color_state_approved)
        }
    }

    private fun buildStateDetails(context: Context, invoiceState: InvoiceState?, date: Long): String {
        return when(invoiceState) {
            InvoiceState.SENT,
            InvoiceState.ACCEPTED,
            InvoiceState.REJECTED,
            InvoiceState.ACCOUNTED,
            null -> DateUtils.formatDateTime(context, date,
                DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE or
                        DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_ABBREV_ALL)
        }
    }
}