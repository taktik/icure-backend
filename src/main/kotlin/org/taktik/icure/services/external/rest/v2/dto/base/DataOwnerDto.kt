package org.taktik.icure.services.external.rest.v2.dto.base

import org.taktik.icure.services.external.rest.v2.dto.PropertyStubDto

interface DataOwnerDto {
	val properties: Set<PropertyStubDto>
}
