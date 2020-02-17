package es.developers.achambi.afines.invoices.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanConstants
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.AfinesApplication
import es.developers.achambi.afines.R
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.presenter.UploadPresenter
import kotlinx.android.synthetic.main.upload_invoice_dialog_layout.*

interface UploadScreenInterface : Screen {
    fun onURIUpdated(uri: Uri?, fileName: String)
    fun onInvoicePreparedToSave(invoiceUpload: InvoiceUpload)
    fun onCannotSaveInvoice()

    fun onInvoicePreparedToEdit(invoiceUpload: InvoiceUpload)

    fun showScreenProgress()
    fun showScreenProgressFinished()

    fun showEditableInvoice(invoice: InvoiceUploadPresentation)
    fun showErrorRetrievingInvoice()

    fun showPhotoCaptureError()
    fun showCamera(uri: Uri)
    fun onPhotoUriCreated(uri: Uri)

    fun showGallery()
}

class UploadInvoiceFragment: BaseFragment(), UploadScreenInterface {
    companion object {
        const val ANY_FILE = "*/*"
        const val MEDIA_SEARCH_RESULT_CODE = 101
        const val PHOTO_CAPTURE_RESULT_CODE = 102
        const val CAMERA_PERMISSION_REQUEST_CODE = 103
        const val SCANNER_REQUEST_CODE = 104
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
        get() = R.layout.new_upload_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = AfinesApplication.invoiceUploadPresenterFactory.build(this, lifecycle)
        invoiceId = arguments?.getLong(INVOICE_ID_KEY)
    }

    override fun onDataSetup() {
        super.onDataSetup()
        presenter.onDataSetup(invoiceId)
    }

    override fun onViewSetup(view: View) {
       /* pick_file_chip.setOnClickListener{
            presenter.userSelectedFileChip()
        }
        pick_file_chip.setOnCloseIconClickListener {
            presenter.userClearedURI()
        }

        invoice_photo_button.setOnClickListener {
            activity?.let {
                if (ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        CAMERA_PERMISSION_REQUEST_CODE)
                }else {
                    presenter.userPhotoFileRequested(it)
                }
            }
        }*/
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        when(requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                grantResults.forEach {
                    if( it != PackageManager.PERMISSION_GRANTED ) {
                        return
                    }
                }
                activity?.let { presenter.userPhotoFileRequested(it) }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.full_screen_dialog_menu, menu)
        if(invoiceId == null) {
            menu.findItem(R.id.action_save)?.isVisible = true
            menu.findItem(R.id.action_override)?.isVisible = false
        } else {
            menu.findItem(R.id.action_override)?.isVisible = true
            menu.findItem(R.id.action_save)?.isVisible = false
        }

        val compatActivity = activity as AppCompatActivity
        compatActivity.supportActionBar?.elevation = 0.0f
        compatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        compatActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.outline_close_24)
    }

    override fun showGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = ANY_FILE
        startActivityForResult( intent,
            MEDIA_SEARCH_RESULT_CODE
        )
    }

    override fun showPhotoCaptureError() {
        view?.let {
            Snackbar.make(it, R.string.photo_capture_error_message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun showCamera(uri: Uri) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            takePictureIntent.resolveActivity(activity?.packageManager)?.also {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(takePictureIntent, PHOTO_CAPTURE_RESULT_CODE)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*if(item.itemId == R.id.action_save) {
            activity?.let { presenter.userSaveSelected(
                    it, uri, file_name_edit_text.text.toString(),
                    invoice_trimester_selector.getChecked())
            }
            return true
        } else if(item.itemId == R.id.action_override) {
            activity?.let {
                    presenter.userOverrideSelected(it, uri,
                        file_name_edit_text.text.toString(),
                        invoice_trimester_selector.getChecked())
            }
        }*/
        return super.onOptionsItemSelected(item)
    }

    override fun onURIUpdated(uri: Uri?, fileName: String) {
        this.uri = uri
        pick_file_chip.text = fileName
        file_name_edit_text.setText(fileName)
    }

    override fun onPhotoUriCreated(uri: Uri) {
        this.uri = uri
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
       // invoice_trimester_selector.setTrimester(invoice.trimester)
    }

    override fun showErrorRetrievingInvoice() {
        progress_background.visibility = View.VISIBLE
        base_request_error_message.text = resources.getText(R.string.invoice_upload_error_message)
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
        } else if(requestCode == PHOTO_CAPTURE_RESULT_CODE) {
            //An uri was previously created over a temp file and set before the picture was taken, if the result
            // code is not ok, we'll just clear the uri value
            if(resultCode == Activity.RESULT_OK) {
                activity?.let { uri?.let { it1 ->
                    startActivityForResult(ScanActivity.getStartIntent(it, it1.toString()),
                        SCANNER_REQUEST_CODE) } }
            } else {
                this.uri = null
            }
        } else if(requestCode == SCANNER_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                val uri: Uri? = data?.extras?.getParcelable(ScanConstants.SCANNED_RESULT)
                activity?.let { uri?.let { it1 -> presenter.userSelectedURI(it, it1) } }
            } else {
                this.uri = null
            }
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