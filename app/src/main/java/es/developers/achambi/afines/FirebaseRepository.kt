package es.developers.achambi.afines

import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import java.lang.Exception

class FirebaseRepository {
    @Throws(Exception::class)
    fun uploadUserFile(uri: Uri, fileName: String) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val user = FirebaseAuth.getInstance().currentUser
        val userName = user?.displayName?.replace("\\s".toRegex(), "")
        val fileReference = storageReference.child("invoices/${user?.uid}/${fileName}")

        val uploadResult = Tasks.await(fileReference.putFile(uri))

        if(uploadResult.error == null) {
            return
        } else {
            throw uploadResult.error!!
        }
    }

    fun userInvoices(): ListResult {
        val user = FirebaseAuth.getInstance().currentUser
        val storage = FirebaseStorage.getInstance()
        val userName = user?.displayName?.replace("\\s".toRegex(), "")
        val listRef = storage.reference.child("invoices/${user?.uid}")

        return Tasks.await(listRef.listAll())
    }
}