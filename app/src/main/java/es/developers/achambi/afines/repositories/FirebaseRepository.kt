package es.developers.achambi.afines.repositories

import android.net.Uri
import com.crashlytics.android.Crashlytics
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.profile.presenter.ProfileUpload
import es.developers.achambi.afines.repositories.model.FirebaseInvoice
import es.developers.achambi.afines.repositories.model.FirebaseNotification
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import es.developer.achambi.coreframework.threading.CoreError
import es.developers.achambi.afines.home.model.TaxDate
import es.developers.achambi.afines.repositories.model.InvoiceCounters
import es.developers.achambi.afines.utils.EventLogger
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class FirebaseRepository(private val firestore: FirebaseFirestore,
                         private val firestorage: FirebaseStorage,
                         private val firebaseAuth: FirebaseAuth,
                         private val analytics: EventLogger) {
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

        const val DEVICE_TOKEN_KEY = "token"
        const val PENDING_INVOICES_KEY = "pending"
        const val REJECTED_INVOICES_KEY = "rejected"
        const val PASSWORD_CHANGED_FLAG = "passwordChanged"
        private const val TIMEOUT = 3L
    }

    @Throws(CoreError::class)
    fun uploadUserFile(uri: Uri, firebaseInvoice: FirebaseInvoice) {
        val storageReference = firestorage.reference
        val user = firebaseAuth.currentUser
        val fileReference = storageReference.child(INVOICES_PATH + "${user?.uid + "/"}/${firebaseInvoice.name}")
        val firebaseReference = firestore.collection("user/"+user?.uid + "/invoices/")
        val invoiceReference = firebaseReference.document()
        firebaseInvoice.dbPath = invoiceReference.id

        try {
            Tasks.await(fileReference.putFile(uri),TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {
            /*On a timeout (no network connection for example) the operation will be performed locally and will be
            synchronized with the server when the connection is available. So we will just ignore this and treat it
            as a successful operation*/
            Crashlytics.logException(e)
        }

        try {
            val profileRef = firestore.collection(PROFILES_PATH).document(user!!.uid)
            firebaseInvoice.fileReference = fileReference.path
            Tasks.await(firestore.runTransaction { transaction ->
                val profileSnapshot = transaction.get(profileRef)
                var pending = profileSnapshot.getLong(PENDING_INVOICES_KEY)
                if(pending == null) pending = 0
                transaction.set(invoiceReference, firebaseInvoice)
                transaction.update(profileRef, PENDING_INVOICES_KEY, ++pending)
            }, TIMEOUT, TimeUnit.SECONDS)
            analytics.publishTransaction(user.uid)
        }catch (e:ExecutionException) {
            throw CoreError(e.message)
        }catch (e:InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
    }

    fun userInvoices(): List<FirebaseInvoice> {
        val user = firebaseAuth.currentUser
        val listRef = firestore.collection("user/"+ user?.uid + "/invoices/")
        val result = Tasks.await(listRef.get())
        analytics.publishReadEvent(user?.uid)
        if(result.isEmpty) {
            return ArrayList()
        }
        return result.toObjects(FirebaseInvoice::class.java)
    }

    @Throws(CoreError::class)
    fun deleteInvoice(invoice: Invoice) {
        val user = firebaseAuth.currentUser
        try {
            val databaseRef = firestore.collection("user/"+ user?.uid + "/invoices/")
                .document(invoice.dbReference)
            val profileRef = firestore.collection(PROFILES_PATH).document(user!!.uid)
            Tasks.await(firestore.runTransaction { transaction ->
                val profileSnapshot = transaction.get(profileRef)
                var pendingCount = profileSnapshot.getLong(PENDING_INVOICES_KEY)
                if(pendingCount == null) pendingCount = 0
                transaction.delete(databaseRef)
                transaction.update(profileRef, PENDING_INVOICES_KEY, --pendingCount)
            }, TIMEOUT ,TimeUnit.SECONDS)
            analytics.publishTransaction(user.uid)
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}

        try {
            val storageRef = firestorage.reference.child(invoice.fileReference)
            Tasks.await(storageRef.delete(), TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
    }

    @Throws(CoreError::class)
    fun updateInvoiceFile(invoice: Invoice, invoiceUpload: InvoiceUpload, uri: Uri) {
        val storageReference = firestorage.reference
        val user = firebaseAuth.currentUser
        val fileReference = storageReference.child(
            INVOICES_PATH + "${user?.uid + "/"}/${invoiceUpload.uriMetadata.displayName}")
        try {
            Tasks.await(fileReference.putFile(uri), TIMEOUT, TimeUnit.SECONDS)
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}

        try {
            val databaseRef = firestore.collection("user/"+ user?.uid + "/invoices/").document(invoice.dbReference)
            Tasks.await(databaseRef.update(FILE_ATTRIBUTE_KEY, fileReference.path), TIMEOUT, TimeUnit.SECONDS)
            analytics.publishWriteEvent(user?.uid)
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
    }

    @Throws(CoreError::class)
    fun logout() {
        try {
            firebaseAuth.signOut()
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {}
    }

    @Throws(CoreError::class)
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
            analytics.publishWriteEvent(userId)
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
    }

    @Throws
    fun updateProfileToken(deviceToken: String) {
        updateProfileToken(deviceToken, firebaseAuth.currentUser?.uid)
    }

    @Throws
    fun updateProfileToken(deviceToken: String, uid: String?) {
        val databaseRef = uid?.let { firestore.collection(PROFILES_PATH).document(it) }
        try {
            databaseRef?.let {
                Tasks.await( databaseRef.update(
                    DEVICE_TOKEN_KEY, deviceToken
                ), TIMEOUT, TimeUnit.SECONDS )
            }
            analytics.publishWriteEvent(uid)
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
    }

    @Throws
    fun checkProfilePasswordFlag() {
        val databaseRef = firebaseAuth.currentUser?.uid?.let {
            firestore.collection(PROFILES_PATH).document(it) }
        try {
            databaseRef?.let {
                Tasks.await( databaseRef.update(
                    PASSWORD_CHANGED_FLAG, true
                ), TIMEOUT, TimeUnit.SECONDS )
            }
            analytics.publishWriteEvent(firebaseAuth.currentUser?.uid)
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
    }

    @Throws(CoreError::class)
    fun updateInvoiceMetadata(invoice: Invoice, name: String, trimester: String ) {
        try {
            val user = firebaseAuth.currentUser
            val databaseRef = firestore.collection("user/"+ user?.uid + "/invoices/").document(invoice.dbReference)
            Tasks.await(databaseRef.update(
                NAME_ATTRIBUTE_KEY, name,
                TRIMESTER_ATTRIBUTE_KEY, trimester),
                TIMEOUT, TimeUnit.SECONDS)
            analytics.publishWriteEvent(user?.uid)
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
    }

    @Throws(CoreError::class)
    fun updateRejectedInvoiceState(invoice: Invoice, state: String, timestamp: Long) {
        try {
            val user = firebaseAuth.currentUser
            val databaseRef = firestore.collection("user/"+ user?.uid + "/invoices/")
                .document(invoice.dbReference)
            val profileRef = firestore.collection(PROFILES_PATH).document(user!!.uid)
            Tasks.await(firestore.runTransaction { transaction ->
                val profileSnapshot = transaction.get(profileRef)
                var pendingCount = profileSnapshot.getLong(PENDING_INVOICES_KEY)
                var rejectedCount = profileSnapshot.getLong(REJECTED_INVOICES_KEY)
                if(pendingCount == null) pendingCount = 0
                if(rejectedCount == null) rejectedCount = 0
                transaction.update(databaseRef, PROCESSED_DATE_KEY, timestamp,
                    INVOICE_STATE_KEY, state)
                transaction.update(profileRef, PENDING_INVOICES_KEY, ++pendingCount)
                transaction.update(profileRef, REJECTED_INVOICES_KEY, --rejectedCount)
            }, TIMEOUT, TimeUnit.SECONDS)
            analytics.publishTransaction(user.uid)
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
    }
    @Throws(CoreError::class)
    fun retrieveCurrentUser(): FirebaseProfile? {
        try {
            val userId = firebaseAuth.currentUser?.uid
            val databaseRef = userId?.let { firestore.collection(PROFILES_PATH).document(it) }
            val result = databaseRef?.let {
                Tasks.await(it.get(), TIMEOUT, TimeUnit.SECONDS) }
            analytics.publishReadEvent(userId)
            result?.let {
                return result.toObject(FirebaseProfile::class.java)
            }
        } catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
        throw CoreError()
    }

    @Throws(CoreError::class)
    fun retrieveNotifications(): List<FirebaseNotification> {
        try {
            val userId = firebaseAuth.currentUser?.uid
            val databaseRef = userId?.let { firestore.collection("user")
                .document(userId).collection(NOTIFICATIONS_PATH) }
            val result = databaseRef?.let {
                Tasks.await(it.get(), TIMEOUT, TimeUnit.SECONDS)
            }
            analytics.publishReadEvent(userId)
            result?.let {
                return result.toObjects(FirebaseNotification::class.java)
            }
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
        throw CoreError()
    }

    fun getFileMetadata(referencePath: String): StorageMetadata {
        val ref = firestorage.reference.child(referencePath)
        return Tasks.await(ref.metadata)
    }

    fun getDownloadUrl(referencePath: String): Uri {
        val ref = firestorage.reference.child(referencePath)
        return Tasks.await(ref.downloadUrl)
    }

    fun getFilesBytes(referencePath: String): ByteArray {
        val ref = firestorage.reference.child(referencePath)
        return Tasks.await(ref.getBytes(20148*2048))
    }

    @Throws(CoreError::class)
    fun updateUserPassword(currentPassword: String, newPassword: String) {
        val user = firebaseAuth.currentUser
        val email = user?.email
        if(email.isNullOrEmpty()) {
            throw CoreError()
        } else {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            try {
                Tasks.await(user.reauthenticate(credential), TIMEOUT, TimeUnit.SECONDS)
                Tasks.await(user.updatePassword(newPassword), TIMEOUT, TimeUnit.SECONDS)
            } catch (e: ExecutionException) {
                throw CoreError(e.message)
            }catch (e: InterruptedException) {
                throw CoreError(e.message)
            }catch (e: TimeoutException) {Crashlytics.logException(e)}
        }
    }

    @Throws(CoreError::class)
    fun login(email: String, password: String) {
        try {
            val credential = EmailAuthProvider.getCredential(email, password)
            Tasks.await(firebaseAuth.signInWithCredential(credential))
        }catch (e: ExecutionException) {
            when(val cause = e.cause) {
                is FirebaseAuthInvalidUserException -> throw CoreError(e.message,
                    RepositoryError.INVALID_USER.toString())
                is FirebaseAuthInvalidCredentialsException -> throw CoreError(e.message, cause.errorCode)
                else -> throw CoreError(e.message, RepositoryError.GENERIC_ERROR.toString())
            }
        }catch (e: InterruptedException) {
            throw CoreError(e.message, RepositoryError.GENERIC_ERROR.toString())
        }
        catch (e: TimeoutException) {Crashlytics.logException(e)}
    }

    @Throws(CoreError::class)
    fun retrievePassword(email: String) {
        try {
            Tasks.await(firebaseAuth.sendPasswordResetEmail(email))
        }catch(e: ExecutionException) {
            when(e.cause) {
                is FirebaseAuthInvalidUserException -> throw CoreError(e.message,
                    RepositoryError.INVALID_USER.toString())
                else -> throw CoreError(e.message, RepositoryError.GENERIC_ERROR.toString())
            }
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
    }

    @Throws(CoreError::class)
    fun getTaxDates(): List<TaxDate> {
        try {
            val databaseRef = firestore.collection("taxes")
            val result = databaseRef.let {
                Tasks.await(it.get(), TIMEOUT, TimeUnit.SECONDS)
            }
            analytics.publishReadEvent(firebaseAuth.currentUser?.uid)
            result?.let {
                return result.toObjects(TaxDate::class.java)
            }
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
        throw CoreError()
    }

    @Throws(CoreError::class)
    fun getCounters(trimester: String, year: String): InvoiceCounters {
        try {
            val userId = firebaseAuth.currentUser?.uid
            val databaseRef = firestore.collection("user/"+ userId.toString() + "/counters/")
            val result = Tasks.await(databaseRef.whereEqualTo("trimester", trimester)
                .whereEqualTo("year", year).get(), TIMEOUT, TimeUnit.SECONDS)
            analytics.publishReadEvent(userId)
            result?.let {
                val parsed = result.toObjects(InvoiceCounters::class.java)
                return parsed[0]
            }
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
        throw CoreError()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}

enum class RepositoryError {
    INVALID_USER,
    ERROR_INVALID_EMAIL,
    ERROR_WRONG_PASSWORD,
    GENERIC_ERROR
}