package es.developers.achambi.afines.invoices.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developer.achambi.coreframework.utils.URIUtils
import es.developers.achambi.afines.R
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.presenter.UploadPresenter
import kotlinx.android.synthetic.main.upload_invoice_dialog_layout.*

class UploadDialogFragment: BaseFragment(), UploadScreenInterface {
    override fun onURIUpdated(uri: Uri?, fileName: String) {
        pick_file_chip.text = fileName
        this.uri = uri
    }

    override fun onInvoicePreparedToSave(invoiceUpload: InvoiceUpload) {
        val intent = activity?.intent
        intent?.putExtra(InvoiceFragment.FILE_EXTRA_CODE, invoiceUpload)
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    override fun onSaveInvoiceFailed() {
        AlertDialog.Builder(activity)
            .setMessage("Antes de guardar la factura tienes que subir un fichero")
            .setTitle("Nos faltan datos")
            .create().show()
    }

    companion object {
        const val ANY_FILE = "*/*"
        const val MEDIA_SEARCH_RESULT_CODE = 101

        fun newInstance(): UploadDialogFragment {
            return UploadDialogFragment()
        }
    }

    private var uri: Uri? = null
    private lateinit var presenter: UploadPresenter

    override fun onViewSetup(view: View) {
        presenter = UploadPresenter(this, lifecycle, MainExecutor.buildExecutor())
        pick_file_chip.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = ANY_FILE
            startActivityForResult( intent,
                MEDIA_SEARCH_RESULT_CODE
            )
        }
        pick_file_chip.setOnCloseIconClickListener {
            presenter.userClearedURI()
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
            activity?.let { presenter.userSaveSelected(
                    it, uri, file_name_edit_text.text.toString(),
                    Trimester.FIRST_TRIMESTER)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if( requestCode == MEDIA_SEARCH_RESULT_CODE && resultCode == Activity.RESULT_OK ) {
            val resultData: Uri? = data?.data
            metadata = resultData?.let { activity?.let { it1 -> URIUtils.retrieveFileMetadata(it1, uri = it) } }
            pick_file_chip.text = metadata?.displayName
            file_name_edit_text.setText( metadata?.displayName )
        }
    }
}