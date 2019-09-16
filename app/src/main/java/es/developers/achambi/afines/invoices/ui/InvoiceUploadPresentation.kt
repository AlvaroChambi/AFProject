package es.developers.achambi.afines.invoices.ui

data class InvoiceUploadPresentation(val name: String,
                                     val file: String,
                                     val trimester: Trimester)
enum class Trimester{
    FIRST_TRIMESTER,
    SECOND_TRIMESTER,
    THRID_TRIMESTER,
    FORTH_TRIMESTER,
    EMPTY
}