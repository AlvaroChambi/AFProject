package es.developers.achambi.afines.profile.ui.presentations

import android.content.Context
import es.developers.achambi.afines.R
import es.developers.achambi.afines.repositories.model.ClientType
import es.developers.achambi.afines.repositories.model.FirebaseProfile

data class ProfilePresentation(val userName: String,
                               val email: String,
                               val address: String,
                               val dni: String,
                               val naf: String,
                               val ccc: String,
                               val bankAccount: String,
                               val dniLabel: String)

class ProfilePresentationBuilder(private val context: Context){
    fun buildPresentation(profile: FirebaseProfile): ProfilePresentation {
        var name = profile.name + " " + profile.lastName1 + " " + profile.lastName2
        var dniLabel = context.getString(R.string.profile_dni_hint_text)
        if(profile.clientType == ClientType.EMPRESA.toString()) {
            name = profile.businessName
            dniLabel = context.getString(R.string.profile_cif_hint_text)
        }
        return ProfilePresentation(
            userName = name,
            email = profile.email,
            address = profile.address,
            dni = profile.dni,
            naf = profile.naf,
            ccc = profile.ccc,
            bankAccount = profile.iban,
            dniLabel = dniLabel
        )
    }
}