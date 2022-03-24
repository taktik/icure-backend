package org.taktik.icure.services.external.rest.v1.mapper.security

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.security.AuthenticationToken
import org.taktik.icure.services.external.rest.v1.dto.security.AuthenticationTokenDto
import org.taktik.icure.services.external.rest.v1.mapper.utils.InstantMapper
import java.time.Instant

@Mapper(componentModel = "spring", uses = [], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class AuthenticationTokenMapper {
    fun map(authenticationTokenDto: AuthenticationTokenDto): AuthenticationToken = AuthenticationToken(authenticationTokenDto.token, Instant.ofEpochMilli(authenticationTokenDto.creationTime), authenticationTokenDto.validity)
    fun map(authenticationToken: AuthenticationToken): AuthenticationTokenDto = AuthenticationTokenDto(authenticationToken.token, authenticationToken.creationTime.toEpochMilli(), authenticationToken.validity)
}
