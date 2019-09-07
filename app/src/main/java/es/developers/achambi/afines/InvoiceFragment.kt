package es.developers.achambi.afines

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.ui.BaseSearchListFragment
import es.developer.achambi.coreframework.ui.SearchAdapterDecorator
import es.developers.achambi.afines.databinding.InvoiceItemLayoutBinding
import java.io.File

class InvoiceFragment: BaseSearchListFragment(), InvoicesScreenInterface {
    private lateinit var adapter: Adapter
    private lateinit var presenter: InvoicePresenter

    companion object {
        const val MEDIA_SEARCH_RESULT_CODE = 101
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
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("application/pdf")
            startActivityForResult( intent,
                MEDIA_SEARCH_RESULT_CODE )
        }
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
        if( requestCode == MEDIA_SEARCH_RESULT_CODE && resultCode == Activity.RESULT_OK ) {
            val storage = FirebaseStorage.getInstance()
            val resultData: Uri? = data?.data

            val storageReference = storage.reference

            val file = Uri.fromFile(File(resultData?.path))
            val user = FirebaseAuth.getInstance().currentUser
            val riversRef = storageReference.child("invoices/${user?.uid}/${file.lastPathSegment}")

            val uploadTask = resultData?.let { riversRef.putFile(it) }

// Register observers to listen for when the download is done or if it fails
            uploadTask?.addOnFailureListener {
                Log.i("UPLOAD", it.message)
            }?.addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...
                Log.i("UPLOAD", "success")
            }
        }
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