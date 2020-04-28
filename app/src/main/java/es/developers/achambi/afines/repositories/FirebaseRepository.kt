package es.developers.achambi.afines.repositories

import android.net.Uri
import com.crashlytics.android.Crashlytics
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.profile.presenter.ProfileUpload
import es.developer.achambi.coreframework.threading.CoreError
import es.developers.achambi.afines.repositories.model.*
import es.developers.achambi.afines.utils.EventLogger
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.collections.ArrayList

class FirebaseRepository(private val firestore: FirebaseFirestore,
                         private val firestorage: FirebaseStorage,
                         private val firebaseAuth: FirebaseAuth,
                         private val analytics: EventLogger) {
    companion object {
        const val INVOICES_PATH = "invoices/"
        const val PROFILES_PATH = "profiles"
        const val ADDRESS_ATTRIBUTE_KEY = "address"
        const val CCC_ATTRIBUTE_KEY = "ccc"
        const val DNI_ATTRIBUTE_KEY = "dni"
        const val EMAIL_ATTRIBUTE_KEY = "email"
        const val IBAN_ATTRIBUTE_KEY = "iban"
        const val PHONE_ATTRIBUTE_KEY = "phone1"
        const val NAF_ATTRIBUTE_KEY = "naf"
        const val NAME_ATTRIBUTE_KEY = "name"
        const val FILE_ATTRIBUTE_KEY = "fileReference"
        const val PROCESSED_DATE_KEY = "processedDate"
        const val INVOICE_STATE_KEY = "state"

        const val DEVICE_TOKEN_KEY = "token"
        const val COUNTERS_REFERENCE_KEY = "countersReference"
        const val PENDING_INVOICES_KEY = "pending"
        const val REJECTED_INVOICES_KEY = "rejected"
        const val APPROVED_INVOICES_KEY = "approved"
        const val PASSWORD_CHANGED_FLAG = "passwordChanged"
        private const val TIMEOUT = 4L
    }

    @Throws(CoreError::class)
    fun uploadUserFile(uri: Uri, firebaseInvoice: FirebaseInvoice, countersReference: String?): Long {
        val storageReference = firestorage.reference
        val user = firebaseAuth.currentUser
        val fileReference = storageReference.child(INVOICES_PATH + "${user?.uid + "/"}/${firebaseInvoice.id}")
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
            //TODO operation will fail if the counters reference is not available, we may need to
            //finish the operation before the file upload...
            val profileRef = firestore.collection(PROFILES_PATH).document(user!!.uid)
            val countersRef = firestore.collection("user/"+ user.uid + "/counters/")
                .document(countersReference!!)
            firebaseInvoice.fileReference = fileReference.path
            Tasks.await(firestore.runTransaction { transaction ->
                val countersSnapshot = transaction.get(countersRef)
                var pending = countersSnapshot.getLong(PENDING_INVOICES_KEY)
                if(pending == null) pending = 0
                var approved = countersSnapshot.getLong(APPROVED_INVOICES_KEY)
                if(approved == null) approved = 0
                transaction.update(countersRef, PENDING_INVOICES_KEY, ++pending)
                transaction.update(profileRef, PENDING_INVOICES_KEY, pending + approved)
                transaction.set(invoiceReference, firebaseInvoice)
            }, TIMEOUT, TimeUnit.SECONDS)
            analytics.publishTransaction(user.uid)
        }catch (e:ExecutionException) {
            throw CoreError(e.message)
        }catch (e:InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
        return firebaseInvoice.id
    }

    @Throws(CoreError::class)
    fun deleteInvoice(invoice: Invoice, countersReference: String?) {
        val user = firebaseAuth.currentUser
        try {
            val profileRef = firestore.collection(PROFILES_PATH).document(user!!.uid)
            val databaseRef = firestore.collection("user/"+ user.uid + "/invoices/")
                .document(invoice.dbReference)
            val countersRef = firestore.collection("user/"+ user.uid + "/counters/")
                .document(countersReference!!)
            Tasks.await(firestore.runTransaction { transaction ->
                val countersSnapshot = transaction.get(countersRef)
                var pendingCount = countersSnapshot.getLong(PENDING_INVOICES_KEY)
                if(pendingCount == null) pendingCount = 0
                var approved = countersSnapshot.getLong(APPROVED_INVOICES_KEY)
                if(approved == null) approved = 0
                transaction.delete(databaseRef)
                transaction.update(countersRef, PENDING_INVOICES_KEY, --pendingCount)
                transaction.update(profileRef, PENDING_INVOICES_KEY, pendingCount + approved)
            }, TIMEOUT ,TimeUnit.SECONDS)
            analytics.publishTransaction(user?.uid)
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
    fun updateRejectedInvoiceState(invoice: Invoice, state: String, timestamp: Long,
                                   countersReference: String?) {
        try {
            val user = firebaseAuth.currentUser
            val databaseRef = firestore.collection("user/"+ user?.uid + "/invoices/")
                .document(invoice.dbReference)
            val profileRef = firestore.collection(PROFILES_PATH).document(user!!.uid)
            val countersRef = firestore.collection("user/"+ user.uid + "/counters/")
                .document(countersReference!!)
            Tasks.await(firestore.runTransaction { transaction ->
                val countersSnapshot = transaction.get(countersRef)
                var pendingCount = countersSnapshot.getLong(PENDING_INVOICES_KEY)
                var rejectedCount = countersSnapshot.getLong(REJECTED_INVOICES_KEY)
                var approvedCount = countersSnapshot.getLong(APPROVED_INVOICES_KEY)
                if(pendingCount == null) pendingCount = 0
                if(rejectedCount == null) rejectedCount = 0
                if(approvedCount == null) approvedCount = 0
                transaction.update(databaseRef, PROCESSED_DATE_KEY, timestamp,
                    INVOICE_STATE_KEY, state)
                transaction.update(countersRef, PENDING_INVOICES_KEY, ++pendingCount)
                transaction.update(countersRef, REJECTED_INVOICES_KEY, --rejectedCount)
                transaction.update(profileRef, PENDING_INVOICES_KEY, pendingCount + approvedCount)
                transaction.update(profileRef, REJECTED_INVOICES_KEY, rejectedCount)
            }, TIMEOUT, TimeUnit.SECONDS)
            analytics.publishTransaction(user.uid)
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
    }

    @Throws(CoreError::class)
    fun fetchInvoices(start: Long, end: Long): List<FirebaseInvoice> {
        val user = firebaseAuth.currentUser
        val listRef = firestore.collection("user/"+ user?.uid + "/invoices/")
        try {
            val result = Tasks.await(listRef.whereGreaterThan("id", start)
                .whereLessThan("id", end).get())
            if(result.isEmpty) {
                return ArrayList()
            }
            analytics.publishReadInvoicesEvent(user?.uid)
            return result.toObjects(FirebaseInvoice::class.java)
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
        throw CoreError()
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
                    NAF_ATTRIBUTE_KEY, profileUpload.naf,
                    PHONE_ATTRIBUTE_KEY, profileUpload.phone
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
    fun updateProfileCountersReference(reference: String) {
        val uid = firebaseAuth.currentUser?.uid
        val databaseRef = uid?.let { firestore.collection(PROFILES_PATH).document(it) }
        try {
            databaseRef?.let {
                Tasks.await( databaseRef.update(
                    COUNTERS_REFERENCE_KEY, reference
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
    fun updateProfileToken(deviceToken: String) {
        val uid = firebaseAuth.currentUser?.uid
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
    fun updateInvoiceMetadata(invoice: Invoice, name: String ) {
        try {
            val user = firebaseAuth.currentUser
            val databaseRef = firestore.collection("user/"+ user?.uid + "/invoices/").document(invoice.dbReference)
            Tasks.await(databaseRef.update(
                NAME_ATTRIBUTE_KEY, name),
                TIMEOUT, TimeUnit.SECONDS)
            analytics.publishWriteEvent(user?.uid)
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
    fun getTaxDates(): List<FirebaseTaxDate> {
        try {
            val databaseRef = firestore.collection("taxes")
            val result = databaseRef.let {
                Tasks.await(it.whereGreaterThan("date", Date())
                    .orderBy("date", Query.Direction.ASCENDING)
                    .get(), TIMEOUT, TimeUnit.SECONDS)
            }
            analytics.publishReadTaxDatesEvent(firebaseAuth.currentUser?.uid)
            result?.let {
                return result.toObjects(FirebaseTaxDate::class.java)
            }
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
        throw CoreError()
    }

    @Throws(CoreError::class)
    fun getCounters(trimester: String, year: String): FirebaseCounters? {
        try {
            val userId = firebaseAuth.currentUser?.uid
            val databaseRef = firestore.collection("user/"+ userId.toString() + "/counters/")
            val result = Tasks.await(databaseRef.whereEqualTo("trimester", trimester)
                .whereEqualTo("year", year).get(), TIMEOUT, TimeUnit.SECONDS)
            analytics.publishReadCountersEvent(userId)
            result?.let {
                val parsed = result.toObjects(FirebaseCounters::class.java)
                if(parsed.isNotEmpty()) {
                    return parsed[0]
                }
            }
            return null
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
        throw CoreError()
    }

    @Throws(CoreError::class)
    fun createCounters(trimester: String, year: String): FirebaseCounters {
        try {
            val userId = firebaseAuth.currentUser?.uid
            val databaseRef = firestore.collection("user/"+ userId.toString() + "/counters/")
            val countersReference = databaseRef.document()
            val newCounters = FirebaseCounters(reference = countersReference.id, year = year,
                trimester = trimester)
            analytics.publishWriteEvent(userId)
            Tasks.await(countersReference.set(newCounters))
            return newCounters
        }catch (e: ExecutionException) {
            throw CoreError(e.message)
        }catch (e: InterruptedException) {
            throw CoreError(e.message)
        }catch (e: TimeoutException) {Crashlytics.logException(e)}
        throw CoreError()
    }

    @Throws(CoreError::class)
    fun fetchInvoice(invoiceId: Long): FirebaseInvoice? {
        try {
            val userId = firebaseAuth.currentUser?.uid
            val invoicesRef = firestore.collection("user/"+userId.toString()+"/invoices/")
            val result = Tasks.await(invoicesRef.whereEqualTo("id", invoiceId).get(),
                TIMEOUT, TimeUnit.SECONDS)
            if(result.isEmpty) {
                return null
            }
            analytics.publishReadEvent(userId)
            return result.toObjects(FirebaseInvoice::class.java)[0]
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