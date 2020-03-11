package es.developers.achambi.afines.home

import es.developer.achambi.coreframework.ui.presentation.SearchListData
import es.developers.achambi.afines.repositories.model.NotificationType

class NotificationPresentation(
    val keyId: Long,
    val message: String,
    val type: NotificationType): SearchListData {
    override fun getId(): Long {
        return keyId
    }
}