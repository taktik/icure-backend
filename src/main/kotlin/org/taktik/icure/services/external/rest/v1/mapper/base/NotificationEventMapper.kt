package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.NotificationEvent
import org.taktik.icure.services.external.rest.v1.dto.base.NotificationEventDto
@Mapper(componentModel = "spring")
interface NotificationEventMapper {
	fun map(notificationEventDto: NotificationEventDto):NotificationEvent
	fun map(notificationEvent: NotificationEvent):NotificationEventDto
}
