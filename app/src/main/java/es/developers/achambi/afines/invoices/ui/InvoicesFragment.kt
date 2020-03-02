package es.developers.achambi.afines.invoices.ui

import android.content.Intent
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeTransform
import android.transition.Fade
import android.transition.TransitionSet
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developers.achambi.afines.R
import kotlinx.android.synthetic.main.invoices_fragment_layout.*

class DetailsTransition: TransitionSet() {
    init{
        ordering = (ORDERING_TOGETHER)
        addTransition( ChangeBounds() ).addTransition(ChangeTransform())
    }
}

class InvoicesFragment: BaseFragment() {
    private lateinit var pagerAdapter: InvoicesPagerAdapter
    private var trimester: Int = 0
    override val layoutResource: Int
        get() = R.layout.invoices_fragment_layout

    companion object {
        fun newInstance(): InvoicesFragment {
            val fragment = InvoicesFragment()
            fragment.sharedElementEnterTransition = (DetailsTransition())
            val fade = Fade()
            fragment.enterTransition = fade
            fragment.sharedElementReturnTransition = DetailsTransition()
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pagerAdapter = InvoicesPagerAdapter(childFragmentManager)
        trimester = TrimesterUtils.getCurrentTrimester().ordinal
    }

    override fun onViewSetup(view: View) {
        invoices_tab_layout.selectTab(invoices_tab_layout.getTabAt(trimester))
        invoices_tab_layout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                invoices_trimester_header_view.animation =
                    AnimationUtils.loadAnimation(context,R.anim.fade_in)
                invoices_view_pager.currentItem = tab.position
                invoices_trimester_header_view.setTrimester(Trimester.values()[tab.position])
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                invoices_trimester_header_view.animation =
                    AnimationUtils.loadAnimation(context, R.anim.fade_out)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        invoices_view_pager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(invoices_tab_layout))
        invoices_view_pager.adapter = pagerAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        invoices_tab_layout.selectTab(invoices_tab_layout.getTabAt(trimester))
        val current = pagerAdapter.getItem(trimester)
        current.onActivityResult(requestCode, resultCode, data)
    }
}

class InvoicesPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
       return InvoiceFragment.newInstance(position)
    }

    override fun getCount(): Int {
        return 4
    }

}