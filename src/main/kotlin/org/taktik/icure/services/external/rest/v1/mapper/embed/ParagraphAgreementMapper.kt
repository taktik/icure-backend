package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.ParagraphAgreement
import org.taktik.icure.services.external.rest.v1.dto.embed.ParagraphAgreementDto
@Mapper
interface ParagraphAgreementMapper {
	fun map(paragraphAgreementDto: ParagraphAgreementDto):ParagraphAgreement
	fun map(paragraphAgreement: ParagraphAgreement):ParagraphAgreementDto
}
