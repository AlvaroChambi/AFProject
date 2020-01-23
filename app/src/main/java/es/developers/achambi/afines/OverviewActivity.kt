package es.developers.achambi.afines

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import es.developer.achambi.coreframework.ui.navigation.BaseNavigationActivity
import es.developers.achambi.afines.invoices.ui.InvoiceFragment
import es.developers.achambi.afines.profile.ui.ProfileFragment

class OverviewActivity : BaseNavigationActivity(){
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent?.action == Navigation.PROFILE_DEEP_LINK.toString()) {
                    navigationView.selectedItemId = R.id.navigation_menu_profile
                } else if(intent?.action == Navigation.INVOICES_DEEP_LINK.toString()) {
                    navigationView.selectedItemId = R.id.navigation_menu_invoice
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter()
        filter.addAction(Navigation.PROFILE_DEEP_LINK.toString())
        filter.addAction(Navigation.INVOICES_DEEP_LINK.toString())
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    override fun provideMenuResource(): Int {
        return R.menu.bottom_navigation_menu
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId) {
            R.id.navigation_menu_profile -> replaceFragment(ProfileFragment.newInstance(), null)
            R.id.navigation_menu_invoice -> replaceFragment(InvoiceFragment.newInstance(), null)
            R.id.navigation_menu_home -> replaceFragment(OverviewFragment.newInstance(), null)
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

enum class Navigation {
    PROFILE_DEEP_LINK,
    INVOICES_DEEP_LINK
}
