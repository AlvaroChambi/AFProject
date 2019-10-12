package es.developers.achambi.afines.invoices.model

import android.os.Parcel
import android.os.Parcelable
import es.developer.achambi.coreframework.utils.ParcelUtil
import es.developers.achambi.afines.invoices.ui.Trimester

data class Invoice(val id: Long,
    val name: String,
    val fileReference: String,
    val trimester: Trimester,
    val state: InvoiceState,
    val date: Long,
    val dbReference: String)
    : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        ParcelUtil.readEnumFromParcel(parcel, Trimester::class.java),
        ParcelUtil.readEnumFromParcel(parcel, InvoiceState::class.java),
        parcel.readLong(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(fileReference)
        ParcelUtil.writeEnumToParcel(parcel, trimester)
        ParcelUtil.writeEnumToParcel(parcel, state)
        parcel.writeLong(date)
        parcel.writeString(dbReference)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Invoice> {
        override fun createFromParcel(parcel: Parcel): Invoice {
            return Invoice(parcel)
        }

        override fun newArray(size: Int): Array<Invoice?> {
            return arrayOfNulls(size)
        }
    }

}

data class DetailedInvoice(val invoice: Invoice,
                           val fileName: String,
                           val mimeType: String,
                           val extension: String)

enum class InvoiceState {
    DELIVERED,
    PROCESSED,
    FAILED
}