package es.developers.achambi.afines.repositories

import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.repositories.model.FirebaseInvoice
import java.lang.Exception
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class FirebaseRepository(private val firestore: FirebaseFirestore,
                         private val firestorage: FirebaseStorage) {
    companion object {
        const val INVOICES_PATH = "invoices/"
        private const val TIMEOUT = 5L
    }
    @Throws(Error::class)
    fun uploadUserFile(uri: Uri, firebaseInvoice: FirebaseInvoice) {
        val storageReference = firestorage.reference
        val fileReference = storageReference.child(INVOICES_PATH + "${buildUserPath()}/${firebaseInvoice.name}")
        val firebaseReference = firestore.collection(buildUserPath())
        val invoiceReference = firebaseReference.document()
        firebaseInvoice.dbPath = invoiceReference.id
        try {
            Tasks.await(fileReference.putFile(uri),TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw Error(e.message)
        }catch (e: InterruptedException) {
            throw Error(e.message)
        }

        try {
            firebaseInvoice.fileReference = fileReference.path
            Tasks.await(invoiceReference.set(firebaseInvoice), TIMEOUT, TimeUnit.SECONDS)
        }catch (e:ExecutionException) {
            throw Error(e.message)
        }catch (e:InterruptedException) {
            throw Error(e.message)
        }
    }

    fun userInvoices(): List<FirebaseInvoice> {
        val listRef = firestore.collection( buildUserPath())
        val result = Tasks.await(listRef.get())
        if(result.isEmpty) {
            return ArrayList()
        }
        return result.toObjects(FirebaseInvoice::class.java)
    }

    fun deleteInvoice(invoice: Invoice) {
        try {

        }catch (e: Exception) {

        }
        /*val storageRef = firestorage.reference.child(invoice.fileReference)
        Tasks.await(storageRef.delete())*/
        val databaseRef = firestore.collection(buildUserPath()).document(invoice.dbReference)
        Tasks.await(databaseRef.delete())
    }

    fun getFileMetadata(referencePath: String): StorageMetadata {
        val ref = firestorage.reference.child(referencePath)
        return Tasks.await(ref.metadata)
    }

    fun getFilesBytes(referencePath: String): ByteArray {
        val ref = firestorage.reference.child(referencePath)
        return Tasks.await(ref.getBytes(20148*2048))
    }

    private fun buildUserPath(): String {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid + "/"
    }
}