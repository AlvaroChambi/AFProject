package es.developers.achambi.afines.invoices.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.developers.achambi.afines.R
import kotlinx.android.synthetic.main.bottomsheet_upload_fragment_layout.*

interface OptionListener {
    fun onScanSelected()
    fun onGallerySelected()
}

//TODO Not so sure about keeping this activity instance
class BottomSheetUploadFragment(private val listener: OptionListener): BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_upload_fragment_layout, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        upload_invoice_scan_action_button.setOnClickListener {
            listener.onScanSelected()
            dismiss() }
        upload_invoice_gallery_action_button.setOnClickListener {
            listener.onGallerySelected()
            dismiss()
        }
    }
}