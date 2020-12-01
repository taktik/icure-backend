package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.FormSkeleton
import org.taktik.icure.services.external.rest.v1.dto.embed.FormSkeletonDto
@Mapper(componentModel = "spring")
interface FormSkeletonMapper {
	fun map(formSkeletonDto: FormSkeletonDto):FormSkeleton
	fun map(formSkeleton: FormSkeleton):FormSkeletonDto
}
