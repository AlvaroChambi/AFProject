package es.developers.achambi.afines.invoices.ui

import es.developers.achambi.afines.invoices.model.Invoice

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
enum class Trimester{
    FIRST_TRIMESTER,
    SECOND_TRIMESTER,
    THIRD_TRIMESTER,
    FORTH_TRIMESTER,
    EMPTY
}