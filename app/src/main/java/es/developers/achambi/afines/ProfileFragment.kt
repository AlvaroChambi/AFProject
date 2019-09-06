package es.developers.achambi.afines

import es.developer.achambi.coreframework.ui.profile.BaseProfileFragment

class ProfileFragment: BaseProfileFragment() {
    companion object{
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
    override fun onUserLoggedOut() {
        startActivity(activity?.let { LoginActivity.getStartIntent(it) })
        activity?.finish()
    }
}