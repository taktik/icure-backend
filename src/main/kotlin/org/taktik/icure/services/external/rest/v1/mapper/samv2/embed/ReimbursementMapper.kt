package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Reimbursement
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.ReimbursementDto
@Mapper(componentModel = "spring")
interface ReimbursementMapper {
	fun map(reimbursementDto: ReimbursementDto):Reimbursement
	fun map(reimbursement: Reimbursement):ReimbursementDto
}
