package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
@Mapper(componentModel = "spring")
interface CodeStubMapper {
	fun map(codeStubDto: CodeStubDto):CodeStub
	fun map(codeStub: CodeStub):CodeStubDto
}
