package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.User
import org.taktik.icure.services.external.rest.v1.dto.UserDto
import org.taktik.icure.services.external.rest.v1.mapper.base.PropertyStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationTagMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.PermissionMapper

@Mapper(componentModel = "spring", uses = [PermissionMapper::class, DelegationTagMapper::class, PropertyStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface UserMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(userDto: UserDto):User
	fun map(user: User):UserDto
}
