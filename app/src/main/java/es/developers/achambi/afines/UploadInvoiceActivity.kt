package es.developers.achambi.afines

import android.content.Context
import android.content.Intent
import android.os.Bundle
import es.developer.achambi.coreframework.ui.BaseActivity
import es.developer.achambi.coreframework.ui.BaseFragment

class UploadInvoiceActivity: BaseActivity() {
    companion object {
        fun newInstance(context: Context): Intent {
            return Intent( context, UploadInvoiceActivity::class.java )
        }
    }
    override fun getScreenTitle(): Int {
        return R.string.upload_invoice_activity_title
    }

    override fun getFragment(args: Bundle?): BaseFragment {
        return UploadDialogFragment.newInstance()
    }
}