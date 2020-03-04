package es.developers.achambi.afines.invoices.presenter

import android.net.Uri
import es.developer.achambi.coreframework.threading.CoreError
import es.developer.achambi.coreframework.utils.URIMetadata
import es.developers.achambi.afines.invoices.model.Invoice
import es.developers.achambi.afines.invoices.model.InvoiceUpload
import es.developers.achambi.afines.invoices.ui.InvoicePresentationBuilder
import es.developers.achambi.afines.invoices.ui.InvoicesScreenInterface
import es.developers.achambi.afines.invoices.ui.Trimester
import es.developers.achambi.afines.invoices.usecase.InvoiceUseCase
import es.developers.achambi.afines.utils.EventLogger
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*

class InvoicePresenterTest: BasePresenterTest() {
    private lateinit var presenter: InvoicePresenter
    @Mock
    private lateinit var screen: InvoicesScreenInterface
    @Mock
    private lateinit var invoiceUseCase: InvoiceUseCase
    @Mock
    private lateinit var builder: InvoicePresentationBuilder
    @Mock
    private lateinit var logger: EventLogger

    override fun setup() {
        super.setup()
        presenter = InvoicePresenter(screen, lifecycle, executor, invoiceUseCase, builder,
            broadcastManager, logger)
    }

    @Test
    fun `test invoice upload success`() {
        val uri = mock(Uri::class.java)
        val upload = mock(InvoiceUpload::class.java)
        `when`(invoiceUseCase.uploadUserFiles(uri, upload)).thenReturn(ArrayList())
        `when`(builder.build(ArrayList())).thenReturn(ArrayList())

        presenter.uploadFile(uri, upload)

        verify(screen, times(1)).showProgress()
        verify(screen, times(1)).showProgressFinished()
        verify(screen, times(1)).showInvoices(ArrayList())
    }

    @Test
    fun `test invoice upload error`() {
        val uri = mock(Uri::class.java)
        val upload = mock(InvoiceUpload::class.java)
        doThrow(CoreError()).`when`(invoiceUseCase).uploadUserFiles(uri, upload)

        presenter.uploadFile(uri, upload)

        verify(screen, times(1)).showProgress()
        verify(screen, times(1)).showProgressFinished()
        verify(screen, times(1)).showUploadError()
    }

    @Test
    fun `test initial invoices request success`() {
        `when`(invoiceUseCase.queryUserInvoices(Trimester.FIRST_TRIMESTER,
            false)).thenReturn(ArrayList())
        `when`(builder.build(ArrayList())).thenReturn(ArrayList())

        presenter.showInvoices(Trimester.FIRST_TRIMESTER)

        verify(screen, times(1)).showFullScreenProgress()
        verify(screen, times(1)).showFullScreenProgressFinished()
        verify(screen, times(1)).showInvoices(ArrayList())
    }

    @Test
    fun `test initial invoices request error`() {
        doThrow(CoreError()).`when`(invoiceUseCase).queryUserInvoices(
            Trimester.FIRST_TRIMESTER, false)

        presenter.showInvoices(Trimester.FIRST_TRIMESTER)

        verify(screen, times(1)).showFullScreenProgress()
        verify(screen, times(1)).showFullScreenProgressFinished()
        verify(screen, times(1)).showInvoicesLoadingError()
    }

    @Test
    fun `test delete invoice success`() {
        `when`(builder.build(ArrayList())).thenReturn(ArrayList())
        `when`(invoiceUseCase.deleteInvoice(0)).thenReturn(ArrayList())

        presenter.deleteRequested(0)

        verify(screen, times(1)).showProgress()
        verify(screen, times(1)).showProgressFinished()
        verify(screen, times(1)).showInvoices(ArrayList())
        verify(screen, times(1)).showInvoiceDeleted()
    }

    @Test
    fun `test delete invoice error`() {
        doThrow(CoreError()).`when`(invoiceUseCase).deleteInvoice(0)

        presenter.deleteRequested(0)

        verify(screen, times(1)).showProgress()
        verify(screen, times(1)).showProgressFinished()
        verify(screen, times(1)).showInvoiceDeleteError()
    }

    @Test
    fun `test update invoice success`() {
        val uri = mock(Uri::class.java)
        val upload = mock(InvoiceUpload::class.java)
        val metadata = mock(URIMetadata::class.java)
        `when`(upload.uriMetadata).thenReturn(metadata)
        `when`(builder.build(ArrayList())).thenReturn(ArrayList())
        `when`(invoiceUseCase.updateInvoice(uri, upload, 0)).thenReturn(ArrayList())

        presenter.updateInvoice(uri, upload, 0)

        verify(screen, times(1)).showProgress()
        verify(screen, times(1)).showProgressFinished()
        verify(screen, times(1)).showInvoices(ArrayList())
        verify(screen, times(1)).showEditInvoiceSuccess()
    }

    @Test
    fun `test update invoice error`() {
        val uri = mock(Uri::class.java)
        val upload = mock(InvoiceUpload::class.java)
        val metadata = mock(URIMetadata::class.java)
        `when`(upload.uriMetadata).thenReturn(metadata)
        doThrow(CoreError()).`when`(invoiceUseCase).updateInvoice(uri, upload, 0)

        presenter.updateInvoice(uri, upload, 0)

        verify(screen, times(1)).showProgress()
        verify(screen, times(1)).showProgressFinished()
        verify(screen, times(1)).showEditInvoiceError()
    }
}