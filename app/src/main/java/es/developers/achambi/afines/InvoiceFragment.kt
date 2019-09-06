package es.developers.achambi.afines

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developers.achambi.afines.databinding.InvoiceItemLayoutBinding
import kotlinx.android.synthetic.main.invoice_fragment_layout.*
import java.io.File

class InvoiceFragment: BaseFragment() {
    companion object {
        const val MEDIA_SEARCH_RESULT_CODE = 101
        fun newInstance(): InvoiceFragment {
            return InvoiceFragment()
        }
    }
    override val layoutResource: Int get() = R.layout.invoice_fragment_layout

    override fun onViewSetup(view: View) {
        upload_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("application/pdf")
            startActivityForResult( intent,
                MEDIA_SEARCH_RESULT_CODE )
        }
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

class Holder(val binding: InvoiceItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {

}