package es.developers.achambi.afines.invoices.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import es.developer.achambi.coreframework.ui.BaseActivity
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developers.achambi.afines.R

class UploadInvoiceActivity: BaseActivity() {
    companion object {
        fun getStartIntent(context: Context, uploadOption: String): Intent {
            val args = UploadInvoiceFragment.getArguments(uploadOption)
            val intent = Intent( context, UploadInvoiceActivity::class.java )
            intent.putExtra(BASE_ARGUMENTS, args)
            return intent
        }
    }
    override fun getScreenTitle(): Int {
        return R.string.upload_invoice_activity_title
    }

    override fun getFragment(args: Bundle?): BaseFragment {
        return UploadInvoiceFragment.newInstance(args)
    }
}