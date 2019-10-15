package es.developers.achambi.afines

import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import es.developer.achambi.coreframework.ui.BaseFragment
import kotlinx.android.synthetic.main.user_profile_layout.*

class ProfileFragment: BaseFragment() {
    private var editActionButton : MenuItem? = null
    private var cancelEditActionButton: MenuItem? = null

    override fun onViewSetup(view: View) {
        activity?.setTitle(R.string.profile_activity_title)

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.profile_navigation_menu, menu)
        val compatActivity = activity as AppCompatActivity
        compatActivity.supportActionBar?.elevation = 0.0f
        editActionButton = menu?.findItem(R.id.action_edit_profile)
        cancelEditActionButton = menu?.findItem(R.id.action_cancel_edit_profile)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_edit_profile -> {
                editActionButton?.isVisible = false
                cancelEditActionButton?.isVisible = true
                profile_save_button.visibility = View.VISIBLE
                user_email_edit_text.isEnabled = true
                user_address_edit_text.isEnabled = true
                user_dni_edit_text.isEnabled = true
            }
            R.id.action_cancel_edit_profile -> {
                editActionButton?.isVisible = true
                cancelEditActionButton?.isVisible = false
                profile_save_button.visibility = View.GONE
                user_email_edit_text.isEnabled = false
                user_address_edit_text.isEnabled = false
                user_dni_edit_text.isEnabled = false
            }
            R.id.action_more_options -> {
                val dialog = activity?.let { BottomSheetDialog(it) }
                val rootView = layoutInflater.inflate(R.layout.profile_logout_layout, null)
                dialog?.setContentView(rootView)
                dialog?.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override val layoutResource: Int
        get() = R.layout.user_profile_layout

    companion object{
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

}