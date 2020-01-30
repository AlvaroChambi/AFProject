package es.developers.achambi.afines

import es.developer.achambi.coreframework.threading.CoreError
import es.developers.achambi.afines.home.model.TaxDate
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.repositories.model.FirebaseInvoice
import es.developers.achambi.afines.repositories.model.FirebaseProfile

class MockSetup {
    companion object {
        private var taxDates: ArrayList<TaxDate>? = null
        private var invoices: ArrayList<FirebaseInvoice>? = null
        private var loginSuccess = false
        private var retrievePassSuccess = false
        private var profile: FirebaseProfile? = null

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

        fun setTaxDates( dates: ArrayList<TaxDate>? ) {
            this.taxDates = dates
        }

        @Throws(CoreError::class)
        fun getTaxDates(): ArrayList<TaxDate> {
            if(taxDates == null) {
                throw CoreError()
            } else {
                return taxDates as ArrayList<TaxDate>
            }
        }
    }
}