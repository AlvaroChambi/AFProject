package es.developers.achambi.afines.login

import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.AfinesApplication
import es.developers.achambi.afines.OverviewActivity
import es.developers.achambi.afines.R
import kotlinx.android.synthetic.main.login_fragment_layout.*

class LoginFragment: BaseFragment(), LoginScreenInterface {
    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    private lateinit var presenter : LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AfinesApplication.loginPresenterFactory.build(this, lifecycle)
    }

    override fun onViewSetup(view: View) {
        login_button.setOnClickListener {
            login_email_edit_frame.error = null
            login_pass_edit_frame.error = null
            presenter.login(login_email_edit_text.text.toString(),
                login_pass_edit_text.text.toString())
        }
    }

    override val layoutResource: Int
        get() = R.layout.login_fragment_layout

    override fun showNextScreen() {
        startActivity(activity?.let { OverviewActivity.getStartIntent(it) })
    }

    override fun showInvalidEmail() {
        login_email_edit_frame.error = getString(R.string.login_invalid_email_text)
    }

    override fun showInvalidPassword() {
        login_pass_edit_frame.error = getString(R.string.login_invalid_password_text)
    }

    override fun showGenericError() {
        view?.let {
            Snackbar.make(it, R.string.generic_error_text, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun showInvalidUser() {
        view?.let {
            Snackbar.make(it, R.string.login_user_doesnt_exist_text, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun showProgress() {
        login_button.text = null
        progressBar.visibility = View.VISIBLE
    }

    override fun finishProgress() {
        login_button.text = getString(R.string.user_login_text)
        progressBar.visibility = View.INVISIBLE
    }
}

interface LoginScreenInterface : Screen {
    fun showInvalidEmail()
    fun showInvalidPassword()
    fun showGenericError()
    fun showInvalidUser()
    fun showProgress()
    fun showNextScreen()
    fun finishProgress()
}