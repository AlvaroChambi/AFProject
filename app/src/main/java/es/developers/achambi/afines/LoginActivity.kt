package es.developers.achambi.afines

import android.content.Context
import android.content.Intent
import android.os.Bundle
import es.developer.achambi.coreframework.ui.BaseActivity
import es.developer.achambi.coreframework.ui.BaseFragment

class LoginActivity: BaseActivity() {
    companion object{
        fun getStartIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
    override fun getScreenTitle(): Int {
        return R.string.login
    }

    override fun getFragment(args: Bundle?): BaseFragment {
        return LoginFragment.newInstance()
    }
}