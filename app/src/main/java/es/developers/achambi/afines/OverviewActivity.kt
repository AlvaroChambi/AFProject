package es.developers.achambi.afines

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.developers.achambi.afines.invoices.ui.*
import es.developers.achambi.afines.profile.ui.ProfileFragment
import kotlinx.android.synthetic.main.overview_activity_layout.*

class OverviewActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener, OptionListener{
    companion object {
        const val INVOICES_FRAGMENT_TAG = "INVOICES_FRAGMENT_TAG"
        const val DEEPLINK_OPTION_KEY = "DEEPLINK_OPTION_KEY"
        const val INVOICE_UPLOAD_DIALOG_CODE = 102
        fun getStartIntent(context: Context, action: String = ""): Intent {
            val intent = Intent(context, OverviewActivity::class.java)
            intent.action = action
            return intent
        }
    }

    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.overview_activity_layout)
        if (savedInstanceState == null) {
            replaceFragment(provideEntryFragment())
        }
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let { handleDeeplink(it) }
            }
        }
        handleDeeplink(intent)
    }

    private fun handleDeeplink(intent: Intent) {
        if(intent.action == Navigation.PROFILE_DEEP_LINK.toString()) {
            bottom_navigation.selectedItemId = R.id.navigation_menu_profile
        } else if(intent.action == Navigation.INVOICES_DEEP_LINK.toString()) {
            bottom_navigation.selectedItemId = R.id.navigation_menu_invoice
        }
    }

    override fun onScanSelected() {
        startActivityForResult(
                UploadInvoiceActivity.getStartIntent( this, UploadInvoiceFragment.SCAN_OPTION)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                , INVOICE_UPLOAD_DIALOG_CODE )
        bottom_navigation.selectedItemId = R.id.navigation_menu_invoice
    }

    override fun onGallerySelected() {
        startActivityForResult(
            UploadInvoiceActivity.getStartIntent( this, UploadInvoiceFragment.GALLERY_OPTION)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            , INVOICE_UPLOAD_DIALOG_CODE )
        bottom_navigation.selectedItemId = R.id.navigation_menu_invoice
    }

    override fun onStart() {
        super.onStart()
        findViewById<View>(R.id.main_upload_invoice_button).setOnClickListener {
            BottomSheetUploadFragment(this).show(supportFragmentManager, null)
        }
        val filter = IntentFilter()
        filter.addAction(Navigation.PROFILE_DEEP_LINK.toString())
        filter.addAction(Navigation.INVOICES_DEEP_LINK.toString())
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == INVOICE_UPLOAD_DIALOG_CODE && resultCode == Activity.RESULT_OK) {
            val fragment = supportFragmentManager.findFragmentByTag(INVOICES_FRAGMENT_TAG)
            fragment?.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId) {
            R.id.navigation_menu_profile -> replaceFragment(ProfileFragment.newInstance())
            R.id.navigation_menu_invoice -> {
                //Transition will only be performed between OverviewFragment and Invoices Fragment
                if(findViewById<View>(R.id.trimesterHeaderView) != null) {
                    val manager = supportFragmentManager
                    manager.beginTransaction()
                        .addSharedElement(findViewById(R.id.trimesterHeaderView),
                            getString(R.string.overview_to_invoices_transition_name))
                        .replace(R.id.navigation_fragment_frame,
                            InvoicesFragment.newInstance(), INVOICES_FRAGMENT_TAG).commit()
                } else {
                    replaceFragment(InvoicesFragment.newInstance(), INVOICES_FRAGMENT_TAG)
                }
            }
            R.id.navigation_menu_home -> replaceFragment(OverviewFragment.newInstance())
        }
        return true
    }

    private fun replaceFragment(fragment: Fragment, tag: String? = null) {
        val manager = supportFragmentManager
        manager.beginTransaction().replace(R.id.navigation_fragment_frame,
                fragment, tag).commit()
    }

    private fun provideEntryFragment(): Fragment {
        return OverviewFragment.newInstance()
    }
}

enum class Navigation {
    PROFILE_DEEP_LINK,
    INVOICES_DEEP_LINK
}
