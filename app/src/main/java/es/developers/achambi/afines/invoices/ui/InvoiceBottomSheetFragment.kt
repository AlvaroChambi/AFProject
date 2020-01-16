package es.developers.achambi.afines.invoices.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.AfinesApplication
import es.developers.achambi.afines.R
import es.developers.achambi.afines.databinding.InvoiceDetailsBottonSheetBinding
import es.developers.achambi.afines.invoices.presenter.InvoiceDetailsPresenter
import kotlinx.android.synthetic.main.invoice_details_botton_sheet.*

private const val WRITE_REQUEST_CODE: Int = 43

class InvoiceBottomSheetFragment : BottomSheetDialogFragment(), InvoiceDetailsScreen {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.invoice_details_botton_sheet, container, false)
        return binding?.root
    }

    private var invoiceId: Long? = null
    private lateinit var presenter: InvoiceDetailsPresenter
    private var binding: InvoiceDetailsBottonSheetBinding? = null

    companion object {
        const val INVOICE_EXTRA_KEY = "INVOICE_EXTRA_KEY"
        private const val INVOICE_ID_EXTRA = "invoice_id_extra"
        fun newInstance(invoiceId: Long): InvoiceBottomSheetFragment {
            val fragment = InvoiceBottomSheetFragment()
            val bundle = Bundle()
            bundle.putLong(INVOICE_ID_EXTRA, invoiceId)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invoiceId = arguments?.getLong(INVOICE_ID_EXTRA)
        presenter = AfinesApplication.invoiceDetailsPresenterFactory.build(this, lifecycle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        invoiceId?.let { presenter.onViewCreated(it) }
        download_invoice_button.setOnClickListener {
            invoiceId?.let { presenter.onUserDownloadClicked(it) }
        }
        delete_invoice_button.setOnClickListener { showConfirmationDialog() }
        edit_invoice_button.setOnClickListener {
            activity?.let {
                val intent = Intent()
                intent.putExtra(InvoiceFragment.INVOICE_OPERATION_KEY, InvoiceFragment.INVOICE_EDITED_CODE)
                intent.putExtra(InvoiceFragment.INVOICE_ID_EXTRA_KEY, invoiceId)
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                dismiss()
            }
        }
        binding = DataBindingUtil.findBinding(view)
    }

    private fun showConfirmationDialog() {
        val builder = context?.let { AlertDialog.Builder(it) }
        builder?.setTitle(R.string.delete_invoice_alert_dialog_title)
            ?.setMessage(R.string.delete_invoice_alert_dialog_message)
            ?.setPositiveButton(R.string.alert_dialog_continue_text){ _, _ ->
                invoiceId?.let {
                    val intent = Intent()
                    intent.putExtra(InvoiceFragment.DELETED_INVOICE_ID_KEY, it)
                    intent.putExtra(InvoiceFragment.INVOICE_OPERATION_KEY, InvoiceFragment.INVOICE_DELETED_CODE)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    dismiss()
                }
            }
            ?.setNegativeButton(R.string.alert_dialog_cancel_text, null)
        builder?.create()?.show()
    }

    override fun showInvoice(invoicePresentation: InvoiceDetailsPresentation) {
        binding?.invoice = invoicePresentation
        presenter.downloadImage(invoicePresentation.id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            invoiceId?.let { presenter.onUserFileBytesRequired(it, data?.data, activity) }
        }
    }

    override fun createFile(mimeType: String, fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            // Filter to only show results that can be "opened", such as
            // a file (as opposed to a list of contacts or timezones).
            addCategory(Intent.CATEGORY_OPENABLE)

            // Create a file with the requested MIME type.
            type = mimeType
            putExtra(Intent.EXTRA_TITLE, fileName)
        }

        startActivityForResult(intent, WRITE_REQUEST_CODE)
    }

    override fun showDetailsLoadingFinished() {
        details_progress_bar.visibility = View.GONE
    }

    override fun showDownloadSuccess() {
        val intent = Intent()
        intent.putExtra(INVOICE_EXTRA_KEY, resources.getString(R.string.invoice_download_success_message))
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        dismiss()
    }

    override fun showDownloadError() {
        val intent = Intent()
        intent.putExtra(INVOICE_EXTRA_KEY, resources.getString(R.string.invoice_download_error_message))
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        dismiss()
    }

    override fun showDetailsLoading() {
        details_progress_bar.visibility = View.VISIBLE
    }

    override fun showDetailsError(invoicePresentation: InvoiceDetailsPresentation) {
        binding?.invoice = invoicePresentation
    }

    override fun showDownloadInProgress() {
        invoice_download_details_progress_bar.visibility = View.VISIBLE
    }

    override fun showDownloadFinished() {
        invoice_download_details_progress_bar.visibility = View.GONE
    }
}

interface InvoiceDetailsScreen : Screen {
    fun showInvoice(invoicePresentation: InvoiceDetailsPresentation)
    fun createFile(mimeType: String, fileName: String)
    fun showDetailsLoading()
    fun showDetailsLoadingFinished()
    fun showDetailsError(invoicePresentation: InvoiceDetailsPresentation)
    fun showDownloadSuccess()
    fun showDownloadError()
    fun showDownloadInProgress()
    fun showDownloadFinished()
}