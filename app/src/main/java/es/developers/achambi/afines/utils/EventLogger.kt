package es.developers.achambi.afines.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class EventLogger(private val analytics: FirebaseAnalytics) {
    companion object {
        const val LOGOUT_EVENT_KEY = "LOGOUT"
        const val PROFILE_DEEPLINK_EVENT = "PROFILE_DEEPLINK"
        const val INVOICE_DEEPLINK_EVENT = "INVOICE_DEEPLINK"
        const val INVOICE_CREATED_EVENT = "INVOICE_CREATED"
        const val INVOICE_UPDATED_EVENT = "INVOICE_UPDATED"
        const val INVOICE_DELETED_EVENT = "INVOICE_DELETED"
        const val INVOICE_QUERY_EVENT = "INVOICE_QUERY"
        const val INVOICE_REFRESHED_EVENT = "INVOICE_REFRESHED"
        const val SCANNER_SELECTED_EVENT = "SCANNER_SELECTED"
        const val GALLERY_SELECTED_EVENT = "GALLERY_SELECTED"
        const val PROFILE_UPDATED_EVENT = "PROFILE_UPDATED"
        const val PASSWORD_UPDATED_EVENT = "PASSWORD_UPDATED"
        const val PASSWORD_RETRIEVED_EVENT = "PASSWORD_RETRIEVED"
        const val FIRESTORE_READ_EVENT = "READ_EVENT"
        const val FIRESTORE_READ_INVOICES_EVENT = "READ_INVOICES_EVENT"
        const val FIRESTORE_READ_TAX_DATES_EVENT = "READ_TAX_DATES_EVENT"
        const val FIRESTORE_READ_COUNTERS_EVENT = "READ_COUNTERS_EVENT"
        const val FIRESTORE_WRITE_EVENT = "WRITE_EVENT"
        const val FIRESTORE_TRANSACTION_EVENT = "TRANSACTION_EVENT"
    }
    fun publishLoginEvent() {
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN, null)
    }

    fun publishLogoutEvent() {
        analytics.logEvent(LOGOUT_EVENT_KEY, null)
    }

    fun publishProfileDeeplinkSelected() {
        analytics.logEvent(PROFILE_DEEPLINK_EVENT, null)
    }

    fun publishInvoicesDeeplinkSelected() {
        analytics.logEvent(INVOICE_DEEPLINK_EVENT, null)
    }

    fun publishInvoiceCreated() {
        analytics.logEvent(INVOICE_CREATED_EVENT, null)
    }

    fun publishInvoiceQuery() {
        analytics.logEvent(INVOICE_QUERY_EVENT, null)
    }

    fun publishInvoiceDeleted() {
        analytics.logEvent(INVOICE_DELETED_EVENT, null)
    }

    fun publishInvoiceUpdated(displayName: String?) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CONTENT, displayName.toString())
        analytics.logEvent(INVOICE_UPDATED_EVENT, bundle)
    }

    fun publishInvoiceRefreshed() {
        analytics.logEvent(INVOICE_REFRESHED_EVENT, null)
    }

    fun publishScannerSelected() {
        analytics.logEvent(SCANNER_SELECTED_EVENT, null)
    }

    fun publishGallerySelected() {
        analytics.logEvent(GALLERY_SELECTED_EVENT, null)
    }

    fun publishProfileUpdated() {
        analytics.logEvent(PROFILE_UPDATED_EVENT, null)
    }

    fun publishPasswordUpdated() {
        analytics.logEvent(PASSWORD_UPDATED_EVENT, null)
    }

    fun publishPasswordRetrieved() {
        analytics.logEvent(PASSWORD_RETRIEVED_EVENT, null)
    }

    fun publishReadEvent(uid: String?){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CHARACTER, uid)
        analytics.logEvent(FIRESTORE_READ_EVENT, bundle)
    }

    fun publishReadInvoicesEvent(uid: String?) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CHARACTER, uid)
        analytics.logEvent(FIRESTORE_READ_INVOICES_EVENT, bundle)
    }

    fun publishReadTaxDatesEvent(uid: String?) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CHARACTER, uid)
        analytics.logEvent(FIRESTORE_READ_TAX_DATES_EVENT, bundle)
    }

    fun publishReadCountersEvent(uid: String?) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CHARACTER, uid)
        analytics.logEvent(FIRESTORE_READ_COUNTERS_EVENT, bundle)
    }

    fun publishWriteEvent(uid: String?) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CHARACTER, uid)
        analytics.logEvent(FIRESTORE_WRITE_EVENT, bundle)
    }

    fun publishTransaction(uid: String?) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CHARACTER, uid)
        analytics.logEvent(FIRESTORE_TRANSACTION_EVENT, bundle)
    }
}