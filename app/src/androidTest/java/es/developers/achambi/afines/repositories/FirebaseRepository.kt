package es.developers.achambi.afines.repositories

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import es.developer.achambi.coreframework.threading.CoreError
import es.developers.achambi.afines.MockSetup
import es.developers.achambi.afines.home.model.TaxDate
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.profile.presenter.ProfileUpload
import es.developers.achambi.afines.repositories.model.FirebaseInvoice
import es.developers.achambi.afines.repositories.model.FirebaseNotification
import es.developers.achambi.afines.repositories.model.FirebaseProfile
import java.util.*
import kotlin.collections.ArrayList

class FirebaseRepository(private val firestore: FirebaseFirestore,
                         private val firestorage: FirebaseStorage,
                         private val firebaseAuth: FirebaseAuth
) {
    @Throws(CoreError::class)
    fun uploadUserFile(uri: Uri, firebaseInvoice: FirebaseInvoice) {

    }

    fun userInvoices(): List<FirebaseInvoice> {
        return MockSetup.getInvoices()
    }

    @Throws(CoreError::class)
    fun deleteInvoice(invoice: Invoice) {

    }

    @Throws(CoreError::class)
    fun updateInvoiceFile(invoice: Invoice, invoiceUpload: InvoiceUpload, uri: Uri) {

    }

    @Throws(CoreError::class)
    fun logout() {

    }

    @Throws(CoreError::class)
    fun updateUserProfile(profileUpload: ProfileUpload) {

    }

    @Throws
    fun updateProfileToken(deviceToken: String) {

    }

    @Throws
    fun updateProfileToken(deviceToken: String, uid: String?) {

    }

    @Throws
    fun updateProfilePendingCount(value: Int) {

    }

    @Throws
    fun updateProfileRejectedCount(value: Int) {

    }

    @Throws
    fun checkProfilePasswordFlag() {

    }

    @Throws(CoreError::class)
    fun updateInvoiceMetadata(invoice: Invoice, name: String, trimester: String ) {

    }
    @Throws(CoreError::class)
    fun updateInvoiceState(invoice: Invoice, state: String, timestamp: Long) {

    }
    @Throws(CoreError::class)
    fun retrieveCurrentUser(): FirebaseProfile? {
        return MockSetup.getProfile()
    }

    @Throws(CoreError::class)
    fun retrieveNotifications(): List<FirebaseNotification> {
        return ArrayList()
    }

    fun getFileMetadata(referencePath: String): StorageMetadata {
        return StorageMetadata()
    }

    fun getDownloadUrl(referencePath: String): Uri {
       return Uri.parse("")
    }

    fun getFilesBytes(referencePath: String): ByteArray {
       return ByteArray(0)
    }

    @Throws(CoreError::class)
    fun updateUserPassword(currentPassword: String, newPassword: String) {

    }

    @Throws(CoreError::class)
    fun login(email: String, password: String) {
        MockSetup.performLogin()
    }

    @Throws(CoreError::class)
    fun retrievePassword(email: String) {
        MockSetup.performRetrievePassword()
    }

    @Throws(CoreError::class)
    fun getTaxDates(): List<TaxDate> {
        return MockSetup.getTaxDates()
    }

    fun getCurrentUser(): FirebaseUser? {
        return null
    }
}