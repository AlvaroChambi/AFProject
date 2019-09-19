package es.developers.achambi.afines.invoices.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import es.developer.achambi.coreframework.threading.Error
import es.developer.achambi.coreframework.threading.MainExecutor
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
            InvoiceBottomSheetFragment().show(activity?.supportFragmentManager, null)
        }
    }

    override fun onInvoicesLoadingError() {
        showError(Error("Failed to load invoices. Please try again later."))
    }

    override fun onUploadError() {
        showSnackBackError(Error("Failed to upload the invoice. Please try again later."))
    }

    override fun startLoadingInvoices() {
        startLoading()
    }

    override fun startUploadingInvoice() {
        progressBar.visibility = View.VISIBLE
    }

    override fun finishedLoadingInvoices() {
        hideLoading()
    }

    override fun finishedUploadingInvoice() {
        progressBar.visibility = View.GONE
    }

    override fun onInvoiceUploaded() {
        presenter.invoiceAdded()
    }

    override fun onInvoicesRetrieved(invoices: ArrayList<InvoicePresentation>) {
        adapter.data = invoices
        presentAdapterData()
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