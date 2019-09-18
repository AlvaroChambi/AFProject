package es.developers.achambi.afines.invoices.usecase

import android.net.Uri
import es.developers.achambi.afines.repositories.FirebaseRepository
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceState
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.ui.Trimester
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
            invoices.add(buildInvoice(item))
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

    private fun buildInvoice(firebaseInvoice: FirebaseInvoice): Invoice {
        return Invoice(
            firebaseInvoice.id.toInt(),
            firebaseInvoice.name,
            resolveTrimester(firebaseInvoice.trimester),
            resolveState(firebaseInvoice.processedDate, firebaseInvoice.failedStatus),
            resolveDate(firebaseInvoice.deliveredDate, firebaseInvoice.processedDate)
        )
    }

    private fun resolveDate(deliveredDate: Long, processedDate: Long?): Date {
        if(processedDate != null) return Date(processedDate)
        return Date(deliveredDate)
    }

    private fun resolveTrimester(trimester: String?): Trimester {
        return if(trimester != null) {
            Trimester.valueOf(trimester)
        } else {
            Trimester.EMPTY
        }
    }

    private fun resolveState(processedDate: Long?, failed: Boolean): InvoiceState {
        return if(failed) {
            InvoiceState.FAILED
        } else if(processedDate != null){
            InvoiceState.PROCESSED
        } else {
            InvoiceState.DELIVERED
        }
    }
}