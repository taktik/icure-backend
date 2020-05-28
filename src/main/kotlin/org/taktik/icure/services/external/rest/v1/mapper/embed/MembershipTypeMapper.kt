package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.MembershipType
import org.taktik.icure.services.external.rest.v1.dto.embed.MembershipTypeDto
@Mapper
interface MembershipTypeMapper {
	fun map(membershipTypeDto: MembershipTypeDto):MembershipType
	fun map(membershipType: MembershipType):MembershipTypeDto
}
