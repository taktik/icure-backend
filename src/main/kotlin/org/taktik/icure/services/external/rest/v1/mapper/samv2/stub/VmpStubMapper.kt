package org.taktik.icure.services.external.rest.v1.mapper.samv2

import org.mapstruct.Mapper

/stub

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2/stub.VmpStub
import org.taktik.icure.services.external.rest.v1.dto.samv2/stub.VmpStubDto
@Mapper
interface VmpStubMapper {
	fun map(vmpStubDto: VmpStubDto):VmpStub
	fun map(vmpStub: VmpStub):VmpStubDto
}
