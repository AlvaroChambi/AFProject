package es.developers.achambi.afines.repositories

import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.profile.presenter.ProfileUpload
import es.developers.achambi.afines.repositories.model.FirebaseInvoice
import es.developers.achambi.afines.repositories.model.FirebaseNotification
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import es.developer.achambi.coreframework.threading.Error
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class FirebaseRepository(private val firestore: FirebaseFirestore,
                         private val firestorage: FirebaseStorage,
                         private val firebaseAuth: FirebaseAuth) {
    companion object {
        const val INVOICES_PATH = "invoices/"
        const val PROFILES_PATH = "profiles"
        const val NOTIFICATIONS_PATH = "notifications"
        const val ADDRESS_ATTRIBUTE_KEY = "address"
        const val CCC_ATTRIBUTE_KEY = "ccc"
        const val DNI_ATTRIBUTE_KEY = "dni"
        const val EMAIL_ATTRIBUTE_KEY = "email"
        const val IBAN_ATTRIBUTE_KEY = "iban"
        const val NAF_ATTRIBUTE_KEY = "naf"
        const val NAME_ATTRIBUTE_KEY = "name"
        const val TRIMESTER_ATTRIBUTE_KEY = "trimester"
        const val FILE_ATTRIBUTE_KEY = "fileReference"
        const val PROCESSED_DATE_KEY = "processedDate"
        const val INVOICE_STATE_KEY = "state"
        private const val TIMEOUT = 3L
    }

    @Throws(Error::class)
    fun uploadUserFile(uri: Uri, firebaseInvoice: FirebaseInvoice) {
        val storageReference = firestorage.reference
        val user = firebaseAuth.currentUser
        val fileReference = storageReference.child(INVOICES_PATH + "${user?.uid + "/"}/${firebaseInvoice.name}")
        val firebaseReference = firestore.collection(user?.uid + "/")
        val invoiceReference = firebaseReference.document()
        firebaseInvoice.dbPath = invoiceReference.id

        try {
            Tasks.await(fileReference.putFile(uri),TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw Error()
        }catch (e: InterruptedException) {
            throw Error()
        }catch (e: TimeoutException) {
            /*On a timeout (no network connection for example) the operation will be performed locally and will be
            synchronized with the server when the connection is available. So we will just ignore this and treat it
            as a successful operation*/
        }

        try {
            firebaseInvoice.fileReference = fileReference.path
            Tasks.await(invoiceReference.set(firebaseInvoice), TIMEOUT, TimeUnit.SECONDS)
        }catch (e:ExecutionException) {
            throw Error()
        }catch (e:InterruptedException) {
            throw Error()
        }catch (e: TimeoutException) {}
    }

    fun userInvoices(): List<FirebaseInvoice> {
        val user = firebaseAuth.currentUser
        val listRef = firestore.collection(user?.uid + "/")
        val result = Tasks.await(listRef.get())
        if(result.isEmpty) {
            return ArrayList()
        }
        return result.toObjects(FirebaseInvoice::class.java)
    }

    @Throws(Error::class)
    fun deleteInvoice(invoice: Invoice) {
        val user = firebaseAuth.currentUser
        try {
            val databaseRef = firestore.collection(user?.uid + "/").document(invoice.dbReference)
            Tasks.await(databaseRef.delete(), TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw Error()
        }catch (e: InterruptedException) {
            throw Error()
        }catch (e: TimeoutException) {}

        try {
            val storageRef = firestorage.reference.child(invoice.fileReference)
            Tasks.await(storageRef.delete(), TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw Error()
        }catch (e: InterruptedException) {
            throw Error()
        }catch (e: TimeoutException) {}
    }

    @Throws(Error::class)
    fun updateInvoiceFile(invoice: Invoice, invoiceUpload: InvoiceUpload, uri: Uri) {
        val storageReference = firestorage.reference
        val user = firebaseAuth.currentUser
        val fileReference = storageReference.child(
            INVOICES_PATH + "${user?.uid + "/"}/${invoiceUpload.uriMetadata.displayName}")
        try {
            Tasks.await(fileReference.putFile(uri), TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw Error()
        }catch (e: InterruptedException) {
            throw Error()
        }catch (e: TimeoutException) {}

        try {
            val databaseRef = firestore.collection(user?.uid + "/").document(invoice.dbReference)
            Tasks.await(databaseRef.update(FILE_ATTRIBUTE_KEY, fileReference.path), TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw Error()
        }catch (e: InterruptedException) {
            throw Error()
        }catch (e: TimeoutException) {}
    }

    @Throws(Error::class)
    fun logout() {
        try {
            firebaseAuth.signOut()
        }catch (e: ExecutionException) {
            throw Error()
        }catch (e: InterruptedException) {
            throw Error()
        }catch (e: TimeoutException) {}
    }

    @Throws(Error::class)
    fun updateUserProfile(profileUpload: ProfileUpload) {
        val userId = firebaseAuth.currentUser?.uid
        val databaseRef = userId?.let { firestore.collection(PROFILES_PATH).document(it) }
        try {
            databaseRef?.let {
                Tasks.await( databaseRef.update(
                    ADDRESS_ATTRIBUTE_KEY, profileUpload.address,
                    CCC_ATTRIBUTE_KEY, profileUpload.ccc,
                    DNI_ATTRIBUTE_KEY, profileUpload.dni,
                    EMAIL_ATTRIBUTE_KEY, profileUpload.email,
                    IBAN_ATTRIBUTE_KEY, profileUpload.account,
                    NAF_ATTRIBUTE_KEY, profileUpload.naf
                ), TIMEOUT, TimeUnit.SECONDS )
            }
        }catch (e: ExecutionException) {
            throw Error()
        }catch (e: InterruptedException) {
            throw Error()
        }catch (e: TimeoutException) {}
    }

    @Throws(Error::class)
    fun updateInvoiceMetadata(invoice: Invoice, name: String, trimester: String ) {
        try {
            val user = firebaseAuth.currentUser
            val databaseRef = firestore.collection(user?.uid + "/").document(invoice.dbReference)
            Tasks.await(databaseRef.update(
                NAME_ATTRIBUTE_KEY, name,
                TRIMESTER_ATTRIBUTE_KEY, trimester),
                TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw Error()
        }catch (e: InterruptedException) {
            throw Error()
        }catch (e: TimeoutException) {}
    }
    @Throws(Error::class)
    fun updateInvoiceState(invoice: Invoice, state: String, timestamp: Long) {
        try {
            val user = firebaseAuth.currentUser
            val databaseRef = firestore.collection(user?.uid + "/").document(invoice.dbReference)
            Tasks.await(databaseRef.update(
                PROCESSED_DATE_KEY, timestamp,
                INVOICE_STATE_KEY, state),
                TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw Error()
        }catch (e: InterruptedException) {
            throw Error()
        }catch (e: TimeoutException) {}
    }
    @Throws(Error::class)
    fun retrieveCurrentUser(): FirebaseProfile? {
        try {
            val userId = firebaseAuth.currentUser?.uid
            val databaseRef = userId?.let { firestore.collection(PROFILES_PATH).document(it) }
            val result = databaseRef?.let {
                Tasks.await(it.get(), TIMEOUT, TimeUnit.SECONDS)
            }
            result?.let {
                return result.toObject(FirebaseProfile::class.java)
            }
        } catch (e: ExecutionException) {
            throw Error()
        }catch (e: InterruptedException) {
            throw Error()
        }catch (e: TimeoutException) {}
        throw Error()
    }

    @Throws(Error::class)
    fun retrieveNotifications(): List<FirebaseNotification> {
        try {
            val userId = firebaseAuth.currentUser?.uid
            val databaseRef = userId?.let { firestore.collection("user")
                .document(userId).collection(NOTIFICATIONS_PATH) }
            val result = databaseRef?.let {
                Tasks.await(it.get(), TIMEOUT, TimeUnit.SECONDS)
            }
            result?.let {
                return result.toObjects(FirebaseNotification::class.java)
            }
        }catch (e: ExecutionException) {
            throw Error()
        }catch (e: InterruptedException) {
            throw Error()
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

    @Throws(Error::class)
    fun updateUserPassword(currentPassword: String, newPassword: String) {
        val user = firebaseAuth.currentUser
        val email = user?.email
        if(email.isNullOrEmpty()) {
            throw Error()
        } else {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            try {
                Tasks.await(user.reauthenticate(credential), TIMEOUT, TimeUnit.SECONDS)
                Tasks.await(user.updatePassword(newPassword), TIMEOUT, TimeUnit.SECONDS)
            } catch (e: ExecutionException) {
                throw Error()
            }catch (e: InterruptedException) {
                throw Error()
            }catch (e: TimeoutException) {}
        }
    }
}