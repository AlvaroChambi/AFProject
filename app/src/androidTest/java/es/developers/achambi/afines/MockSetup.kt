package es.developers.achambi.afines

import es.developer.achambi.coreframework.threading.CoreError
import es.developers.achambi.afines.home.model.TaxDate

class MockSetup {
    companion object {
        private var taxDates: ArrayList<TaxDate>? = null
        private var loginSuccess = false

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