package es.developers.achambi.afines.repositories.model

data class FirebaseProfile(var userId: String = "",
                           var email: String = "",
                           var userName: String = "",
                           var address: String = "",
                           var dni: String = "",
                           var naf: String = "",
                           var ccc: String = "",
                           var iban: String = "",
                           var token: String = "")