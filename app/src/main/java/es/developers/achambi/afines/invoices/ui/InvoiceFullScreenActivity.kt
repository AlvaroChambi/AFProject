package es.developers.achambi.afines.invoices.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import es.developer.achambi.coreframework.ui.BaseActivity
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developers.achambi.afines.R

class InvoiceFullScreenActivity: BaseActivity() {
    companion object {
        fun getStartIntent(context: Context, invoiceId: Long, invoiceName: String): Intent {
            val intent = Intent( context, InvoiceFullScreenActivity::class.java )
            val args = InvoiceFullScreenFragment.getArguments(invoiceId, invoiceName)
            intent.putExtra(BASE_ARGUMENTS, args)
            return intent
        }
    }

    override fun getFragment(args: Bundle?): BaseFragment {
        return InvoiceFullScreenFragment.newInstance(args)
    }

    override fun getScreenTitle(): Int {
        return R.string.app_name
    }
}