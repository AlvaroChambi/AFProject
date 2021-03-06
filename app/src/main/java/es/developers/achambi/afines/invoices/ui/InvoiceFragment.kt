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
import androidx.recyclerview.widget.SortedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import es.developer.achambi.coreframework.threading.CoreError
import es.developer.achambi.coreframework.ui.BaseSearchListFragment
import es.developer.achambi.coreframework.ui.SearchAdapterDecorator
import es.developers.achambi.afines.AfinesApplication
import es.developers.achambi.afines.R
import es.developers.achambi.afines.databinding.InvoiceMinimalItemLayoutBinding
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.presenter.InvoicePresenter

class InvoiceFragment: BaseSearchListFragment(), InvoicesScreenInterface, InvoicesListener {
    private lateinit var progressBar : ProgressBar
    private lateinit var adapter: Adapter
    private lateinit var presenter: InvoicePresenter
    private lateinit var broadcastReceiver: BroadcastReceiver
    private var trimester= Trimester.EMPTY

    companion object {
        private const val TRIMESTER_EXTRA_KEY = "TRIMESTER_EXTRA_KEY"
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
        fun newInstance(trimester: Int): InvoiceFragment {
            val fragment = InvoiceFragment()
            fragment.arguments = getArguments(trimester)
            return fragment
        }

        private fun getArguments(trimester: Int): Bundle {
            val bundle = Bundle()
            bundle.putInt(TRIMESTER_EXTRA_KEY, trimester)
            return bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            trimester = Trimester.values()[it.getInt(TRIMESTER_EXTRA_KEY)]
        }
        presenter = AfinesApplication.invoicePresenterFactory.build(this, lifecycle)
    }

    override fun onStart() {
        super.onStart()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                presenter.refreshInvoices(trimester)
            }
        }
        presenter.registerBroadcast(broadcastReceiver)
    }

    override fun onStop() {
        super.onStop()
        presenter.unregisterBroadcast(broadcastReceiver)
    }

    override fun onViewSetup(view: View) {
        super.onViewSetup(view)
        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh_layout)
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            presenter.refreshInvoices(trimester)
        }

        progressBar = view.findViewById(R.id.horizontal_progress_bar)

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

    override fun onDataSetup() {
        super.onDataSetup()
        presenter.showInvoices(trimester)
    }

    override fun onRetry() {
        super.onRetry()
        presenter.refreshInvoices(trimester)
    }

    override fun provideAdapter(): SearchAdapterDecorator<InvoicePresentation, Holder> {
        adapter = Adapter(this)
        return adapter
    }

    override fun onMoreOptionSelected(item: InvoicePresentation) {
        val dialog = InvoiceBottomSheetFragment.newInstance(item.id)
        dialog.setTargetFragment(this, INVOICE_DETAILS_REQUEST_CODE)
        parentFragment?.childFragmentManager.let { it?.let { it1 -> dialog.show(it1, null) } }
    }

    override fun onInvoiceSelected(item: InvoicePresentation) {
        startActivityForResult(activity?.let {
            InvoiceFullScreenActivity.getStartIntent(it, item.id, item.name) },
            INVOICE_DETAILS_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if( requestCode == INVOICE_UPLOAD_DIALOG_CODE && resultCode == Activity.RESULT_OK ) {
            val invoice: InvoiceUpload? = data?.getParcelableExtra(FILE_EXTRA_CODE)
            val uri: Uri? = data?.getParcelableExtra(URI_EXTRA_CODE)
            uri?.let { invoice?.let { it1 -> presenter.uploadFile(it, it1) } }
        }
        else if(requestCode == INVOICE_DETAILS_REQUEST_CODE) {
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
        }
        else if(requestCode == INVOICE_EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
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

interface InvoicesListener {
    fun onMoreOptionSelected(item: InvoicePresentation)
    fun onInvoiceSelected(item: InvoicePresentation)
}

class Adapter(private val invoicesListener:InvoicesListener)
    : SearchAdapterDecorator<InvoicePresentation, Holder>() {

    override fun getLayoutResource(): Int {
        return R.layout.invoice_minimal_item_layout
    }

    override fun createViewHolder(rootView: View, rootData: SortedList<*>): Holder? {
        val binding = DataBindingUtil.bind<InvoiceMinimalItemLayoutBinding>(rootView)
        val holder =  binding?.let { Holder(it) }
        binding?.minimalInvoiceMoreButton?.setOnClickListener {
            val position = holder?.adapterPosition!!
            if (position != RecyclerView.NO_POSITION) {
                invoicesListener.onMoreOptionSelected(rootData.get(position) as InvoicePresentation)
            }
        }
        binding?.minimalInvoiceContainer?.setOnClickListener {
            val position = holder?.adapterPosition!!
            if (position != RecyclerView.NO_POSITION) {
                invoicesListener.onInvoiceSelected(rootData.get(position) as InvoicePresentation)
            }
        }
        return holder
    }

    override fun bindViewHolder(holder: Holder, item: InvoicePresentation) {
        holder.binding.invoice = item
    }
}