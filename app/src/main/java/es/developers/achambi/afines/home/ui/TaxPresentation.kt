package es.developers.achambi.afines.home.ui

import android.content.Context
import es.developers.achambi.afines.utils.DateFormatUtils
import es.developers.achambi.afines.R
import es.developers.achambi.afines.home.model.TaxDate
import es.developers.achambi.afines.invoices.ui.Trimester
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class TaxPresentation(val name: String,
                      val trimester: String,
                      val daysLeft: String,
                      val date: String,
                      val sortValue: Long,
                      val id: String)

class TaxPresentationBuilder(private val context: Context) {
    fun build(taxes: List<TaxDate>): ArrayList<TaxPresentation> {
        val result = ArrayList<TaxPresentation>()
        taxes.forEach{
            if(it.date.after(Date())) {
                result.add( build(it) )
            }
        }
        return result
    }
    fun build(taxDate: TaxDate): TaxPresentation {
        val trimester = when(taxDate.trimester) {
            Trimester.FIRST_TRIMESTER -> context.getString(R.string.upload_first_trimester_text)
            Trimester.SECOND_TRIMESTER -> context.getString(R.string.upload_second_trimester_text)
            Trimester.THIRD_TRIMESTER -> context.getString(R.string.upload_third_trimester_text)
            Trimester.FORTH_TRIMESTER -> context.getString(R.string.upload_forth_trimester_text)
            Trimester.EMPTY -> context.getString(R.string.upload_empty_trimester_text)
        }

        val left = taxDate.date.time - Date().time
        val daysLeft = TimeUnit.MILLISECONDS.toDays(left)
        return TaxPresentation(
            taxDate.name,
            trimester,
            context.resources.getQuantityString(R.plurals.taxes_days_left_text,
                daysLeft.toInt(), daysLeft.toString()),
            DateFormatUtils.formatDateSimplified(taxDate.date),
            taxDate.date.time,
            taxDate.id
        )
    }
}