package es.developers.achambi.afines

import es.developer.achambi.coreframework.threading.CoreError
import es.developers.achambi.afines.repositories.model.*

class MockSetup {
    companion object {
        private var taxDates: ArrayList<FirebaseTaxDate>? = null
        private var invoices: ArrayList<FirebaseInvoice>? = null
        private var loginSuccess = false
        private var retrievePassSuccess = false
        private var profile: FirebaseProfile? = null
        private var counters: FirebaseCounters? = null
        private var invoice: FirebaseInvoice? = null

        fun setInvoice(invoice: FirebaseInvoice) {
            this.invoice = invoice
        }

        fun getInvoice(): FirebaseInvoice? {
            return invoice
        }

        fun setProfile(profile: FirebaseProfile?) {
            this.profile = profile
        }

        fun getProfile(): FirebaseProfile? {
            return profile
        }

        fun setInvoices(invoices: ArrayList<FirebaseInvoice>?) {
            this.invoices = invoices
        }

        fun getInvoices(): List<FirebaseInvoice> {
            if(invoices == null) {
                throw CoreError()
            } else {
                return invoices as List<FirebaseInvoice>
            }
        }

        fun setRetrievePassResult(success: Boolean) {
            retrievePassSuccess = success
        }

        fun performRetrievePassword() {
            if(!retrievePassSuccess) {
                throw CoreError()
            }
        }

        fun setLoginState(success: Boolean) {
            loginSuccess = success
        }

        @Throws(CoreError::class)
        fun performLogin() {
            if(!loginSuccess) {
                throw CoreError()
            }
        }

        fun setTaxDates( dates: ArrayList<FirebaseTaxDate>? ) {
            this.taxDates = dates
        }

        @Throws(CoreError::class)
        fun getTaxDates(): ArrayList<FirebaseTaxDate> {
            if(taxDates == null) {
                throw CoreError()
            } else {
                return taxDates as ArrayList<FirebaseTaxDate>
            }
        }

        @Throws(CoreError::class)
        fun getCounters(): FirebaseCounters {
            if(counters == null) {
                throw CoreError()
            } else {
                return counters as FirebaseCounters
            }
        }

        fun setCOunters(counters: FirebaseCounters?) {
            this.counters = counters
        }
    }
}