package es.developers.achambi.afines.invoices.presenter

import android.net.Uri
import es.developer.achambi.coreframework.threading.CoreError
import es.developer.achambi.coreframework.utils.URIMetadata
import es.developer.achambi.coreframework.utils.URIUtils
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.ui.*
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase
import es.developers.achambi.afines.utils.EventLogger
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
    @Mock
    private lateinit var logger: EventLogger

    override fun setup() {
        super.setup()
        presenter = UploadPresenter(screenInterface, lifecycle,executor, uriUtils, invoiceUseCase,
            presentationBuilder, logger)
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
    fun `user saves empty name`() {
        presenter.userSaveSelected(context, uri, "")

        verify(screenInterface, times(1)).onCannotSaveInvoice()
    }

    @Test
    fun `user overrides empty name`() {
        presenter.userOverrideSelected(context, uri, "")

        verify(screenInterface, times(1)).onCannotSaveInvoice()
    }

    @Test
    fun `user valid override`() {
        val uriMetadata = URIMetadata("display")
        `when`(uriUtils.retrieveFileMetadata(context,uri)).thenReturn(uriMetadata)

        val invoiceUpload = InvoiceUpload(uriMetadata, "display")

        presenter.userOverrideSelected(context, uri, "display")

        verify(screenInterface, times(1)).onInvoicePreparedToEdit(invoiceUpload)
    }

    @Test
    fun `user saves null uri`() {
        presenter.userSaveSelected(context, null, "")

        verify(screenInterface, times(1)).onCannotSaveInvoice()
    }

    @Test
    fun`user saved valid uri`() {
        val uriMetadata = URIMetadata("display")
        `when`(uriUtils.retrieveFileMetadata(context,uri)).thenReturn(uriMetadata)

        val invoiceUpload = InvoiceUpload(uriMetadata, "name")

        presenter.userSaveSelected(context, uri, "name")

        verify(screenInterface, times(1)).onInvoicePreparedToSave(invoiceUpload)
    }

    @Test
    fun `data setup for upload`() {
        presenter.onDataSetup(invoiceId = 0L)

        verify(invoiceUseCase, never()).getInvoice(0L)
        verify(invoiceUseCase, never()).getDownloadUrl(0L)
    }

    @Test
    fun `data setup for invoice edit`() {
        val invoice = mock(Invoice::class.java)
        val presentation = mock(InvoiceUploadPresentation::class.java)
        `when`(invoiceUseCase.getInvoice(10L)).thenReturn(invoice)
        `when`(invoiceUseCase.getDownloadUrl(10L)).thenReturn(uri)
        `when`(presentationBuilder.build(invoice)).thenReturn(presentation)

        presenter.onDataSetup(10L)

        verify(screenInterface, times(1)).showScreenProgress()
        verify(screenInterface, times(1)).showScreenProgressFinished()
        verify(screenInterface, times(1)).showInvoiceUriImage(uri)
        verify(screenInterface, times(1)).showEditableInvoice(presentation)
    }

    @Test
    fun `edit invoice fetch error`() {
        doThrow(CoreError()).`when`(invoiceUseCase).getInvoice(10L)

        presenter.onDataSetup(10L)

        verify(screenInterface, times(1)).showScreenProgress()
        verify(screenInterface, times(1)).showScreenProgressFinished()
        verify(screenInterface, times(1)).showErrorRetrievingInvoice()
    }
}