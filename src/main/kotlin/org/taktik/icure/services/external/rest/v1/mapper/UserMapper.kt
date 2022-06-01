/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.User
import org.taktik.icure.services.external.rest.v1.dto.UserDto
import org.taktik.icure.services.external.rest.v1.mapper.base.PropertyStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.security.AuthenticationTokenMapper
import org.taktik.icure.services.external.rest.v1.mapper.security.PermissionMapper

@Mapper(componentModel = "spring", uses = [PermissionMapper::class, PropertyStubMapper::class, AuthenticationTokenMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface UserMapper {
	@Mappings(
		Mapping(target = "lastLoginDate", ignore = true),
		Mapping(target = "expirationDate", ignore = true),
		Mapping(target = "attachments", ignore = true),
		Mapping(target = "revHistory", ignore = true),
		Mapping(target = "conflicts", ignore = true),
		Mapping(target = "revisionsInfo", ignore = true)
	)
	fun map(userDto: UserDto): User

	@Mappings(
		Mapping(target = "passwordHash", expression = "kotlin(user.passwordHash?.let { \"*\" })"),
		Mapping(target = "secret", ignore = true),
		Mapping(target = "applicationTokens", expression = "kotlin(emptyMap())"),
		Mapping(target = "authenticationTokens", expression = "kotlin(emptyMap())")
	)
	fun map(user: User): UserDto
}
