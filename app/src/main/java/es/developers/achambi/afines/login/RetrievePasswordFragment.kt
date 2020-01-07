package es.developers.achambi.afines.login

import android.app.Activity
import android.os.Bundle
import android.view.View
import es.developer.achambi.coreframework.ui.BaseDialogFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.AfinesApplication
import es.developers.achambi.afines.R
import kotlinx.android.synthetic.main.retrieve_password_fragment_layout.*

class RetrievePasswordFragment: BaseDialogFragment(), RetrievePasswordScreen {
    private lateinit var presenter: RetrievePasswordPresenter
    companion object {
        fun newInstance(): RetrievePasswordFragment {
            return RetrievePasswordFragment()
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.retrieve_password_fragment_layout
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AfinesApplication.retrievePasswordPresenterFactory.build(this, lifecycle)
    }

    override fun onViewSetup(view: View?, savedInstanceState: Bundle?) {
        retrieve_pass_send_button.setOnClickListener {
            retrieve_pass_email_edit_frame.error = null
            presenter.retrievePassword( retrieve_pass_email_edit_frame.editText?.text.toString())
        }
        retrieve_pass_cancel_button.setOnClickListener { dismiss() }
    }

    override fun showEmailSentSuccess() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
        dismiss()
    }

    override fun showInvalidUser() {
        retrieve_pass_email_edit_frame.error = getString(R.string.login_user_doesnt_exist_text)
    }
}

interface RetrievePasswordScreen : Screen {
    fun showInvalidUser()
    fun showEmailSentSuccess()
}