package es.developers.achambi.afines.invoices.usecase

import android.net.Uri
import es.developer.achambi.coreframework.threading.CoreError
import es.developers.achambi.afines.utils.DateFormatUtils
import es.developers.achambi.afines.AfinesApplication
import es.developers.achambi.afines.invoices.model.DetailedInvoice
import es.developers.achambi.afines.repositories.FirebaseRepository
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.ui.Trimester
import es.developers.achambi.afines.invoices.ui.TrimesterUtils
import es.developers.achambi.afines.profile.usecase.CountersUseCase
import es.developers.achambi.afines.repositories.model.FirebaseInvoice
import es.developers.achambi.afines.repositories.model.InvoiceState
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class InvoiceUseCase(private val firebaseRepository: FirebaseRepository,
                     private val countersUseCase: CountersUseCase) {
    private val cachedInvoices = HashMap<Trimester, ArrayList<Invoice>>()

    init {
        cachedInvoices[Trimester.FIRST_TRIMESTER] = ArrayList()
        cachedInvoices[Trimester.SECOND_TRIMESTER] = ArrayList()
        cachedInvoices[Trimester.THIRD_TRIMESTER] = ArrayList()
        cachedInvoices[Trimester.FORTH_TRIMESTER] = ArrayList()
    }

    fun clearCache() {
        cachedInvoices[Trimester.FIRST_TRIMESTER] = ArrayList()
        cachedInvoices[Trimester.SECOND_TRIMESTER] = ArrayList()
        cachedInvoices[Trimester.THIRD_TRIMESTER] = ArrayList()
        cachedInvoices[Trimester.FORTH_TRIMESTER] = ArrayList()
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

    @Throws(CoreError::class)
    fun uploadUserFiles(uri: Uri, invoiceUpload: InvoiceUpload): ArrayList<Invoice>{
        val id = firebaseRepository.uploadUserFile(uri, buildPostInvoice(invoiceUpload),
            countersUseCase.countersReference())
        fetchInvoice(id)?.let {
            cachedInvoices[it.trimester]?.add(it)
        }
        return cachedInvoices[TrimesterUtils.getTrimester(Date(id))]!!
    }

    @Throws(CoreError::class)
    fun updateInvoice(uri: Uri?, invoiceUpload: InvoiceUpload, invoiceId: Long): ArrayList<Invoice>{
        val invoice = getInvoice(invoiceId)
        val trimester = TrimesterUtils.getTrimester(Date(invoiceId))
        invoice?.let { firebaseRepository.updateInvoiceMetadata(invoice, invoiceUpload.name) }
        if(uri != null) {
            invoice?.let { firebaseRepository.updateInvoiceFile(invoice, invoiceUpload, uri) }
            invoice?.let { firebaseRepository.updateRejectedInvoiceState(invoice,
                InvoiceState.SENT.toString(), Date().time, countersUseCase.countersReference()) }
            AfinesApplication.profileUseCase.clearProfileCache()
        }
        cachedInvoices[trimester]?.remove(invoice)
        fetchInvoice(invoiceId)?.let {
            cachedInvoices[trimester]?.add(it)
        }
        return cachedInvoices[trimester]!!
    }

    @Throws(CoreError::class)
    fun deleteInvoice(invoiceId: Long): ArrayList<Invoice>{
        val invoice = getInvoice(invoiceId)
        val trimester = TrimesterUtils.getTrimester(Date(invoiceId))
        invoice?.let {
            firebaseRepository.deleteInvoice(it, countersUseCase.countersReference())
        }
        cachedInvoices[trimester]?.remove(invoice)
        return cachedInvoices[trimester]!!
    }

    fun getFileBytes(invoiceId: Long): ByteArray? {
        val invoice = getInvoice(invoiceId)
        return invoice?.fileReference?.let { firebaseRepository.getFilesBytes(it) }
    }

    fun getDownloadUrl(invoiceId: Long): Uri? {
        val invoice = getInvoice(invoiceId)
        return invoice?.fileReference?.let{ firebaseRepository.getDownloadUrl(it) }
    }

    //TODO Here i was querying the whole list when the invoice wasn't found, check if it's really needed
    @Throws(CoreError::class)
    fun getInvoice(invoiceId: Long): Invoice? {
        var result: Invoice? = null
        val trimester = TrimesterUtils.getTrimester(Date(invoiceId))
        for((_, v) in cachedInvoices) {
            result = v.find { it.id == invoiceId }
            if(  result != null ) { break }
        }
        if(result == null){
            result = fetchInvoice(invoiceId)
            result?.let { cachedInvoices[trimester]?.add(it) }
        }
        return result
    }

    @Throws(CoreError::class)
    fun fetchInvoice(invoiceId: Long): Invoice? {
        val firebaseInvoice = firebaseRepository.fetchInvoice(invoiceId)
        var invoice: Invoice? = null
        firebaseInvoice?.let {
            invoice = Invoice(it.id,
                it.name,
                it.fileReference?: "",
                TrimesterUtils.getTrimester(Date(it.id)),
                it.state?.let { InvoiceState.valueOf(it) },
                resolveDate(it.deliveredDate, it.processedDate),
                it.dbPath)
        }
        return invoice
    }

    @Throws(CoreError::class)
    fun queryUserInvoices(trimester: Trimester, refresh: Boolean): ArrayList<Invoice> {
        var trimesterInvoices = cachedInvoices[trimester]
        if(refresh) {
            trimesterInvoices?.clear()
        }
        if(trimesterInvoices!!.isNotEmpty()) {
            return trimesterInvoices
        }
        val dates = DateFormatUtils.getStartAndEndDate(trimester.start, trimester.end)
        val result = firebaseRepository.fetchInvoices(dates.first.time, dates.second.time)
        trimesterInvoices = ArrayList()
        result.forEach { firebaseInvoice ->
            trimesterInvoices.add(
                Invoice(firebaseInvoice.id,
                    firebaseInvoice.name,
                    firebaseInvoice.fileReference?: "",
                    TrimesterUtils.getTrimester(Date(firebaseInvoice.id)),
                    firebaseInvoice.state?.let { InvoiceState.valueOf(it) },
                    firebaseInvoice.id,
                    firebaseInvoice.dbPath))
        }
        cachedInvoices[trimester] = trimesterInvoices
        return trimesterInvoices
    }

    private fun buildPostInvoice(invoiceUpload: InvoiceUpload): FirebaseInvoice {
        return FirebaseInvoice(
            id = Date().time,
            name = invoiceUpload.name,
            fileReference = invoiceUpload.uriMetadata.displayName,
            state = InvoiceState.SENT.toString(),
            deliveredDate = Date().time
        )
    }

    private fun resolveDate(deliveredDate: Long, processedDate: Long?): Long {
        if(processedDate != null) return processedDate
        return deliveredDate
    }
}