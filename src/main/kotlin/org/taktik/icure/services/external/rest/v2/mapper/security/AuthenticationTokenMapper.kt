package org.taktik.icure.services.external.rest.v2.mapper.security

import java.time.Instant
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.security.AuthenticationToken
import org.taktik.icure.services.external.rest.v2.dto.security.AuthenticationTokenDto

@Mapper(componentModel = "spring", uses = [], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class AuthenticationTokenV2Mapper {
	fun map(authenticationTokenDto: AuthenticationTokenDto): AuthenticationToken = AuthenticationToken(authenticationTokenDto.token, Instant.ofEpochMilli(authenticationTokenDto.creationTime), authenticationTokenDto.validity)
	fun map(authenticationToken: AuthenticationToken): AuthenticationTokenDto = AuthenticationTokenDto(authenticationToken.token, authenticationToken.creationTime.toEpochMilli(), authenticationToken.validity)
}
