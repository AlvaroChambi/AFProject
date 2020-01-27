package es.developers.achambi.afines.invoices.presenter

import android.net.Uri
import es.developer.achambi.coreframework.utils.URIMetadata
import es.developer.achambi.coreframework.utils.URIUtils
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.ui.InvoiceUploadPresentationBuilder
import es.developers.achambi.afines.invoices.ui.Trimester
import es.developers.achambi.afines.invoices.ui.UploadScreenInterface
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase
import org.junit.Before
import org.junit.Test

import org.mockito.Mock
import org.mockito.Mockito.*

class UploadPresenterTest: BasePresenterTest() {
    @Mock
    private lateinit var uriUtils: URIUtils
    @Mock
    private lateinit var screenInterface: UploadScreenInterface
    @Mock
    private lateinit var uri: Uri
    @Mock
    private lateinit var invoiceUseCase: InvoiceUseCase
    @Mock
    private lateinit var presentationBuilder: InvoiceUploadPresentationBuilder
    private lateinit var presenter: UploadPresenter

    @Before
    fun setUp() {
        presenter = UploadPresenter(screenInterface, lifecycle,executor, uriUtils, invoiceUseCase,
            presentationBuilder)
    }

    @Test
    fun `user clears uri`(){
        presenter.userClearedURI()

        verify(screenInterface, times(1)).onURIUpdated(null, "")
    }

    @Test
    fun `user selects uri with valid display name`() {
        val uriMetadata = URIMetadata("display")
        `when`(uriUtils.retrieveFileMetadata(context,uri)).thenReturn(uriMetadata)

        presenter.userSelectedURI(context,uri)

        verify(screenInterface, times(1)).onURIUpdated(uri, "display")
    }

    @Test
    fun `user selects uri without display name`() {
        val uriMetadata = URIMetadata(null)
        `when`(uriUtils.retrieveFileMetadata(context,uri)).thenReturn(uriMetadata)

        presenter.userSelectedURI(context, uri)

        verify(screenInterface, times(1)).onURIUpdated(uri, "")
    }

    @Test
    fun `user saves null uri`() {
        presenter.userSaveSelected(context, null, "", Trimester.FORTH_TRIMESTER)

        verify(screenInterface, times(1)).onCannotSaveInvoice()
    }

    @Test
    fun`user saved valid uri`() {
        val uriMetadata = URIMetadata("display")
        `when`(uriUtils.retrieveFileMetadata(context,uri)).thenReturn(uriMetadata)

        val invoiceUpload = InvoiceUpload(uriMetadata, "name", Trimester.FORTH_TRIMESTER)

        presenter.userSaveSelected(context, uri, "name", Trimester.FORTH_TRIMESTER)

        verify(screenInterface, times(1)).onInvoicePreparedToSave(invoiceUpload)
    }
}