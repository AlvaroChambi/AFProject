package es.developers.achambi.afines

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.developers.achambi.afines.invoices.ui.InvoicesFragment
import es.developers.achambi.afines.invoices.ui.UploadInvoiceActivity
import es.developers.achambi.afines.profile.ui.ProfileFragment
import kotlinx.android.synthetic.main.overview_activity_layout.*

class OverviewActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener{
    companion object {
        const val INVOICE_UPLOAD_DIALOG_CODE = 102
        fun getStartIntent(context: Context): Intent {
            return Intent(context, OverviewActivity::class.java)
        }
    }

    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.overview_activity_layout)
        if (savedInstanceState == null) {
            replaceFragment(provideEntryFragment())
        }
        bottom_navigation.inflateMenu(provideMenuResource())
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent?.action == Navigation.PROFILE_DEEP_LINK.toString()) {
                    bottom_navigation.selectedItemId = R.id.navigation_menu_profile
                } else if(intent?.action == Navigation.INVOICES_DEEP_LINK.toString()) {
                    bottom_navigation.selectedItemId = R.id.navigation_menu_invoice
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        findViewById<View>(R.id.floatingActionButton).setOnClickListener {
            startActivityForResult(
                UploadInvoiceActivity.getStartIntent( this).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                , INVOICE_UPLOAD_DIALOG_CODE )
        }
        bottom_navigation.setBackgroundColor(ContextCompat.getColor(this,R.color.primary))
        val filter = IntentFilter()
        filter.addAction(Navigation.PROFILE_DEEP_LINK.toString())
        filter.addAction(Navigation.INVOICES_DEEP_LINK.toString())
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    private fun provideMenuResource(): Int {
        return R.menu.bottom_navigation_menu
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId) {
            R.id.navigation_menu_profile -> replaceFragment(ProfileFragment.newInstance())
            R.id.navigation_menu_invoice -> replaceFragment(InvoicesFragment.newInstance())
            R.id.navigation_menu_home -> replaceFragment(OverviewFragment.newInstance())
        }
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        val manager = supportFragmentManager
        manager.beginTransaction()
            .replace(
                R.id.navigation_fragment_frame,
                fragment, null)
            .commit()
    }

    private fun provideEntryFragment(): Fragment {
        return OverviewFragment.newInstance()
    }
}

enum class Navigation {
    PROFILE_DEEP_LINK,
    INVOICES_DEEP_LINK
}
