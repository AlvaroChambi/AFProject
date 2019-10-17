package es.developers.achambi.afines.invoices.ui.profile.presentations

import es.developers.achambi.afines.repositories.model.FirebaseProfile

data class ProfilePresentation(val userName: String,
                               val email: String,
                               val address: String,
                               val dni: String,
                               val naf: String,
                               val ccc: String,
                               val bankAccount: String)

class ProfilePresentationBuilder{
    fun buildPresentation(profile: FirebaseProfile): ProfilePresentation {
        return ProfilePresentation(
            userName = profile.userName,
            email = profile.email,
            address = profile.address,
            dni = profile.dni,
            naf = profile.naf,
            ccc = profile.ccc,
            bankAccount = profile.iban
        )
    }
}