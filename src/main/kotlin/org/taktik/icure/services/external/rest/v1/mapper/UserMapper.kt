package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.User
import org.taktik.icure.services.external.rest.v1.dto.UserDto
@Mapper
interface UserMapper {
	fun map(userDto: UserDto):User
	fun map(user: User):UserDto
}
