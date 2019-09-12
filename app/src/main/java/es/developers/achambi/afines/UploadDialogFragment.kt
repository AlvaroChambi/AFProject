package es.developers.achambi.afines

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developer.achambi.coreframework.utils.URIMetadata
import es.developer.achambi.coreframework.utils.URIUtils
import kotlinx.android.synthetic.main.upload_invoice_dialog_layout.*

class UploadDialogFragment: BaseFragment() {
    companion object {
        const val ANY_FILE = "*/*"
        const val MEDIA_SEARCH_RESULT_CODE = 101

        fun newInstance(): UploadDialogFragment {
            return UploadDialogFragment()
        }
    }

    private var metadata: URIMetadata? = null

    override fun onViewSetup(view: View) {
        pick_file_chip.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = ANY_FILE
            startActivityForResult( intent,
                MEDIA_SEARCH_RESULT_CODE )
        }
    }

    override val layoutResource: Int
        get() = R.layout.upload_invoice_dialog_layout

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.full_screen_dialog_menu, menu)
        val compatActivity = activity as AppCompatActivity
        compatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        compatActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.outline_close_24)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.action_save) {
            val intent = activity?.intent
            intent?.putExtra(InvoiceFragment.FILE_EXTRA_CODE, metadata)
            activity?.setResult(Activity.RESULT_OK, intent)
            activity?.finish()
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if( requestCode == MEDIA_SEARCH_RESULT_CODE && resultCode == Activity.RESULT_OK ) {
            val resultData: Uri? = data?.data
            metadata = resultData?.let { activity?.let { it1 -> URIUtils.retrieveFileMetadata(it1, uri = it) } }
            pick_file_chip.text = metadata?.displayName
        }
    }
}