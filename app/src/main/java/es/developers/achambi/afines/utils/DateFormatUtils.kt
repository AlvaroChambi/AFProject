package es.developers.achambi.afines.utils

import android.content.Context
import es.developers.achambi.afines.R
import es.developers.achambi.afines.invoices.ui.Trimester
import es.developers.achambi.afines.invoices.ui.TrimesterUtils
import java.text.SimpleDateFormat
import java.util.*

object DateFormatUtils {
    private const val YEAR_MONTH_DAY_HOUR_MINUTE_SECONDS = "yyyyMMdd_HHmmss"
    private const val DAY_MONTH_YEAR = "dd MMM yyyy"
    private const val RANGE_DATE_MONTH_DAY = "dd 'de' MMMM"
    fun formatDateDetailed(date: Date) : String {
        val dateFormat = SimpleDateFormat(YEAR_MONTH_DAY_HOUR_MINUTE_SECONDS, Locale.getDefault())
        return dateFormat.format(date)
    }

    fun formatDateSimplified(date: Date): String {
        val dateFormat = SimpleDateFormat(DAY_MONTH_YEAR, Locale.getDefault())
        return dateFormat.format(date)
    }

    fun getStartAndEndDate(startMonth: Int, endMonth: Int): Pair<Date, Date>  {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.MONTH, startMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = calendar.time

        calendar.set(Calendar.MONTH, endMonth)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDate = calendar.time

        return Pair<Date, Date>( startDate, endDate )
    }

    fun formatDateRange(context: Context, trimester: Trimester): String {
        val dateFormat = SimpleDateFormat(RANGE_DATE_MONTH_DAY, Locale.getDefault())
        val dates = getStartAndEndDate(trimester.start, trimester.end)
        return if(trimester == TrimesterUtils.getCurrentTrimester()) {
            context.getString(R.string.invoices_header_trimester_dates_from,
                dateFormat.format(dates.second.time))
        } else {
            context.getString(R.string.invoices_header_trimester_dates_from_to,
                dateFormat.format(dates.first.time),
                dateFormat.format(dates.second.time))
        }
    }
}