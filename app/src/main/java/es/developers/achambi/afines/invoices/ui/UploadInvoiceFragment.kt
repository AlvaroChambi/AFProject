package es.developers.achambi.afines.invoices.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developers.achambi.afines.AfinesApplication
import es.developers.achambi.afines.R
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.presenter.UploadPresenter
import kotlinx.android.synthetic.main.upload_invoice_dialog_layout.*

class UploadInvoiceFragment: BaseFragment(), UploadScreenInterface {
    companion object {
        const val ANY_FILE = "*/*"
        const val MEDIA_SEARCH_RESULT_CODE = 101
        const val SAVED_URI_KEY = "SAVED_URI_KEY"
        private const val INVOICE_ID_KEY = "invoice_id_key"

        fun newInstance(args: Bundle?): UploadInvoiceFragment {
            val fragment = UploadInvoiceFragment()
            fragment.arguments = args
            return fragment
        }

        fun getArguments(invoiceId: Long): Bundle {
            val bundle = Bundle()
            bundle.putLong(INVOICE_ID_KEY, invoiceId)
            return bundle
        }
    }

    private var uri: Uri? = null
    private var invoiceId: Long? = null
    private lateinit var presenter: UploadPresenter

    override val layoutResource: Int
        get() = R.layout.upload_invoice_dialog_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invoiceId = arguments?.getLong(INVOICE_ID_KEY)
    }

    override fun onViewSetup(view: View) {
        presenter = AfinesApplication.invoiceUploadPresenterFactory.build(this, lifecycle)
        presenter.onViewSetup(invoiceId)
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.full_screen_dialog_menu, menu)
        if(invoiceId == null) {
            menu?.findItem(R.id.action_save)?.isVisible = true
            menu?.findItem(R.id.action_override)?.isVisible = false
        } else {
            menu?.findItem(R.id.action_override)?.isVisible = true
            menu?.findItem(R.id.action_save)?.isVisible = false
        }

        val compatActivity = activity as AppCompatActivity
        compatActivity.supportActionBar?.elevation = 0.0f
        compatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        compatActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.outline_close_24)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.action_save) {
            activity?.let { presenter.userSaveSelected(
                    it, uri, file_name_edit_text.text.toString(),
                    invoice_trimester_selector.getChecked())
            }
            return true
        } else if(item?.itemId == R.id.action_override) {
            activity?.let {
                    presenter.userOverrideSelected(it, uri,
                        file_name_edit_text.text.toString(),
                        invoice_trimester_selector.getChecked())
            }
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

    override fun onInvoicePreparedToEdit(invoiceUpload: InvoiceUpload) {
        val intent = activity?.intent
        intent?.putExtra(InvoiceFragment.INVOICE_OPERATION_KEY, InvoiceFragment.INVOICE_EDITED_CODE)
        intent?.putExtra(InvoiceFragment.FILE_EXTRA_CODE, invoiceUpload)
        intent?.putExtra(InvoiceFragment.URI_EXTRA_CODE, uri)
        intent?.putExtra(InvoiceFragment.INVOICE_ID_EXTRA_KEY, invoiceId)
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    override fun onCannotSaveInvoice() {
        AlertDialog.Builder(activity)
            .setMessage(R.string.upload_name_error_message)
            .setTitle(R.string.upload_name_error_title)
            .create().show()
    }

    override fun showEditableInvoice(invoice: InvoiceUploadPresentation) {
        pick_file_chip.text = invoice.file
        file_name_edit_text.setText(invoice.name)
        invoice_trimester_selector.setTrimester(invoice.trimester)
    }

    override fun showErrorRetrievingInvoice() {
        progress_background.visibility = View.VISIBLE
        base_request_error_message.text = "No se pudo recuperar la factura. Por favor itentelo de nuevo mas tarde."
    }

    override fun showScreenProgress() {
        progress_background.visibility = View.VISIBLE
        base_request_progress_bar.visibility = View.VISIBLE
    }

    override fun showScreenProgressFinished() {
        progress_background.visibility = View.GONE
        base_request_progress_bar.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if( requestCode == MEDIA_SEARCH_RESULT_CODE && resultCode == Activity.RESULT_OK ) {
            val resultData: Uri? = data?.data
            activity?.let { resultData?.let { it1 -> presenter.userSelectedURI(it, it1) } }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SAVED_URI_KEY, uri)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        uri = savedInstanceState.getParcelable(SAVED_URI_KEY)
        activity?.let { uri?.let { it1 -> presenter.userSelectedURI(it, it1) } }
    }
}