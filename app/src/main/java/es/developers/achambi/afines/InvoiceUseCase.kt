package es.developers.achambi.afines

import es.developer.achambi.coreframework.utils.URIMetadata

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
            invoices.add( Invoice(item.hashCode(),
                item.name) )
        }
        return invoices
    }

    fun uploadUserFiles(uriMetadata: URIMetadata) {
        firebaseRepository.uploadUserFile(uriMetadata.uri, uriMetadata.displayName)
    }
}