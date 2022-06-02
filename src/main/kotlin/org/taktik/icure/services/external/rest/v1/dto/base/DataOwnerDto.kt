package org.taktik.icure.services.external.rest.v1.dto.base

import org.taktik.icure.services.external.rest.v1.dto.PropertyStubDto

interface DataOwnerDto {
	val properties: Set<PropertyStubDto>
}
