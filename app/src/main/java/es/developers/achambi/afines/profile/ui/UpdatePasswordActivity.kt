package es.developers.achambi.afines.profile.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import es.developer.achambi.coreframework.ui.BaseActivity
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developers.achambi.afines.R

class UpdatePasswordActivity: BaseActivity() {
    companion object{
        fun getStartIntent(context: Context): Intent {
            return Intent(context, UpdatePasswordActivity::class.java)
        }
    }
    override fun getScreenTitle(): Int {
        return R.string.update_password_activity_title
    }

    override fun getFragment(args: Bundle?): BaseFragment {
        return UpdatePasswordFragment.newInstance()
    }
}