package es.developers.achambi.afines.invoices.usecase

import android.net.Uri
import es.developers.achambi.afines.invoices.model.DetailedInvoice
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

    fun getDetailedInvoice(invoiceId: Long): DetailedInvoice? {
        val invoice = getInvoice(invoiceId)
        val metadata = invoice?.fileReference.let { it?.let { it1 -> firebaseRepository.getFileMetadata(it1) } }
        return invoice?.let {
            DetailedInvoice(
                it,
                metadata?.name ?: "",
                metadata?.contentType ?: "",
                metadata?.contentType ?: ""
            )
        }
    }

    fun deleteInvoice(invoiceId: Long) {
        val invoice = getInvoice(invoiceId)
        invoice?.let { it?.let { it1 -> firebaseRepository.deleteInvoice(it1) } }
    }

    fun getFileBytes(invoiceId: Long): ByteArray? {
        val invoice = getInvoice(invoiceId)
        return invoice?.fileReference?.let { firebaseRepository.getFilesBytes(it) }
    }

    fun getInvoice(invoiceId: Long): Invoice? {
        val result = invoices.find { it.id == invoiceId }
        if(result == null){
            invoices.addAll(queryUserInvoices(false))
        }
        return invoices.find { it.id == invoiceId }
    }

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
            firebaseInvoice.id,
            firebaseInvoice.name,
            firebaseInvoice.fileReference?: "",
            resolveTrimester(firebaseInvoice.trimester),
            resolveState(firebaseInvoice.processedDate, firebaseInvoice.failedStatus),
            resolveDate(firebaseInvoice.deliveredDate, firebaseInvoice.processedDate),
            firebaseInvoice.dbPath
        )
    }

    private fun resolveDate(deliveredDate: Long, processedDate: Long?): Long {
        if(processedDate != null) return processedDate
        return deliveredDate
    }

    private fun resolveTrimester(trimester: String?): Trimester {
        return if(trimester != null) {
            Trimester.valueOf(trimester)
        } else {
            Trimester.EMPTY
        }
    }

    private fun resolveState(processedDate: Long?, failed: Boolean): InvoiceState {
        return when {
            failed -> InvoiceState.FAILED
            processedDate != null -> InvoiceState.PROCESSED
            else -> InvoiceState.DELIVERED
        }
    }
}