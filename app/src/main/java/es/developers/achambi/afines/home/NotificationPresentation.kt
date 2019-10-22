package es.developers.achambi.afines.home

import es.developer.achambi.coreframework.ui.presentation.SearchListData

class NotificationPresentation(
    val keyId: Long,
    val message: String): SearchListData {
    override fun getId(): Long {
        return keyId
    }
}