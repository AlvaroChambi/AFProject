package es.developers.achambi.afines

import android.app.Application
import android.content.Context
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.utils.URIUtils
import es.developers.achambi.afines.home.ui.TaxPresentationBuilder
import es.developers.achambi.afines.home.usecase.TaxesUseCase
import es.developers.achambi.afines.invoices.ui.InvoiceDetailsPresentationBuilder
import es.developers.achambi.afines.repositories.FirebaseRepository
import es.developers.achambi.afines.invoices.ui.InvoicePresentationBuilder
import es.developers.achambi.afines.invoices.ui.InvoiceUploadPresentationBuilder
import es.developers.achambi.afines.profile.ui.presentations.ProfilePresentationBuilder
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase
import es.developers.achambi.afines.login.usecase.LoginUseCase
import es.developers.achambi.afines.profile.usecase.ProfileUseCase

class AfinesApplication : Application() {
    companion object {
        lateinit var invoicePresenterFactory: InvoicePresenterFactory
        lateinit var invoiceDetailsPresenterFactory: InvoiceDetailsPresenterFactory
        lateinit var invoiceUploadPresenterFactory: InvoiceUploadPresenterFactory
        lateinit var profilePresenterFactory: ProfilePresenterFactory
        lateinit var overviewPresenterFactory: OverviewPresenterFactory
        lateinit var updatePasswordPresenterFactory: UpdatePasswordPresenterFactory
        lateinit var loginPresenterFactory: LoginPresenterFactory
        lateinit var retrievePasswordPresenterFactory: RetrievePasswordPresenterFactory
        lateinit var invoiceFullScreenPresenterFactory: InvoiceFullScreenPresenterFactory

        lateinit var messagingServicePresenterFactory: MessagingServicePresenterFactory

        lateinit var profileUseCase: ProfileUseCase
        const val DEFAULT_PREFERENCE = "DEFAULT_PREFERENCE"
    }
    override fun onCreate() {
        super.onCreate()
        val executor = MainExecutor.buildExecutor()
        val firebaseRepository = FirebaseRepository(FirebaseFirestore.getInstance(),
            FirebaseStorage.getInstance(), FirebaseAuth.getInstance())
        val invoicesUseCase = InvoiceUseCase(firebaseRepository)
        val taxesUseCase = TaxesUseCase(firebaseRepository)
        val presentationBuilder = InvoicePresentationBuilder(this)
        val uploadPresentationBuilder = InvoiceUploadPresentationBuilder()
        val uriUtils = URIUtils()
        val profilePresentationBuilder = ProfilePresentationBuilder()
        val taxesPresentationBuilder = TaxPresentationBuilder(this)
        val preferences = getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE)
        profileUseCase = ProfileUseCase(firebaseRepository, invoicesUseCase, preferences)
        val loginUseCase = LoginUseCase(firebaseRepository, profileUseCase)
        val broadcastManager = LocalBroadcastManager.getInstance(this)

        invoicePresenterFactory = InvoicePresenterFactory(executor, invoicesUseCase,
            presentationBuilder, broadcastManager)
        invoiceDetailsPresenterFactory = InvoiceDetailsPresenterFactory(executor, invoicesUseCase,
            InvoiceDetailsPresentationBuilder(this, presentationBuilder)
        )
        invoiceUploadPresenterFactory = InvoiceUploadPresenterFactory(executor, invoicesUseCase,
            uploadPresentationBuilder, uriUtils)

        profilePresenterFactory = ProfilePresenterFactory(executor, profileUseCase, profilePresentationBuilder)
        overviewPresenterFactory = OverviewPresenterFactory(executor, profileUseCase, taxesUseCase,
            broadcastManager, taxesPresentationBuilder)

        updatePasswordPresenterFactory = UpdatePasswordPresenterFactory(executor, profileUseCase)
        loginPresenterFactory = LoginPresenterFactory(executor, loginUseCase)
        retrievePasswordPresenterFactory = RetrievePasswordPresenterFactory(executor, loginUseCase)
        messagingServicePresenterFactory = MessagingServicePresenterFactory(executor,
            profileUseCase, broadcastManager)
        invoiceFullScreenPresenterFactory = InvoiceFullScreenPresenterFactory(executor, invoicesUseCase)
    }
}