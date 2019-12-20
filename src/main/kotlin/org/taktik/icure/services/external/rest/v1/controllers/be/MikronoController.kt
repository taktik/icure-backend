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

package org.taktik.icure.services.external.rest.v1.controllers.be

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.be.mikrono.MikronoLogic
import org.taktik.icure.constants.TypedValuesType
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.dto.message.EmailOrSmsMessage
import org.taktik.icure.entities.Property
import org.taktik.icure.entities.PropertyType
import org.taktik.icure.entities.embed.TypedValue
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.SessionLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.services.external.rest.v1.dto.AppointmentDto
import org.taktik.icure.services.external.rest.v1.dto.EmailOrSmsMessageDto
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.AppointmentImportDto
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.MikronoCredentialsDto
import java.io.IOException
import java.util.function.Supplier

@RestController
@RequestMapping("/rest/v1/be_mikrono")
@Api(tags = ["be_mikrono"])
class MikronoController(private val mapper: MapperFacade,
                        private val mikronoLogic: MikronoLogic,
                        private val patientLogic: PatientLogic,
                        private val sessionLogic: SessionLogic,
                        private val userLogic: UserLogic) {

    private val uuidGenerator = UUIDGenerator()

    @ApiOperation(nickname = "setUserCredentials", value = "Set credentials for provided user")
    @PutMapping("/user/{userId}/credentials")
    fun setUserCredentials(@PathVariable userId: String,
                           @RequestBody(required = false) credentials: MikronoCredentialsDto?) {

        if (credentials == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials")
        } else {
            val u = userLogic.getUser(userId)
            if (u == null) {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid user")
            } else {
                val mikronoServer = u.properties.stream().filter { prop -> "org.taktik.icure.be.plugins.mikrono.url" == prop.type.identifier }.findAny()
                val user = u.properties.stream().filter { prop -> "org.taktik.icure.be.plugins.mikrono.user" == prop.type.identifier }.findAny()
                val password = u.properties.stream().filter { prop -> "org.taktik.icure.be.plugins.mikrono.password" == prop.type.identifier }.findAny()

                if (mikronoServer.isPresent) {
                    mikronoServer.orElseThrow<IllegalStateException>(Supplier<IllegalStateException> { IllegalStateException() }).typedValue = TypedValue(TypedValuesType.STRING, credentials.serverUrl)
                } else {
                    val p = Property(PropertyType(TypedValuesType.STRING, "org.taktik.icure.be.plugins.mikrono.url"), TypedValue(TypedValuesType.STRING, credentials.serverUrl))
                    u.properties.add(p)
                }

                if (user.isPresent) {
                    user.orElseThrow<IllegalStateException>(Supplier<IllegalStateException> { IllegalStateException() }).typedValue = TypedValue(TypedValuesType.STRING, credentials.user)
                } else {
                    val p = Property(PropertyType(TypedValuesType.STRING, "org.taktik.icure.be.plugins.mikrono.user"), TypedValue(TypedValuesType.STRING, credentials.user))
                    u.properties.add(p)
                }

                if (password.isPresent) {
                    password.orElseThrow<IllegalStateException>(Supplier<IllegalStateException> { IllegalStateException() }).typedValue = TypedValue(TypedValuesType.STRING, credentials.password)
                } else {
                    val p = Property(PropertyType(TypedValuesType.STRING, "org.taktik.icure.be.plugins.mikrono.password"), TypedValue(TypedValuesType.STRING, credentials.password))
                    u.properties.add(p)
                }

                try {
                    userLogic.updateEntities(listOf(u))
                } catch (e: Exception) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
                }
            }
        }
    }

    @ApiOperation(nickname = "register", value = "Set credentials for provided user")
    @PutMapping("/user/{userId}/register")
    fun register(@PathVariable userId: String,
                 @RequestBody credentials: MikronoCredentialsDto) {

        val u = userLogic.getUser(userId)
        if (u == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid user")
        } else {
            val token = uuidGenerator.newGUID().toString()
            u.applicationTokens["MIKRONO"] = token
            userLogic.save(u)

            val mikronoServerUrl = mikronoLogic.getMikronoServer(credentials.serverUrl)
            var mikronoToken: String? = mikronoLogic.register(mikronoServerUrl, u.id, token)
                    ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot obtain mikrono token")

            mikronoToken = mikronoLogic.getPassword(mikronoToken)

            val mikronoServer = u.properties.stream().filter { prop -> "org.taktik.icure.be.plugins.mikrono.url" == prop.type.identifier }.findAny()
            val user = u.properties.stream().filter { prop -> "org.taktik.icure.be.plugins.mikrono.user" == prop.type.identifier }.findAny()
            val password = u.properties.stream().filter { prop -> "org.taktik.icure.be.plugins.mikrono.password" == prop.type.identifier }.findAny()

            if (mikronoServer.isPresent) {
                mikronoServer.orElseThrow<IllegalStateException>(Supplier<IllegalStateException> { IllegalStateException() }).typedValue = TypedValue(TypedValuesType.STRING, mikronoServerUrl)
            } else {
                val p = Property(PropertyType(TypedValuesType.STRING, "org.taktik.icure.be.plugins.mikrono.url"), TypedValue(TypedValuesType.STRING, mikronoServerUrl))
                u.properties.add(p)
            }

            if (user.isPresent) {
                user.orElseThrow<IllegalStateException>(Supplier<IllegalStateException> { IllegalStateException() }).typedValue = TypedValue(TypedValuesType.STRING, u.id)
            } else {
                val p = Property(PropertyType(TypedValuesType.STRING, "org.taktik.icure.be.plugins.mikrono.user"), TypedValue(TypedValuesType.STRING, u.id))
                u.properties.add(p)
            }

            if (password.isPresent) {
                password.orElseThrow<IllegalStateException>(Supplier<IllegalStateException> { IllegalStateException() }).typedValue = TypedValue(TypedValuesType.STRING, mikronoToken)
            } else {
                val p = Property(PropertyType(TypedValuesType.STRING, "org.taktik.icure.be.plugins.mikrono.password"), TypedValue(TypedValuesType.STRING, mikronoToken))
                u.properties.add(p)
            }

            userLogic.save(u)
        }

    }

    @ApiOperation(nickname = "sendMessage", value = "Send message using mikrono from logged user")
    @PostMapping("/sendMessage")
    fun sendMessage(@RequestBody message: EmailOrSmsMessageDto) {
        val loggedUser = sessionLogic.currentSessionContext.user

        val loggedMikronoUser = loggedUser.properties.stream().filter { p -> p.type.identifier == "org.taktik.icure.be.plugins.mikrono.user" }.findFirst().map { p -> p.typedValue.stringValue }.orElse(null)
        val loggedMikronoPassword = loggedUser.properties.stream().filter { p -> p.type.identifier == "org.taktik.icure.be.plugins.mikrono.password" }.findFirst().map { p -> p.typedValue.stringValue }.orElse(null)
        if (loggedMikronoUser != null && loggedMikronoPassword != null) {
            try {
                mikronoLogic.sendMessage(null, loggedMikronoUser, loggedMikronoPassword, mapper.map(message, EmailOrSmsMessage::class.java))
            } catch (e: IOException) {
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
            }
        }
    }

    @ApiOperation(nickname = "notify", value = "Notify of an appointment change")
    @GetMapping("/notify/{appointmentId}/{action}")
    fun notify(@PathVariable appointmentId: String,
               @PathVariable action: String) {
    }

    @ApiOperation(nickname = "appointmentsByDate", value = "Get appointments for patient")
    @GetMapping("/appointments/byDate/{calendarDate}")
    fun appointmentsByDate(@PathVariable calendarDate: Long): List<AppointmentDto> {
        val loggedUser = sessionLogic.currentSessionContext.user

        val loggedMikronoUser = loggedUser.properties.stream().filter { p -> p.type.identifier == "org.taktik.icure.be.plugins.mikrono.user" }.findFirst().map { p -> p.typedValue.stringValue }.orElse(null)
        val loggedMikronoPassword = loggedUser.properties.stream().filter { p -> p.type.identifier == "org.taktik.icure.be.plugins.mikrono.password" }.findFirst().map { p -> p.typedValue.stringValue }.orElse(null)

        return if (loggedMikronoUser != null && loggedMikronoPassword != null) {
            mikronoLogic.getAppointmentsByDate(null, loggedMikronoUser, loggedMikronoPassword, loggedUser.id, calendarDate)
        } else listOf()
    }

    @ApiOperation(nickname = "appointmentsByPatient", value = "Get appointments for patient")
    @GetMapping("/appointments/byPatient/{patientId}")
    fun appointmentsByPatient(@PathVariable patientId: String,
                              @RequestParam(required = false) from: Long?,
                              @RequestParam(required = false) to: Long?): List<AppointmentDto> {
        val loggedUser = sessionLogic.currentSessionContext.user

        val loggedMikronoUser = loggedUser.properties.stream().filter { p -> p.type.identifier == "org.taktik.icure.be.plugins.mikrono.user" }.findFirst().map { p -> p.typedValue.stringValue }.orElse(null)
        val loggedMikronoPassword = loggedUser.properties.stream().filter { p -> p.type.identifier == "org.taktik.icure.be.plugins.mikrono.password" }.findFirst().map { p -> p.typedValue.stringValue }.orElse(null)

        return if (loggedMikronoUser != null && loggedMikronoPassword != null) {
            mikronoLogic.getAppointmentsByPatient(null, loggedMikronoUser, loggedMikronoPassword, loggedUser.id, patientId, from, to)
        } else listOf()
    }

    @ApiOperation(nickname = "createAppointments", value = "Create appointments for owner")
    @PostMapping("/appointments")
    fun createAppointments(@RequestBody appointments: List<AppointmentImportDto>): List<String> {
        val loggedUser = sessionLogic.currentSessionContext.user

        val loggedMikronoUser = loggedUser.properties.stream().filter { p -> p.type.identifier == "org.taktik.icure.be.plugins.mikrono.user" }.findFirst().map { p -> p.typedValue.stringValue }.orElse(null)
        val loggedMikronoPassword = loggedUser.properties.stream().filter { p -> p.type.identifier == "org.taktik.icure.be.plugins.mikrono.password" }.findFirst().map { p -> p.typedValue.stringValue }.orElse(null)

        return if (loggedMikronoUser != null && loggedMikronoPassword != null) {
            mikronoLogic.createAppointments(null, loggedMikronoUser, loggedMikronoPassword, appointments)
        } else listOf("Missing Mikrono username/password for user")
    }
}