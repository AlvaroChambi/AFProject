package es.developers.achambi.afines.repositories

import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.UploadTask
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.repositories.model.FirebaseInvoice
import java.lang.Exception

class FirebaseRepository(private val firestore: FirebaseFirestore,
                         private val firestorage: FirebaseStorage) {
    companion object {
        const val INVOICES_PATH = "invoices/"
    }
    @Throws(Exception::class)
    fun uploadUserFile(uri: Uri, firebaseInvoice: FirebaseInvoice) {
       val uploadResult = uploadToStorage(uri, firebaseInvoice.name)
        if(uploadResult.error != null) {
            throw uploadResult.error!!
        }

        firebaseInvoice.downloadUrl = uploadResult.storage.path
        val firebaseReference = firestore.collection(buildUserPath())
        val databaseResult = Tasks.await(firebaseReference.add(firebaseInvoice))
        if(databaseResult.get().exception != null) {
            throw databaseResult.get().exception!!
        }
    }

    private fun buildUserPath(): String {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid + "/"
    }

    private fun uploadToStorage(uri: Uri, fileName: String): UploadTask.TaskSnapshot {
        val storageReference = firestorage.reference
        val fileReference = storageReference.child(INVOICES_PATH + "${buildUserPath()}/${fileName}")

        return Tasks.await(fileReference.putFile(uri))
    }

    fun userInvoices(): List<FirebaseInvoice> {
        val listRef = firestore.collection( buildUserPath())
        val result = Tasks.await(listRef.get())
        if(result.isEmpty) {
            return ArrayList()
        }
        return result.toObjects(FirebaseInvoice::class.java)
    }
}