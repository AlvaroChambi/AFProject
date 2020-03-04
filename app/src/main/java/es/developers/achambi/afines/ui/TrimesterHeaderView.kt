package es.developers.achambi.afines.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import es.developers.achambi.afines.utils.DateFormatUtils
import es.developers.achambi.afines.R
import es.developers.achambi.afines.invoices.ui.Trimester
import es.developers.achambi.afines.invoices.ui.TrimesterUtils
import kotlinx.android.synthetic.main.trimester_header_view_layout.view.*

class TrimesterHeaderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.trimester_header_view_layout, this)
        if(!isInEditMode) {
            setTrimester( TrimesterUtils.getCurrentTrimester() )
        }
    }

    fun setTrimester(trimester: Trimester) {
        trimester_card_date_text.text = DateFormatUtils.formatDateRange(context, trimester)
        when(trimester) {
            Trimester.FIRST_TRIMESTER -> {
                trimester_card_title_text.text = context.getText(R.string.upload_first_trimester_text)
            }
            Trimester.SECOND_TRIMESTER -> {
                trimester_card_title_text.text = context.getText(R.string.upload_second_trimester_text)
            }
            Trimester.THIRD_TRIMESTER -> {
                trimester_card_title_text.text = context.getText(R.string.upload_third_trimester_text)
            }
            Trimester.FORTH_TRIMESTER -> {
                trimester_card_title_text.text = context.getText(R.string.upload_forth_trimester_text)
            }
            Trimester.EMPTY -> {}
        }
    }
}