package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.CommentedClassification
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.CommentedClassificationDto
@Mapper(componentModel = "spring", uses = [SamTextMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface CommentedClassificationMapper {
	fun map(commentedClassificationDto: CommentedClassificationDto):CommentedClassification
	fun map(commentedClassification: CommentedClassification):CommentedClassificationDto
}
