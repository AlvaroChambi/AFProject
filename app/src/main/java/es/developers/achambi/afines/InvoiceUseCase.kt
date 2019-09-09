package es.developers.achambi.afines

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class InvoiceUseCase {
    private val invoices = ArrayList<Invoice>()

    fun queryUserInvoices(refresh: Boolean): ArrayList<Invoice> {
        if(refresh) {
            invoices.clear()
        }
        val user = FirebaseAuth.getInstance().currentUser
        val storage = FirebaseStorage.getInstance()
        val listRef = storage.reference.child("invoices/${user?.uid}")

        if(invoices.isNotEmpty()) {
            return invoices
        }
        val listResult = Tasks.await(listRef.listAll())
        listResult.items.forEach { item ->
            invoices.add( Invoice(item.hashCode(),
                item.name) )
        }
        return invoices
    }
}