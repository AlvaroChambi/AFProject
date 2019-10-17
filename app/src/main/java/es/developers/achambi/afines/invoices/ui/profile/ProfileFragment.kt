package es.developers.achambi.afines.invoices.ui.profile

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developers.achambi.afines.AfinesApplication
import es.developers.achambi.afines.R
import es.developers.achambi.afines.invoices.presenter.ProfilePresenter
import es.developers.achambi.afines.invoices.presenter.ProfileScreenInterface
import es.developers.achambi.afines.invoices.ui.profile.presentations.ProfilePresentation
import kotlinx.android.synthetic.main.user_profile_layout.*

class ProfileFragment: BaseFragment(), ProfileScreenInterface {
    private lateinit var presenter: ProfilePresenter
    private var editActionButton : MenuItem? = null
    private var cancelEditActionButton: MenuItem? = null
    private var editEnabled = false

    override val layoutResource: Int
        get() = R.layout.user_profile_layout

    companion object{
        private const val EDIT_SAVED_STATE_KEY = "edit_saved_state_key"
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AfinesApplication.profilePresenterFactory.build(this, lifecycle)
    }

    override fun onDataSetup() {
        super.onDataSetup()
        presenter.onDataSetup()
    }

    override fun onViewSetup(view: View) {
        activity?.setTitle(R.string.profile_activity_title)
    }

    override fun showProfileFields(presentation: ProfilePresentation) {
        editActionButton?.isVisible = true

        profile_user_name_text.text = presentation.userName

        profile_email_edit_frame.editText?.setText(presentation.email)
        profile_address_edit_frame.editText?.setText(presentation.address)
        profile_dni_edit_frame.editText?.setText(presentation.dni)
        profile_naf_edit_frame.editText?.setText(presentation.naf)
        profile_ccc_edit_frame.editText?.setText(presentation.ccc)
        profile_account_edit_frame.editText?.setText(presentation.bankAccount)
    }

    override fun showProfileFieldsError() {
        full_screen_progress.showError()
    }

    override fun showFullScreenProgress() {
        full_screen_progress.showProgress()
    }

    override fun showFullScreenProgressFinished() {
        full_screen_progress.showProgressSuccess()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.profile_navigation_menu, menu)
        val compatActivity = activity as AppCompatActivity
        compatActivity.supportActionBar?.elevation = 0.0f
        editActionButton = menu?.findItem(R.id.action_edit_profile)
        cancelEditActionButton = menu?.findItem(R.id.action_cancel_edit_profile)
        /*Restore pass is performed before this step, so we have to apply the restored state here*/
        showEditState()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_edit_profile -> {
                editEnabled = true
                showEditState()
            }
            R.id.action_cancel_edit_profile -> {
                editEnabled = false
                showEditState()
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

    private fun showEditState() {
        if(editEnabled) {
            editActionButton?.isVisible = false
            cancelEditActionButton?.isVisible = true
            profile_save_button.visibility = View.VISIBLE
            profile_email_edit_frame.isEnabled = true
            profile_address_edit_frame.isEnabled = true
            profile_dni_edit_frame.isEnabled = true
            profile_naf_edit_frame.isEnabled = true
            profile_ccc_edit_frame.isEnabled = true
            profile_account_edit_frame.isEnabled = true
        } else {
            editActionButton?.isVisible = true
            cancelEditActionButton?.isVisible = false
            profile_save_button.visibility = View.GONE
            profile_email_edit_frame.isEnabled = false
            profile_address_edit_frame.isEnabled = false
            profile_dni_edit_frame.isEnabled = false
            profile_naf_edit_frame.isEnabled = false
            profile_ccc_edit_frame.isEnabled = false
            profile_account_edit_frame.isEnabled = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EDIT_SAVED_STATE_KEY, editEnabled)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editEnabled = savedInstanceState.getBoolean(EDIT_SAVED_STATE_KEY)
    }
}