package es.developers.achambi.afines.invoices.ui

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import es.developer.achambi.coreframework.threading.CoreError
import es.developer.achambi.coreframework.ui.BaseSearchListFragment
import es.developer.achambi.coreframework.ui.SearchAdapterDecorator
import es.developers.achambi.afines.*
import es.developers.achambi.afines.databinding.InvoiceItemLayoutBinding
import es.developers.achambi.afines.databinding.InvoiceMinimalItemLayoutBinding
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.presenter.InvoicePresenter

class InvoiceFragment: BaseSearchListFragment(), InvoicesScreenInterface {
    private lateinit var progressBar : ProgressBar
    private lateinit var adapter: Adapter
    private lateinit var presenter: InvoicePresenter
    private lateinit var broadcastReceiver: BroadcastReceiver

    companion object {
        const val INVOICE_UPLOAD_DIALOG_CODE = 102
        const val FILE_EXTRA_CODE = "FILE_EXTRA_CODE"
        const val URI_EXTRA_CODE = "URI_EXTRA_CODE"
        const val INVOICE_ID_EXTRA_KEY = "INVOICE_ID_EXTRA_KEY"
        const val INVOICES_SAVED_STATE = "INVOICES_SAVED_STATE"
        const val INVOICE_DETAILS_REQUEST_CODE = 103
        const val INVOICE_EDIT_REQUEST_CODE = 104
        const val DELETED_INVOICE_ID_KEY = "deleted_invoice_id_key"

        const val INVOICE_OPERATION_KEY = "INVOICE_OPERATION_KEY"
        const val INVOICE_DELETED_CODE = 104
        const val INVOICE_EDITED_CODE = 105
        fun newInstance(): InvoiceFragment {
            return InvoiceFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AfinesApplication.invoicePresenterFactory.build(this, lifecycle)
    }

    override fun onStart() {
        super.onStart()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                presenter.refreshInvoices()
            }
        }
        presenter.registerBroadcast(broadcastReceiver)
    }

    override fun onStop() {
        super.onStop()
        presenter.unregisterBroadcast(broadcastReceiver)
    }

    override fun getHeaderLayoutResource(): Int {
        return R.layout.invoices_header_layout
    }

    override fun onViewSetup(view: View) {
        super.onViewSetup(view)
        activity?.setTitle(R.string.invoices_screen_title)
        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh_layout)
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            presenter.refreshInvoices()
        }

        progressBar = view.findViewById(R.id.horizontal_progress_bar)
        view.findViewById<View>(R.id.base_search_floating_button).visibility = View.VISIBLE
        view.findViewById<View>(R.id.base_search_floating_button).setOnClickListener {
            startActivityForResult(activity?.let {
                UploadInvoiceActivity.getStartIntent( it ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }, INVOICE_UPLOAD_DIALOG_CODE )
        }

        adapter.setListener { item ->
            startActivityForResult(activity?.let {
                InvoiceFullScreenActivity.getStartIntent(it, item.id, item.name) },
                INVOICE_DETAILS_REQUEST_CODE)
        }
    }

    override fun showInvoicesLoadingError() {
        showError(CoreError(resources.getString(R.string.invoices_overview_error_message)))
    }

    override fun showUploadError() {
        showSnackBackError(CoreError(resources.getString(R.string.invoices_upload_error_message)))
    }

    override fun showFullScreenProgress() {
        startLoading()
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun showProgressFinished() {
        progressBar.visibility = View.GONE
    }

    override fun showFullScreenProgressFinished() {
        hideLoading()
    }

    override fun showInvoices(invoices: ArrayList<InvoicePresentation>) {
        adapter.data = invoices
        presentAdapterData()
    }

    override fun showInvoiceDeleted() {
        view?.let {
            Snackbar.make(it, R.string.invoice_delete_success_message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun showInvoiceDeleteError() {
        view?.let {
            Snackbar.make(it, R.string.invoice_delete_error_message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun showEditInvoiceError() {
        view?.let {
            Snackbar.make(it, R.string.invoice_edit_error_message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun showEditInvoiceSuccess() {
        view?.let {
            Snackbar.make(it, R.string.invoice_edit_success_message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onQueryTextSubmitted(query: String) {
        presenter.queryInvoices(query)
    }

    override fun onSearchFinished() {
        presenter.showInvoices()
    }

    override fun onDataSetup() {
        super.onDataSetup()
        presenter.showInvoices()
    }

    override fun onRetry() {
        super.onRetry()
        presenter.showInvoices()
    }

    override fun provideAdapter(): SearchAdapterDecorator<InvoicePresentation, Holder> {
        adapter = Adapter()
        return adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if( requestCode == INVOICE_UPLOAD_DIALOG_CODE && resultCode == Activity.RESULT_OK ) {
            val invoice: InvoiceUpload? = data?.getParcelableExtra(FILE_EXTRA_CODE)
            val uri: Uri? = data?.getParcelableExtra(URI_EXTRA_CODE)
            uri?.let { invoice?.let { it1 -> presenter.uploadFile(it, it1) } }
        } else if(requestCode == INVOICE_DETAILS_REQUEST_CODE) {
            val code = data?.getIntExtra(INVOICE_OPERATION_KEY, 0)
            if(code == INVOICE_DELETED_CODE) {
                val invoiceId = data.getLongExtra(DELETED_INVOICE_ID_KEY, 0)
                presenter.deleteRequested(invoiceId)
            } else if(code == INVOICE_EDITED_CODE) {
                val invoiceId: Long? = data.getLongExtra(INVOICE_ID_EXTRA_KEY, 0)
                if (invoiceId != null) {
                    activity?.let { startActivityForResult(EditInvoiceActivity.getStartIntent(it, invoiceId),
                        INVOICE_EDIT_REQUEST_CODE) }
                }
            }else {
                val message = data?.getStringExtra(InvoiceBottomSheetFragment.INVOICE_EXTRA_KEY)
                view?.let {
                    message?.let { it1 -> Snackbar.make(it, it1, Snackbar.LENGTH_SHORT).show() }
                }
            }
        } else if(requestCode == INVOICE_EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val invoice: InvoiceUpload? = data?.getParcelableExtra(FILE_EXTRA_CODE)
            val uri: Uri? = data?.getParcelableExtra(URI_EXTRA_CODE)
            val invoiceId: Long? = data?.getLongExtra(INVOICE_ID_EXTRA_KEY, 0)
            if (invoiceId != null && invoice != null) {
                presenter.updateInvoice(uri, invoice, invoiceId)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(INVOICES_SAVED_STATE, adapter.data)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        adapter.data = savedInstanceState.getParcelableArrayList(INVOICES_SAVED_STATE)
        presentAdapterData()
    }
}

class Holder(val binding: InvoiceMinimalItemLayoutBinding): RecyclerView.ViewHolder(binding.root)

class Adapter: SearchAdapterDecorator<InvoicePresentation, Holder>() {
    override fun getLayoutResource(): Int {
        return R.layout.invoice_minimal_item_layout
    }

    override fun createViewHolder(rootView: View): Holder? {
        val binding = DataBindingUtil.bind<InvoiceMinimalItemLayoutBinding>(rootView)
        return binding?.let { Holder(it) }
    }

    override fun bindViewHolder(holder: Holder, item: InvoicePresentation) {
        holder.binding.invoice = item
    }

}