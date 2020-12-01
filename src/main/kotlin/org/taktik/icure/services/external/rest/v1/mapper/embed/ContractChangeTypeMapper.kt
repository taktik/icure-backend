package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.ContractChangeType
import org.taktik.icure.services.external.rest.v1.dto.embed.ContractChangeTypeDto
@Mapper(componentModel = "spring")
interface ContractChangeTypeMapper {
	fun map(contractChangeTypeDto: ContractChangeTypeDto):ContractChangeType
	fun map(contractChangeType: ContractChangeType):ContractChangeTypeDto
}
