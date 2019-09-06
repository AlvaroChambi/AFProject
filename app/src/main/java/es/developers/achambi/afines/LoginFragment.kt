package es.developers.achambi.afines

import android.content.Intent
import es.developer.achambi.coreframework.ui.login.BaseLoginFragment

class LoginFragment: BaseLoginFragment() {
    companion object {
        fun newInstance(): LoginFragment{
            return LoginFragment()
        }
    }

    override fun getNextScreenIntent(): Intent? {
        return this.activity?.let { OverviewActivity.getStartIntent(it) }
    }
}