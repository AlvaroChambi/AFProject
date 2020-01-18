package es.developers.achambi.afines.utils

class IBANUtil {
    companion object {
        private const val IBAN_MIN_SIZE = 15
        private const val IBAN_MAX_SIZE = 34
        private const val IBAN_MAX: Long = 999999999
        private const val IBAN_MODULUS: Long = 97
        fun isIbanValid(iban: String): Boolean {

            val trimmed = iban.trim { it <= ' ' }
            if (trimmed.length < IBAN_MIN_SIZE || trimmed.length > IBAN_MAX_SIZE) {
                return false
            }
            val reformat = trimmed.substring(4) + trimmed.substring(0, 4)
            var total: Long = 0
            for (i in 0 until reformat.length) {
                val charValue = Character.getNumericValue(reformat[i])
                if (charValue < 0 || charValue > 35) {
                    return false
                }
                total = (if (charValue > 9) total * 100 else total * 10) + charValue
                if (total > IBAN_MAX) {
                    total = total % IBAN_MODULUS
                }
            }
            return total % IBAN_MODULUS == 1L
        }
    }
}