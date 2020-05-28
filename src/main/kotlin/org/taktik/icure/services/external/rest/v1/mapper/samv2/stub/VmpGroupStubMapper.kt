package org.taktik.icure.services.external.rest.v1.mapper.samv2

import org.mapstruct.Mapper

/stub

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2/stub.VmpGroupStub
import org.taktik.icure.services.external.rest.v1.dto.samv2/stub.VmpGroupStubDto
@Mapper
interface VmpGroupStubMapper {
	fun map(vmpGroupStubDto: VmpGroupStubDto):VmpGroupStub
	fun map(vmpGroupStub: VmpGroupStub):VmpGroupStubDto
}
