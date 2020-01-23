package es.developers.achambi.afines.invoices.usecase

import android.net.Uri
import es.developers.achambi.afines.AfinesApplication
import es.developers.achambi.afines.invoices.model.DetailedInvoice
import es.developers.achambi.afines.repositories.FirebaseRepository
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.ui.Trimester
import es.developers.achambi.afines.repositories.model.FirebaseInvoice
import es.developers.achambi.afines.repositories.model.InvoiceState
import java.util.*
import kotlin.collections.ArrayList

class InvoiceUseCase(private val firebaseRepository: FirebaseRepository) {
    private val invoices = ArrayList<Invoice>()

    fun clearCache() {
        invoices.clear()
    }

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

    fun updateInvoice(uri: Uri?, invoiceUpload: InvoiceUpload, invoiceId: Long) {
        val invoice = getInvoice(invoiceId)
        invoice?.let { firebaseRepository.updateInvoiceMetadata(invoice, invoiceUpload.name,
            invoiceUpload.trimester.toString()) }
        if(uri != null) {
            invoice?.let { firebaseRepository.updateInvoiceFile(invoice, invoiceUpload, uri) }
            invoice?.let{ firebaseRepository.updateInvoiceState(invoice,
                InvoiceState.SENT.toString(), Date().time) }

            AfinesApplication.profileUseCase.increasePendingCount()
            AfinesApplication.profileUseCase.decreaseRejectedCount()
        }
    }

    fun deleteInvoice(invoiceId: Long) {
        val invoice = getInvoice(invoiceId)
        invoice?.let {
            firebaseRepository.deleteInvoice(it)
            AfinesApplication.profileUseCase.decreasePendingCount()
        }
    }

    fun getFileBytes(invoiceId: Long): ByteArray? {
        val invoice = getInvoice(invoiceId)
        return invoice?.fileReference?.let { firebaseRepository.getFilesBytes(it) }
    }

    fun getDownloadUrl(invoiceId: Long): Uri? {
        val invoice = getInvoice(invoiceId)
        return invoice?.fileReference?.let{ firebaseRepository.getDownloadUrl(it) }
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
        listResult.forEach { firebaseInvoice ->
            invoices.add(
                Invoice(firebaseInvoice.id,
                firebaseInvoice.name,
                firebaseInvoice.fileReference?: "",
                resolveTrimester(firebaseInvoice.trimester),
                firebaseInvoice.state?.let { InvoiceState.valueOf(it) },
                resolveDate(firebaseInvoice.deliveredDate, firebaseInvoice.processedDate),
                firebaseInvoice.dbPath))
        }
        return invoices
    }

    fun queryUserInvoices(query: String): ArrayList<Invoice> {
        val filteredArray = ArrayList<Invoice>()
        invoices.forEach { invoice ->
            if(invoice.name.contains( query )) {
                filteredArray.add(invoice)
            }
        }
        return filteredArray
    }

    fun uploadUserFiles(uri: Uri, invoiceUpload: InvoiceUpload) {
        firebaseRepository.uploadUserFile(uri, buildPostInvoice(invoiceUpload))
        AfinesApplication.profileUseCase.increasePendingCount()
    }

    private fun buildPostInvoice(invoiceUpload: InvoiceUpload): FirebaseInvoice {
        return FirebaseInvoice(
            id = Date().time,
            name = invoiceUpload.name,
            trimester = invoiceUpload.trimester.toString(),
            fileReference = invoiceUpload.uriMetadata.displayName,
            state = InvoiceState.SENT.toString(),
            deliveredDate = Date().time
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
}