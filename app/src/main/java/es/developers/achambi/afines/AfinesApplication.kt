package es.developers.achambi.afines

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import es.developer.achambi.coreframework.threading.MainExecutor
import es.developers.achambi.afines.repositories.FirebaseRepository
import es.developers.achambi.afines.invoices.ui.InvoicePresentationBuilder
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase

class AfinesApplication : Application() {
    companion object {
        lateinit var invoicePresenterFactory: InvoicePresenterFactory
    }
    override fun onCreate() {
        super.onCreate()
        val executor = MainExecutor.buildExecutor()
        val firebaseRepository = FirebaseRepository(FirebaseFirestore.getInstance(),
            FirebaseStorage.getInstance())
        val invoicesUseCase = InvoiceUseCase(firebaseRepository)
        val presentationBuilder = InvoicePresentationBuilder(this)

        invoicePresenterFactory = InvoicePresenterFactory(executor, invoicesUseCase, presentationBuilder)
    }
}