package es.developers.achambi.afines

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.ui.BaseSearchListFragment
import es.developer.achambi.coreframework.ui.SearchAdapterDecorator
import es.developer.achambi.coreframework.utils.URIMetadata
import es.developers.achambi.afines.databinding.InvoiceItemLayoutBinding

class InvoiceFragment: BaseSearchListFragment(), InvoicesScreenInterface {
    private lateinit var adapter: Adapter
    private lateinit var presenter: InvoicePresenter

    companion object {
        const val INVOICE_UPLOAD_DIALOG_CODE = 102
        const val FILE_EXTRA_CODE = "FILE_EXTRA_CODE"
        fun newInstance(): InvoiceFragment {
            return InvoiceFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = InvoicePresenter(this,lifecycle,
            MainExecutor.buildExecutor(), InvoicePresentationBuilder(activity!!))
    }


    override fun onViewSetup(view: View) {
        super.onViewSetup(view)
        view.findViewById<View>(R.id.base_search_floating_button).visibility = View.VISIBLE
        view.findViewById<View>(R.id.base_search_floating_button).setOnClickListener {
            startActivityForResult(activity?.let { UploadInvoiceActivity.newInstance(it) },
                INVOICE_UPLOAD_DIALOG_CODE)
        }
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

    override fun provideAdapter(): SearchAdapterDecorator<InvoicePresentation, Holder> {
        adapter = Adapter()
        return adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if( requestCode == INVOICE_UPLOAD_DIALOG_CODE && resultCode == Activity.RESULT_OK ) {
            val resultData: URIMetadata? = data?.getParcelableExtra(FILE_EXTRA_CODE)
            activity?.let { resultData?.let { it1 -> presenter.uploadFile(it, it1) } }
        }
    } }

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