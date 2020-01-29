package es.developers.achambi.afines.repositories

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import es.developer.achambi.coreframework.threading.Error
import es.developers.achambi.afines.home.model.TaxDate
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.profile.presenter.ProfileUpload
import es.developers.achambi.afines.repositories.model.FirebaseInvoice
import es.developers.achambi.afines.repositories.model.FirebaseNotification
import es.developers.achambi.afines.repositories.model.FirebaseProfile

class FirebaseRepository(private val firestore: FirebaseFirestore,
                         private val firestorage: FirebaseStorage,
                         private val firebaseAuth: FirebaseAuth
) {
    @Throws(Error::class)
    fun uploadUserFile(uri: Uri, firebaseInvoice: FirebaseInvoice) {

    }

    fun userInvoices(): List<FirebaseInvoice> {
        return ArrayList()
    }

    @Throws(Error::class)
    fun deleteInvoice(invoice: Invoice) {

    }

    @Throws(Error::class)
    fun updateInvoiceFile(invoice: Invoice, invoiceUpload: InvoiceUpload, uri: Uri) {

    }

    @Throws(Error::class)
    fun logout() {

    }

    @Throws(Error::class)
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

    @Throws(Error::class)
    fun updateInvoiceMetadata(invoice: Invoice, name: String, trimester: String ) {

    }
    @Throws(Error::class)
    fun updateInvoiceState(invoice: Invoice, state: String, timestamp: Long) {

    }
    @Throws(Error::class)
    fun retrieveCurrentUser(): FirebaseProfile? {
        return FirebaseProfile()
    }

    @Throws(Error::class)
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

    @Throws(Error::class)
    fun updateUserPassword(currentPassword: String, newPassword: String) {

    }

    @Throws(Error::class)
    fun login(email: String, password: String) {

    }

    @Throws(Error::class)
    fun retrievePassword(email: String) {

    }

    @Throws(Error::class)
    fun getTaxDates(): List<TaxDate> {
        return ArrayList()
    }

    fun getCurrentUser(): FirebaseUser? {
        return null
    }
}