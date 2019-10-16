package es.developers.achambi.afines.repositories

import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.ui.Trimester
import es.developers.achambi.afines.repositories.model.FirebaseInvoice
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import java.lang.Exception
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class FirebaseRepository(private val firestore: FirebaseFirestore,
                         private val firestorage: FirebaseStorage,
                         private val firebaseAuth: FirebaseAuth) {
    companion object {
        const val INVOICES_PATH = "invoices/"
        private const val TIMEOUT = 3L
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
        }catch (e: TimeoutException) {
            /*On a timeout (no network connection for example) the operation will be performed locally and will be
            synchronized with the server when the connection is available. So we will just ignore this and treat it
            as a successful operation*/
        }

        try {
            firebaseInvoice.fileReference = fileReference.path
            Tasks.await(invoiceReference.set(firebaseInvoice), TIMEOUT, TimeUnit.SECONDS)
        }catch (e:ExecutionException) {
            throw Error(e.message)
        }catch (e:InterruptedException) {
            throw Error(e.message)
        }catch (e: TimeoutException) {}
    }

    fun userInvoices(): List<FirebaseInvoice> {
        val listRef = firestore.collection( buildUserPath())
        val result = Tasks.await(listRef.get())
        if(result.isEmpty) {
            return ArrayList()
        }
        return result.toObjects(FirebaseInvoice::class.java)
    }

    @Throws(Error::class)
    fun deleteInvoice(invoice: Invoice) {
        try {
            val databaseRef = firestore.collection(buildUserPath()).document(invoice.dbReference)
            Tasks.await(databaseRef.delete(), TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw Error(e.message)
        }catch (e: InterruptedException) {
            throw Error(e.message)
        }catch (e: TimeoutException) {}

        try {
            val storageRef = firestorage.reference.child(invoice.fileReference)
            Tasks.await(storageRef.delete(), TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw Error(e.message)
        }catch (e: InterruptedException) {
            throw Error(e.message)
        }catch (e: TimeoutException) {}
    }

    @Throws(Error::class)
    fun updateInvoiceFile(invoice: Invoice, invoiceUpload: InvoiceUpload, uri: Uri) {
        val storageReference = firestorage.reference
        val fileReference = storageReference.child(
            INVOICES_PATH + "${buildUserPath()}/${invoiceUpload.uriMetadata.displayName}")
        try {
            Tasks.await(fileReference.putFile(uri), TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw Error(e.message)
        }catch (e: InterruptedException) {
            throw Error(e.message)
        }catch (e: TimeoutException) {}

        try {
            val databaseRef = firestore.collection(buildUserPath()).document(invoice.dbReference)
            Tasks.await(databaseRef.update("fileReference", fileReference.path), TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw Error(e.message)
        }catch (e: InterruptedException) {
            throw Error(e.message)
        }catch (e: TimeoutException) {}
    }

    @Throws(Error::class)
    fun updateInvoiceMetadata(invoice: Invoice, name: String, trimester: String ) {
        try {
            val databaseRef = firestore.collection(buildUserPath()).document(invoice.dbReference)
            Tasks.await(databaseRef.update("name", name), TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw Error(e.message)
        }catch (e: InterruptedException) {
            throw Error(e.message)
        }catch (e: TimeoutException) {}

        try {
            val databaseRef = firestore.collection(buildUserPath()).document(invoice.dbReference)
            Tasks.await(databaseRef.update("trimester", trimester), TIMEOUT, TimeUnit.SECONDS)
        } catch (e: ExecutionException) {
            throw Error(e.message)
        }catch (e: InterruptedException) {
            throw Error(e.message)
        }catch (e: TimeoutException) {}
    }
    @Throws(Error::class)
    fun retrieveCurrentUser(): FirebaseProfile? {
        try {
            val userId = firebaseAuth.currentUser?.uid
            val databaseRef = userId?.let { firestore.collection("profiles").document(it) }
            val result = databaseRef?.let {
                Tasks.await(it.get(), TIMEOUT, TimeUnit.SECONDS)
            }
            result?.let {
                val result = result.toObject(FirebaseProfile::class.java)
                return result
            }
        } catch (e: ExecutionException) {
            throw Error(e.message)
        }catch (e: InterruptedException) {
            throw Error(e.message)
        }catch (e: TimeoutException) {}
        throw Error()
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
        val user = firebaseAuth.currentUser
        return user?.uid + "/"
    }
}