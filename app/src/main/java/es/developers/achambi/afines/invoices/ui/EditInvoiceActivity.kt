package es.developers.achambi.afines.invoices.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import es.developer.achambi.coreframework.ui.BaseActivity
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developers.achambi.afines.R

class EditInvoiceActivity: BaseActivity() {
    companion object {
        fun newInstance(context: Context, invoiceId: Long): Intent {
            val intent = Intent( context, EditInvoiceActivity::class.java )
            val args = UploadInvoiceFragment.getArguments(invoiceId)
            intent.putExtra(BASE_ARGUMENTS, args)
            return intent
        }
    }
    override fun getScreenTitle(): Int {
        return R.string.edit_invoice_activity_title
    }

    override fun getFragment(args: Bundle): BaseFragment {
        return UploadInvoiceFragment.newInstance(args)
    }
}