package es.developers.achambi.afines.invoices.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.chip.ChipGroup
import es.developers.achambi.afines.R

class TrimesterSelector @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ChipGroup(context, attrs, defStyleAttr) {
    init{
        init(context)
    }
    private fun init(context: Context) {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.trimester_selector_layout, this)
    }

    fun getChecked(): Trimester {
        return when(checkedChipId) {
            R.id.chip_first -> Trimester.FIRST_TRIMESTER
            R.id.chip_second -> Trimester.SECOND_TRIMESTER
            R.id.chip_third -> Trimester.THIRD_TRIMESTER
            R.id.chip_forth -> Trimester.FORTH_TRIMESTER
            else -> Trimester.EMPTY
        }
    }
}