package es.developers.achambi.afines.invoices.model

import android.os.Parcel
import android.os.Parcelable
import es.developer.achambi.coreframework.utils.ParcelUtil
import es.developer.achambi.coreframework.utils.URIMetadata
import es.developers.achambi.afines.invoices.ui.Trimester
import es.developers.achambi.afines.invoices.ui.TrimesterUtils

data class InvoiceUpload(var uriMetadata: URIMetadata = URIMetadata(),
                         var name: String = "") : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(URIMetadata::class.java.classLoader)!!,
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(uriMetadata, flags)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InvoiceUpload> {
        override fun createFromParcel(parcel: Parcel): InvoiceUpload {
            return InvoiceUpload(parcel)
        }

        override fun newArray(size: Int): Array<InvoiceUpload?> {
            return arrayOfNulls(size)
        }
    }
}