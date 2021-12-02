package org.taktik.icure.services.external.rest.v1.mapper.security

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.security.AuthenticationToken
import org.taktik.icure.services.external.rest.v1.dto.security.AuthenticationTokenDto

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AuthenticationTokenMapper {
    fun map(authenticationTokenDto: AuthenticationTokenDto): AuthenticationToken
    fun map(authenticationToken: AuthenticationToken): AuthenticationTokenDto
}
