package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Form
import org.taktik.icure.services.external.rest.v1.dto.FormDto
@Mapper
interface FormMapper {
	fun map(formDto: FormDto):Form
	fun map(form: Form):FormDto
}
