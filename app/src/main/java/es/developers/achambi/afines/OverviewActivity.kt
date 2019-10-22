package es.developers.achambi.afines

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.fragment.app.Fragment
import es.developer.achambi.coreframework.ui.navigation.BaseNavigationActivity
import es.developers.achambi.afines.invoices.ui.InvoiceFragment
import es.developers.achambi.afines.invoices.ui.profile.ProfileFragment

class OverviewActivity : BaseNavigationActivity(){
    override fun provideMenuResource(): Int {
        return R.menu.bottom_navigation_menu
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId) {
            R.id.navigation_menu_profile -> replaceFragment(ProfileFragment.newInstance(), null)
            R.id.navigation_menu_overview -> replaceFragment(OverviewFragment.newInstance(), null)
            R.id.navigation_menu_invoice -> replaceFragment(InvoiceFragment.newInstance(), null)
        }
        return true
    }

    override fun provideEntryFragment(): Fragment {
        return OverviewFragment.newInstance()
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, OverviewActivity::class.java)
        }
    }

}
