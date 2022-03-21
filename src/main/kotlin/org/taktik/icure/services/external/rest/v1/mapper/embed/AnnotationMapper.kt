package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Annotation
import org.taktik.icure.services.external.rest.v1.dto.embed.AnnotationDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AnnotationMapper {
    fun map(annotationDto: AnnotationDto): Annotation
    fun map(annotation: Annotation): AnnotationDto
}
