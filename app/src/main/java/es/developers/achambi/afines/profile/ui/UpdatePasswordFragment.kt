package es.developers.achambi.afines.profile.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developer.achambi.coreframework.utils.WindowUtils
import es.developers.achambi.afines.AfinesApplication
import es.developers.achambi.afines.R
import es.developers.achambi.afines.profile.presenter.UpdatePasswordPresenter

interface UpdatePasswordScreen: Screen {
    fun showHeaderProgress()
    fun showHeaderProgressFinished()
    fun hideSoftKeyboard()

    fun showPasswordUpdateSuccess()

    fun showMissingFieldsError()
    fun showInvalidPasswordError()
    fun showWeakPasswordError()
    fun showConfirmationNotMatching()
}

class UpdatePasswordFragment: BaseFragment(), UpdatePasswordScreen {
    private lateinit var currentPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmationPassword: EditText

    private lateinit var presenter: UpdatePasswordPresenter

    companion object {
        fun newInstance(): UpdatePasswordFragment {
            return UpdatePasswordFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AfinesApplication.updatePasswordPresenterFactory.build(this, lifecycle)
    }

    override fun onViewSetup(view: View) {
        currentPassword = view.findViewById(R.id.current_pass_edit_text)
        newPassword = view.findViewById(R.id.new_password_edit_text)
        confirmationPassword = view.findViewById(R.id.new_passwordconfimation_edit_text)
    }

    override val layoutResource: Int
        get() = R.layout.update_password_fragment_layout

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.update_password_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_save_password -> {
                presenter.userSaved(
                    currentPassword = currentPassword.text.toString(),
                    newPassword = newPassword.text.toString(),
                    confirmationPassword = confirmationPassword.text.toString())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showHeaderProgress() {
        view?.findViewById<View>(R.id.update_password_progress_bar)?.visibility = View.VISIBLE
    }

    override fun showHeaderProgressFinished() {
        view?.findViewById<View>(R.id.update_password_progress_bar)?.visibility = View.GONE
    }

    override fun showPasswordUpdateSuccess() {
        view?.let {
            Snackbar.make(it, R.string.password_update_success_text, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun showMissingFieldsError() {
        view?.let {
            Snackbar.make(it, R.string.password_update_missing_fields_text, Snackbar.LENGTH_INDEFINITE).show()
        }
    }

    override fun showInvalidPasswordError() {
        view?.let {
            Snackbar.make(it, R.string.password_update_invalid_password_text, Snackbar.LENGTH_INDEFINITE).show()
        }
    }

    override fun showWeakPasswordError() {
        view?.let {
            Snackbar.make(it, R.string.password_update_weak_password_text, Snackbar.LENGTH_INDEFINITE).show()
        }
    }

    override fun showConfirmationNotMatching() {
        view?.let {
            Snackbar.make(it, R.string.password_update_not_matching_text, Snackbar.LENGTH_INDEFINITE).show()
        }
    }

    override fun hideSoftKeyboard() {
        activity?.let {WindowUtils.hideSoftKeyboard(it)}
    }
}