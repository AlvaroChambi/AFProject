package es.developers.achambi.afines.invoices.presenter

import android.content.Context
import android.net.Uri
import androidx.lifecycle.Lifecycle
import es.developer.achambi.coreframework.threading.MockExecutor
import es.developer.achambi.coreframework.utils.URIMetadata
import es.developer.achambi.coreframework.utils.URIUtils
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.ui.Trimester
import es.developers.achambi.afines.invoices.ui.UploadScreenInterface
import org.junit.Before
import org.junit.Test

import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UploadPresenterTest {
    @Mock
    private lateinit var context: Context
    private val executor = MockExecutor()
    @Mock
    private lateinit var lifecycle: Lifecycle
    @Mock
    private lateinit var uriUtils: URIUtils
    @Mock
    private lateinit var screenInterface: UploadScreenInterface
    @Mock
    private lateinit var uri: Uri
    private lateinit var presenter: UploadPresenter

    @Before
    fun setUp() {
        presenter = UploadPresenter(screenInterface, lifecycle,executor, uriUtils)
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