package es.developers.achambi.afines.invoices.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.AfinesApplication
import es.developers.achambi.afines.R
import es.developers.achambi.afines.databinding.InvoiceDetailsBottonSheetBinding
import es.developers.achambi.afines.invoices.presenter.InvoiceDetailsPresenter
import kotlinx.android.synthetic.main.invoice_details_botton_sheet.*
import java.io.IOException

private const val WRITE_REQUEST_CODE: Int = 43
class InvoiceBottomSheetFragment : BottomSheetDialogFragment(), InvoiceDetailsScreen {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?{
        binding = DataBindingUtil.inflate(inflater, R.layout.invoice_details_botton_sheet, container, false)
        return binding?.root
    }

    private var invoiceId: Int? = null
    private lateinit var presenter: InvoiceDetailsPresenter
    private var binding: InvoiceDetailsBottonSheetBinding? = null
    companion object {
        private const val INVOICE_ID_EXTRA = "invoice_id_extra"
        fun newInstance(invoiceId: Int): InvoiceBottomSheetFragment{
            val fragment = InvoiceBottomSheetFragment()
            val bundle = Bundle()
            bundle.putInt(INVOICE_ID_EXTRA, invoiceId)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invoiceId = arguments?.getInt(INVOICE_ID_EXTRA)
        presenter = AfinesApplication.invoiceDetailsPresenterFactory.build(this,lifecycle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        invoiceId?.let { presenter.onViewCreated(it) }
        download_invoice_button.setOnClickListener {
            invoiceId?.let { presenter.onUserDownloadClicked(it) }
        }
        binding = DataBindingUtil.findBinding(view)
    }

    override fun showInvoice(invoicePresentation: InvoiceDetailsPresentation) {
        binding?.invoice = invoicePresentation
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            invoiceId?.let { presenter.onUserFileBytesRequired(it, data?.data) }
        }
    }

    override fun populateFile(uri: Uri?, data: ByteArray) {
        try {
            val os = activity?.contentResolver?.openOutputStream(uri)

            if (os != null) {
                os.write(data)
                os.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            dismiss()
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
}

interface InvoiceDetailsScreen: Screen {
    fun showInvoice(invoicePresentation: InvoiceDetailsPresentation)
    fun createFile(mimeType: String, fileName: String)
    fun populateFile(uri: Uri?, data: ByteArray)
}