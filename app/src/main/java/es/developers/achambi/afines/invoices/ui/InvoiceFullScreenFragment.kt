package es.developers.achambi.afines.invoices.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.AfinesApplication
import es.developers.achambi.afines.R
import es.developers.achambi.afines.invoices.presenter.InvoiceFullScreenPresenter
import kotlinx.android.synthetic.main.invoice_full_screen_layout.*
import kotlinx.android.synthetic.main.user_profile_layout.*

class InvoiceFullScreenFragment: BaseFragment(), InvoiceFullScreenInterface {
    private var invoiceId: Long? = 0
    private lateinit var presenter: InvoiceFullScreenPresenter
    companion object {
        private const val INVOICE_ID_EXTRA = "invoice_id_extra"
        private const val INVOICE_NAME_EXTRA = "invoice_name_extra"
        fun newInstance(bundle: Bundle?): InvoiceFullScreenFragment {
            val fragment = InvoiceFullScreenFragment()
            fragment.arguments = bundle
            return fragment
        }

        fun getArguments(invoiceId: Long, invoiceName: String): Bundle {
            val bundle = Bundle()
            bundle.putLong(INVOICE_ID_EXTRA, invoiceId)
            bundle.putString(INVOICE_NAME_EXTRA, invoiceName)
            return bundle
        }
    }

    override val layoutResource: Int
        get() = R.layout.invoice_full_screen_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.elevation = 0F
        presenter = AfinesApplication.invoiceFullScreenPresenterFactory.build(this, lifecycle)
        invoiceId = arguments?.getLong(INVOICE_ID_EXTRA)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.invoice_navigation_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_more_options -> {
                val dialog = invoiceId?.let { InvoiceBottomSheetFragment.newInstance(it) }
                dialog?.setTargetFragment(this, InvoiceFragment.INVOICE_DETAILS_REQUEST_CODE)
                activity?.supportFragmentManager?.let { dialog?.show(it, null) }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewSetup(view: View) {
        invoiceId?.let { presenter.downloadImage(it) }
        activity?.title = arguments?.getString(INVOICE_NAME_EXTRA)
    }

    override fun showImage(uri: Uri) {
        val manager = Glide.with(this)
        val circularProgressDrawable = CircularProgressDrawable(activity!!)
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 60f
        circularProgressDrawable.start()
        val requestOptions: RequestOptions by lazy {
            RequestOptions()
                .error(R.drawable.default_image_thumbnail)
                .placeholder(circularProgressDrawable)
        }
        manager.load(uri)
            .apply(requestOptions)
            .into(invoice_image_view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activity?.setResult(resultCode, data)
        activity?.finish()
    }
}

interface InvoiceFullScreenInterface : Screen {
    fun showImage(uri: Uri)
}