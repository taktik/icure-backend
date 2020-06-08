package org.taktik.icure.services.external.rest.v1.mapper.gui.layout

import org.mapstruct.Mapper
import org.taktik.icure.services.external.rest.v1.dto.gui.layout.FormLayout

@Mapper(componentModel = "spring")
interface FormLayoutMapper {
	fun map(formLayoutDto: FormLayout):org.taktik.icure.dto.gui.layout.FormLayout
	fun map(formLayout: org.taktik.icure.dto.gui.layout.FormLayout):FormLayout
}
