/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import ma.glasnost.orika.MapperFacade
import org.springframework.web.bind.annotation.*
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.ICureSessionLogic
import org.taktik.icure.services.external.rest.v1.dto.AuthenticationResponse
import org.taktik.icure.services.external.rest.v1.dto.LoginCredentials

@RestController
@RequestMapping("/rest/v1/auth")
@Api(tags = ["auth"])
class LoginController(private val mapper: MapperFacade, private val sessionLogic: ICureSessionLogic, private val healthcarePartyLogic: HealthcarePartyLogic) {

    @ApiOperation(nickname = "login", value = "login", notes = "Login using username and password")
    @PostMapping("/login")
    fun login(@RequestBody loginInfo: LoginCredentials): AuthenticationResponse {
        val response = AuthenticationResponse()
        val sessionContext = sessionLogic.login(loginInfo.username, loginInfo.password)
        response.isSuccessful = sessionContext != null && sessionContext.isAuthenticated
        if (response.isSuccessful) {
            response.healthcarePartyId = sessionLogic.currentHealthcarePartyId
            response.username = loginInfo.username
        }
        return mapper.map(response, AuthenticationResponse::class.java)
    }

    @ApiOperation(nickname = "logout", value = "logout", notes = "Logout")
    @GetMapping("/logout")
    fun logout(): AuthenticationResponse {
        sessionLogic.logout()
        return mapper.map(AuthenticationResponse(true), AuthenticationResponse::class.java)
    }

    @ApiOperation(nickname = "logoutPost", value = "logout", notes = "Logout")
    @PostMapping("/logout")
    fun logoutPost(): AuthenticationResponse {
        sessionLogic.logout()
        return mapper.map(AuthenticationResponse(true), AuthenticationResponse::class.java)
    }

}
