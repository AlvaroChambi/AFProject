package es.developers.achambi.afines

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import es.developer.achambi.coreframework.ui.BaseActivity
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developer.achambi.coreframework.ui.navigation.BaseNavigationActivity
import es.developers.achambi.afines.invoices.ui.InvoiceFragment
import es.developers.achambi.afines.login.LoginFragment
import es.developers.achambi.afines.profile.ui.ProfileFragment

class TestOverviewActivity: BaseNavigationActivity() {
    override fun provideMenuResource(): Int {
        return R.menu.bottom_navigation_menu
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.navigation_menu_profile -> replaceFragment(ProfileFragment.newInstance(), null)
            R.id.navigation_menu_invoice -> replaceFragment(TestInvoiceFragment(), null)
            R.id.navigation_menu_home -> replaceFragment(TestOverviewFragment(), null)
        }
        return true
    }

    override fun provideEntryFragment(): Fragment {
        return TestOverviewFragment()
    }
}