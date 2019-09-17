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
import es.developers.achambi.afines.R
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.presenter.UploadPresenter
import kotlinx.android.synthetic.main.upload_invoice_dialog_layout.*

class UploadDialogFragment: BaseFragment(), UploadScreenInterface {
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
        compatActivity.supportActionBar?.elevation = 0.0f
        compatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        compatActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.outline_close_24)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.action_save) {
            activity?.let { presenter.userSaveSelected(
                    it, uri, file_name_edit_text.text.toString(),
                    chipGroup.getChecked())
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onURIUpdated(uri: Uri?, fileName: String) {
        this.uri = uri
        pick_file_chip.text = fileName
        file_name_edit_text.setText(fileName)
    }

    override fun onInvoicePreparedToSave(invoiceUpload: InvoiceUpload) {
        val intent = activity?.intent
        intent?.putExtra(InvoiceFragment.FILE_EXTRA_CODE, invoiceUpload)
        intent?.putExtra(InvoiceFragment.URI_EXTRA_CODE, uri)
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    override fun onSaveInvoiceFailed() {
        AlertDialog.Builder(activity)
            .setMessage(R.string.upload_name_error_message)
            .setTitle(R.string.upload_name_error_title)
            .create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if( requestCode == MEDIA_SEARCH_RESULT_CODE && resultCode == Activity.RESULT_OK ) {
            val resultData: Uri? = data?.data
            activity?.let { resultData?.let { it1 -> presenter.userSelectedURI(it, it1) } }
        }
    }
}