package es.developers.achambi.afines.invoices.usecase

import android.net.Uri
import es.developer.achambi.coreframework.utils.URIMetadata
import es.developers.achambi.afines.FirebaseRepository
import es.developers.achambi.afines.invoices.model.Invoice

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
        listResult.items.forEach { item ->
            invoices.add(
                Invoice(
                    item.hashCode(),
                    item.name
                )
            )
        }
        return invoices
    }

    fun uploadUserFiles(uri: Uri, fileName: String) {
        firebaseRepository.uploadUserFile(uri, fileName)
    }
}