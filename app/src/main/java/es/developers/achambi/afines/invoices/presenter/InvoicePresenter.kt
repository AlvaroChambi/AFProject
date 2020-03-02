package es.developers.achambi.afines.invoices.presenter

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import es.developer.achambi.coreframework.threading.CoreError
import es.developer.achambi.coreframework.threading.ExecutorInterface
import es.developer.achambi.coreframework.threading.Request
import es.developer.achambi.coreframework.threading.ResponseHandler
import es.developer.achambi.coreframework.ui.Presenter
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.ui.InvoicePresentationBuilder
import es.developers.achambi.afines.invoices.ui.InvoicesScreenInterface
import es.developers.achambi.afines.invoices.ui.Trimester
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase
import es.developers.achambi.afines.services.Notifications
import es.developers.achambi.afines.utils.EventLogger
import java.util.*
import kotlin.collections.ArrayList

class InvoicePresenter(screenInterface: InvoicesScreenInterface,
                       lifecycle : Lifecycle,
                       executor: ExecutorInterface,
                       private val invoiceUseCase: InvoiceUseCase,
                       private val presentationBuilder: InvoicePresentationBuilder,
                       private val broadcastManager: LocalBroadcastManager,
                       private val analytics: EventLogger)
    : Presenter<InvoicesScreenInterface>(screenInterface,lifecycle,executor){

    fun uploadFile(uri: Uri, invoiceUpload: InvoiceUpload) {
        screen.showProgress()
        analytics.publishInvoiceCreated()
        val responseHandler = object: ResponseHandler<ArrayList<Invoice>> {
            override fun onSuccess(response: ArrayList<Invoice>) {
                screen.showProgressFinished()
                screen.showInvoices(presentationBuilder.build(response))
            }

            override fun onError(error: CoreError) {
                super.onError(error)
                screen.showProgressFinished()
                screen.showUploadError()
            }
        }
        val request = object : Request<ArrayList<Invoice>> {
            override fun perform(): ArrayList<Invoice> {
                return invoiceUseCase.uploadUserFiles(uri, invoiceUpload)
            }
        }
        request(request, responseHandler)
    }

    fun showInvoices(trimester: Trimester) {
        screen.showFullScreenProgress()
        val responseHandler = object: ResponseHandler<ArrayList<Invoice>> {
            override fun onSuccess(response: ArrayList<Invoice>) {
                screen.showFullScreenProgressFinished()
                screen.showInvoices( presentationBuilder.build(response) )
            }

            override fun onError(error: CoreError) {
                super.onError(error)
                screen.showFullScreenProgressFinished()
                screen.showInvoicesLoadingError()
            }
        }
        val request = object : Request<ArrayList<Invoice>>{
            override fun perform(): ArrayList<Invoice> {
                return invoiceUseCase.queryUserInvoices(Calendar.getInstance().get(Calendar.YEAR),
                    trimester, false )
            }

        }
        request(request , responseHandler)
    }

    fun queryInvoices(query: String) {
        screen.showProgress()
        analytics.publishInvoiceQuery()
        val responseHandler = object: ResponseHandler<ArrayList<Invoice>> {
            override fun onSuccess(response: ArrayList<Invoice>) {
                screen.showProgressFinished()
                screen.showInvoices( presentationBuilder.build(response) )
            }

            override fun onError(error: CoreError) {
                super.onError(error)
                screen.showProgressFinished()
                screen.showInvoicesLoadingError()
            }
        }
        val request = object : Request<ArrayList<Invoice>>{
            override fun perform(): ArrayList<Invoice> {
                return invoiceUseCase.queryUserInvoices(query)
            }

        }
        request(request , responseHandler)
    }

    fun deleteRequested(invoiceId: Long) {
        screen.showProgress()
        analytics.publishInvoiceDeleted()
        val responseHandler = object : ResponseHandler<ArrayList<Invoice>> {
            override fun onSuccess(response: ArrayList<Invoice>) {
                screen.showInvoiceDeleted()
                screen.showProgressFinished()
                screen.showInvoices(presentationBuilder.build(response))
            }

            override fun onError(error: CoreError) {
                screen.showProgressFinished()
                screen.showInvoiceDeleteError()
            }
        }
        val request = object : Request<ArrayList<Invoice>> {
            override fun perform(): ArrayList<Invoice> {
                return invoiceUseCase.deleteInvoice(invoiceId)
            }
        }
        request(request, responseHandler)
    }

    fun updateInvoice(uri: Uri?, invoiceUpload: InvoiceUpload, invoiceId: Long) {
        screen.showProgress()
        analytics.publishInvoiceUpdated(invoiceUpload.uriMetadata.displayName)
        val responseHandler = object : ResponseHandler<ArrayList<Invoice>> {
            override fun onSuccess(response: ArrayList<Invoice>) {
                screen.showProgressFinished()
                screen.showEditInvoiceSuccess()
                screen.showInvoices(presentationBuilder.build(response))
            }

            override fun onError(error: CoreError) {
                screen.showProgressFinished()
                screen.showEditInvoiceError()
            }
        }

        val request = object : Request<ArrayList<Invoice>> {
            override fun perform(): ArrayList<Invoice> {
                return invoiceUseCase.updateInvoice(uri, invoiceUpload, invoiceId)
            }
        }
        request(request, responseHandler)
    }

    fun refreshInvoices(trimester: Trimester) {
        screen.showProgress()
        val responseHandler = object: ResponseHandler<ArrayList<Invoice>> {
            override fun onSuccess(response: ArrayList<Invoice>) {
                screen.showProgressFinished()
                screen.showInvoices( presentationBuilder.build(response) )
            }

            override fun onError(error: CoreError) {
                super.onError(error)
                screen.showProgressFinished()
            }
        }
        val request = object : Request<ArrayList<Invoice>>{
            override fun perform(): ArrayList<Invoice> {
                return invoiceUseCase.queryUserInvoices(Calendar.getInstance().get(Calendar.YEAR),
                    trimester, false )
            }

        }
        request(request , responseHandler)
    }

    fun registerBroadcast(broadcastReceiver: BroadcastReceiver) {
        broadcastManager.registerReceiver(broadcastReceiver,
            IntentFilter(Notifications.INVOICE_REJECTED.toString()))
    }

    fun unregisterBroadcast(broadcastReceiver: BroadcastReceiver) {
        broadcastManager.unregisterReceiver(broadcastReceiver)
    }
}