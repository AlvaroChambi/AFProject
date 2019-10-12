package es.developers.achambi.afines.invoices.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import es.developer.achambi.coreframework.threading.Error
import es.developer.achambi.coreframework.ui.BaseSearchListFragment
import es.developer.achambi.coreframework.ui.SearchAdapterDecorator
import es.developers.achambi.afines.*
import es.developers.achambi.afines.databinding.InvoiceItemLayoutBinding
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.presenter.InvoicePresenter

class InvoiceFragment: BaseSearchListFragment(), InvoicesScreenInterface {
    private lateinit var progressBar : ProgressBar
    private lateinit var adapter: Adapter
    private lateinit var presenter: InvoicePresenter

    companion object {
        const val INVOICE_UPLOAD_DIALOG_CODE = 102
        const val FILE_EXTRA_CODE = "FILE_EXTRA_CODE"
        const val URI_EXTRA_CODE = "URI_EXTRA_CODE"
        const val INVOICES_SAVED_STATE = "INVOICES_SAVED_STATE"
        const val INVOICE_DETAILS_REQUEST_CODE = 103
        const val DELETED_INVOICE_ID_KEY = "deleted_invoice_id_key"

        const val INVOICE_OPERATION_KEY = "INVOICE_OPERATION_KEY"
        const val INVOICE_DELETED_CODE = 104
        fun newInstance(): InvoiceFragment {
            return InvoiceFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AfinesApplication.invoicePresenterFactory.build(this, lifecycle)
    }

    override fun onViewSetup(view: View) {
        super.onViewSetup(view)
        activity?.setTitle(R.string.invoices_screen_title)

        progressBar = view.findViewById(R.id.horizontal_progress_bar)
        view.findViewById<View>(R.id.base_search_floating_button).visibility = View.VISIBLE
        view.findViewById<View>(R.id.base_search_floating_button).setOnClickListener {
            startActivityForResult(activity?.let {
                UploadInvoiceActivity.newInstance( it )
            }, INVOICE_UPLOAD_DIALOG_CODE )
        }

        adapter.setListener { item ->
            val dialog = InvoiceBottomSheetFragment.newInstance(item.id)
            dialog.setTargetFragment(this, INVOICE_DETAILS_REQUEST_CODE)
            dialog.show(activity?.supportFragmentManager, null)
        }
    }



    override fun onInvoicesLoadingError() {
        showError(Error(resources.getString(R.string.invoices_overview_error_message)))
    }

    override fun onUploadError() {
        showSnackBackError(Error(resources.getString(R.string.invoices_upload_error_message)))
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
            } else {
                val message = data?.getStringExtra(InvoiceBottomSheetFragment.INVOICE_EXTRA_KEY)
                view?.let {
                    message?.let { it1 -> Snackbar.make(it, it1, Snackbar.LENGTH_SHORT).show() }
                }
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

class Holder(val binding: InvoiceItemLayoutBinding): RecyclerView.ViewHolder(binding.root)

class Adapter: SearchAdapterDecorator<InvoicePresentation, Holder>() {
    override fun getLayoutResource(): Int {
        return R.layout.invoice_item_layout
    }

    override fun createViewHolder(rootView: View): Holder? {
        val binding = DataBindingUtil.bind<InvoiceItemLayoutBinding>(rootView)
        return binding?.let { Holder(it) }
    }

    override fun bindViewHolder(holder: Holder, item: InvoicePresentation) {
        holder.binding.invoice = item
    }

}