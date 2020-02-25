package es.developers.achambi.afines.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import es.developers.achambi.afines.R
import es.developers.achambi.afines.invoices.ui.Trimester
import es.developers.achambi.afines.invoices.ui.TrimesterUtils
import kotlinx.android.synthetic.main.trimester_header_view_layout.view.*
import java.util.*

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
        when(trimester) {
            Trimester.FIRST_TRIMESTER -> {
                trimester_card_title_text.text = context.getText(R.string.upload_first_trimester_text)
                trimester_card_date_text.text = "Hasta el 01 de Junio"
            }
            Trimester.SECOND_TRIMESTER -> {
                trimester_card_title_text.text = context.getText(R.string.upload_second_trimester_text)
                trimester_card_date_text.text = "Desde el 10 de Enero hasta el 01 de Junio"
            }
            Trimester.THIRD_TRIMESTER -> {
                trimester_card_title_text.text = context.getText(R.string.upload_third_trimester_text)
                trimester_card_date_text.text = "Desde el 10 de Enero hasta el 01 de Junio"
            }
            Trimester.FORTH_TRIMESTER -> {
                trimester_card_title_text.text = context.getText(R.string.upload_forth_trimester_text)
                trimester_card_date_text.text = "Desde el 10 de Enero hasta el 01 de Junio"
            }
            Trimester.EMPTY -> {}
        }
    }
}