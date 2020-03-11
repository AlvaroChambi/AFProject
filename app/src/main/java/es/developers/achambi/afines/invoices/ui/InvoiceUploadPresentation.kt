package es.developers.achambi.afines.invoices.ui

import es.developers.achambi.afines.invoices.model.Invoice
import java.time.Year
import java.util.*

data class InvoiceUploadPresentation(val id: Long,
                                     val name: String,
                                     val file: String,
                                     val trimester: Trimester)
class InvoiceUploadPresentationBuilder {
    fun build(invoice: Invoice): InvoiceUploadPresentation {
        return InvoiceUploadPresentation(invoice.id,
            name = invoice.name,
            file = buildFileName( invoice.fileReference ),
            trimester = invoice.trimester)
    }

    private fun buildFileName(fileReference: String): String {
        val fileDisplayPosition = 3
        val split = fileReference.split('/')
        if(split.size > fileDisplayPosition) {
            return split[fileDisplayPosition]
        }
        return ""
    }
}
enum class Trimester(val start: Int, val end: Int){
    FIRST_TRIMESTER(start = 0, end = 2),
    SECOND_TRIMESTER(start = 3, end = 5),
    THIRD_TRIMESTER(start = 6, end = 8),
    FORTH_TRIMESTER(start = 9, end = 11),
    EMPTY(0,0);
}

class TrimesterUtils {
    companion object {
        fun getCurrentTrimester(): Trimester {
            return getTrimester(Date())
        }

        fun getCurrentYear(): String {
            return Calendar.getInstance().get(Calendar.YEAR).toString()
        }

        fun getTrimester(date: Date): Trimester {
            val calendar = Calendar.getInstance()
            calendar.time = date
            return when(calendar.get(Calendar.MONTH)) {
                in Trimester.FIRST_TRIMESTER.start..Trimester.FIRST_TRIMESTER.end -> Trimester.FIRST_TRIMESTER
                in Trimester.SECOND_TRIMESTER.start..Trimester.SECOND_TRIMESTER.end -> Trimester.SECOND_TRIMESTER
                in Trimester.THIRD_TRIMESTER.start..Trimester.SECOND_TRIMESTER.end -> Trimester.THIRD_TRIMESTER
                in Trimester.FORTH_TRIMESTER.start..Trimester.FORTH_TRIMESTER.end -> Trimester.FORTH_TRIMESTER
                else -> Trimester.EMPTY
            }
        }
    }
}