package es.developers.achambi.afines.invoices.usecase

import android.net.Uri
import es.developers.achambi.afines.repositories.FirebaseRepository
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.repositories.model.FirebaseInvoice
import java.util.*
import kotlin.collections.ArrayList

class InvoiceUseCase(private val firebaseRepository: FirebaseRepository) {
    private val invoices = ArrayList<Invoice>()

    fun queryUserInvoices(refresh: Boolean): ArrayList<Invoice> {
        if(refresh) {
            invoices.clear()
        }
        if(invoices.isNotEmpty()) {
            return invoices
        }
        val listResult = firebaseRepository.userInvoices()
        listResult.forEach { item ->
            invoices.add(
                Invoice(
                    item.hashCode(),
                    item.name
                )
            )
        }
        return invoices
    }

    fun uploadUserFiles(uri: Uri, invoiceUpload: InvoiceUpload) {
        firebaseRepository.uploadUserFile(uri, buildPostInvoice(invoiceUpload))
    }

    private fun buildPostInvoice(invoiceUpload: InvoiceUpload): FirebaseInvoice {
        return FirebaseInvoice(Date().time,
            invoiceUpload.name,
            invoiceUpload.trimester.toString(),
            invoiceUpload.uriMetadata.displayName,
            Date().time)
    }
}