package es.developers.achambi.afines

import android.app.Application
import android.content.Context
import android.util.Patterns
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developer.achambi.coreframework.utils.URIUtils
import es.developers.achambi.afines.home.usecase.TaxesUseCase
import es.developers.achambi.afines.invoices.ui.InvoiceDetailsPresentationBuilder
import es.developers.achambi.afines.repositories.FirebaseRepository
import es.developers.achambi.afines.invoices.ui.InvoicePresentationBuilder
import es.developers.achambi.afines.invoices.ui.InvoiceUploadPresentationBuilder
import es.developers.achambi.afines.profile.ui.presentations.ProfilePresentationBuilder
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase
import es.developers.achambi.afines.login.usecase.LoginUseCase
import es.developers.achambi.afines.profile.usecase.ProfileUseCase
import es.developers.achambi.afines.ui.OverviewPresentationBuilder
import es.developers.achambi.afines.utils.EventLogger

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
        lateinit var baseTestPresenterFactory: BaseTestPresenterFactory
        lateinit var profileUseCase: ProfileUseCase
        lateinit var taxesUseCase: TaxesUseCase

        val executor = MainExecutor.buildExecutor()
        const val DEFAULT_PREFERENCE = "DEFAULT_PREFERENCE"
    }
    override fun onCreate() {
        super.onCreate()
        val analytics = EventLogger(FirebaseAnalytics.getInstance(this))
        val firebaseRepository = FirebaseRepository(FirebaseFirestore.getInstance(),
            FirebaseStorage.getInstance(), FirebaseAuth.getInstance(), analytics)
        val invoicesUseCase = InvoiceUseCase(firebaseRepository)
        val presentationBuilder = InvoicePresentationBuilder(this)
        val uploadPresentationBuilder = InvoiceUploadPresentationBuilder()
        val uriUtils = URIUtils()
        val profilePresentationBuilder = ProfilePresentationBuilder()
        val preferences = getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE)
        taxesUseCase = TaxesUseCase(firebaseRepository)
        profileUseCase = ProfileUseCase(firebaseRepository, invoicesUseCase, taxesUseCase ,preferences)
        val loginUseCase = LoginUseCase(firebaseRepository, profileUseCase)
        val broadcastManager = LocalBroadcastManager.getInstance(this)

        invoicePresenterFactory = InvoicePresenterFactory(executor, invoicesUseCase,
            presentationBuilder, broadcastManager, analytics)
        invoiceDetailsPresenterFactory = InvoiceDetailsPresenterFactory(executor, invoicesUseCase,
            InvoiceDetailsPresentationBuilder(this, presentationBuilder)
        )
        invoiceUploadPresenterFactory = InvoiceUploadPresenterFactory(executor, invoicesUseCase,
            uploadPresentationBuilder, uriUtils, analytics)

        profilePresenterFactory = ProfilePresenterFactory(executor, profileUseCase, profilePresentationBuilder,
            Patterns.EMAIL_ADDRESS, analytics)
        overviewPresenterFactory = OverviewPresenterFactory(executor, profileUseCase,
            broadcastManager, OverviewPresentationBuilder(this), analytics)

        updatePasswordPresenterFactory = UpdatePasswordPresenterFactory(executor, profileUseCase, analytics)
        loginPresenterFactory = LoginPresenterFactory(executor, loginUseCase, analytics)
        retrievePasswordPresenterFactory = RetrievePasswordPresenterFactory(executor, loginUseCase, analytics)
        messagingServicePresenterFactory = MessagingServicePresenterFactory(executor,
            profileUseCase, broadcastManager)
        invoiceFullScreenPresenterFactory = InvoiceFullScreenPresenterFactory(executor, invoicesUseCase)
        baseTestPresenterFactory = BaseTestPresenterFactory(profileUseCase)
    }
}