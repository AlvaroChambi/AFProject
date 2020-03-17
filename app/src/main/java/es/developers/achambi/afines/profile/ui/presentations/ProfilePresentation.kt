package es.developers.achambi.afines.profile.ui.presentations

import es.developers.achambi.afines.repositories.model.ClientType
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
        var name = profile.name + " " + profile.lastName1 + " " + profile.lastName2
        if(profile.clientType == ClientType.EMPRESA.toString()) {
            name = profile.businessName
        }
        return ProfilePresentation(
            userName = name,
            email = profile.email,
            address = profile.address,
            dni = profile.dni,
            naf = profile.naf,
            ccc = profile.ccc,
            bankAccount = profile.iban
        )
    }
}