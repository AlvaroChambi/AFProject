package es.developers.achambi.afines.repositories.model

data class FirebaseProfile(var userId: String = "",
                           var email: String = "",
                           var name: String = "",
                           var lastName1: String = "",
                           var lastName2: String = "",
                           var businessName: String = "",
                           var clientType: String = "",
                           var address: String = "",
                           var phone1: String = "",
                           var dni: String = "",
                           var naf: String = "",
                           var ccc: String = "",
                           var iban: String = "",
                           var token: String = "",
                           var passwordChanged: Boolean = false,
                           var pending: Int = 0,
                           var rejected: Int = 0,
                           var approved: Int = 0)

enum class ClientType {
    EMPRESA,
    AUTONOMO
}