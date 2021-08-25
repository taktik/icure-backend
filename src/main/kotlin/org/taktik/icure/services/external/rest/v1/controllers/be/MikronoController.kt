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

package org.taktik.icure.services.external.rest.v1.controllers.be

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.be.mikrono.MikronoLogic
import org.taktik.icure.constants.TypedValuesType
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.entities.base.PropertyStub
import org.taktik.icure.entities.base.PropertyTypeStub
import org.taktik.icure.entities.embed.TypedValue
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.AppointmentImportDto
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.EmailOrSmsMessageDto
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.MikronoAppointmentTypeRestDto
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.MikronoCredentialsDto
import org.taktik.icure.services.external.rest.v1.mapper.UserMapper
import org.taktik.icure.services.external.rest.v1.mapper.mikrono.EmailOrSmsMessageMapper
import org.taktik.icure.services.external.rest.v1.utils.firstOrNull
import java.io.IOException

@RestController
@RequestMapping("/rest/v1/be_mikrono")
@Tag(name = "bemikrono")
class MikronoController(
        private val mikronoLogic: MikronoLogic,
        private val patientLogic: PatientLogic,
        private val sessionLogic: AsyncSessionLogic,
        private val userLogic: UserLogic,
        private val emailOrSmsMessageMapper: EmailOrSmsMessageMapper,
        private val userMapper: UserMapper
) {

    private val uuidGenerator = UUIDGenerator()

    @Operation(summary = "Set credentials for provided user")
    @PutMapping("/user/{userId}/credentials")
    fun setUserCredentials(@PathVariable userId: String,
                           @RequestBody(required = false) credentials: MikronoCredentialsDto?) = mono {

        if (credentials == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials")
        } else {
            val u = userLogic.getUser(userId)
            if (u == null) {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid user")
            } else {
                val mikronoServer = u.properties.find { prop -> "org.taktik.icure.be.plugins.mikrono.url" == prop.type?.identifier ?: "" }
                val user = u.properties.find { prop -> "org.taktik.icure.be.plugins.mikrono.user" == prop.type?.identifier ?: "" }
                val password = u.properties.find { prop -> "org.taktik.icure.be.plugins.mikrono.password" == prop.type?.identifier ?: "" }

                try {
                    val mkProps = listOfNotNull(mikronoServer, user, password)
                    userLogic.updateEntities(listOf(
                            u.copy(
                                    properties = u.properties.filter { !mkProps.contains(it) }.toSet() +
                                            setOf(
                                                    TypedValue<String>(type = TypedValuesType.STRING, stringValue = credentials.serverUrl).let {
                                                        mikronoServer?.copy(typedValue = it)
                                                                ?: PropertyStub(type = PropertyTypeStub(identifier = "org.taktik.icure.be.plugins.mikrono.url", type = TypedValuesType.STRING), typedValue = it)
                                                    },
                                                    TypedValue<String>(type = TypedValuesType.STRING, stringValue = credentials.user).let {
                                                        user?.copy(typedValue = it)
                                                                ?: PropertyStub(type = PropertyTypeStub(identifier = "org.taktik.icure.be.plugins.mikrono.user", type = TypedValuesType.STRING), typedValue = it)
                                                    },
                                                    TypedValue<String>(type = TypedValuesType.STRING, stringValue = credentials.password).let {
                                                        password?.copy(typedValue = it)
                                                                ?: PropertyStub(type = PropertyTypeStub(identifier = "org.taktik.icure.be.plugins.mikrono.password", type = TypedValuesType.STRING), typedValue = it)
                                                    }
                                            )
                            ))).firstOrNull()?.let { userMapper.map(it) }
                } catch (e: Exception) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
                }
            }
        }
    }

    @Operation(summary = "Set credentials for provided user")
    @PutMapping("/user/{userId}/register")
    fun register(@PathVariable userId: String,
                 @RequestBody credentials: MikronoCredentialsDto) = mono {

        val u = userLogic.getUser(userId)
        if (u == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid user")
        } else {
            val token = uuidGenerator.newGUID().toString()
            val uu = userLogic.save(u.copy(applicationTokens = u.applicationTokens + ("MIKRONO" to token))) ?: u

            val mikronoServerUrl = mikronoLogic.getMikronoServer(credentials.serverUrl)?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot obtain mikrono server url")
            var mikronoToken = mikronoLogic.register(mikronoServerUrl, u.id, token) ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot obtain mikrono token")

            mikronoToken = mikronoLogic.getPassword(mikronoToken)

            val mikronoServer = u.properties.find { prop -> "org.taktik.icure.be.plugins.mikrono.url" == prop.type?.identifier ?: "" }
            val user = u.properties.find { prop -> "org.taktik.icure.be.plugins.mikrono.user" == prop.type?.identifier ?: "" }
            val password = u.properties.find { prop -> "org.taktik.icure.be.plugins.mikrono.password" == prop.type?.identifier ?: "" }

            val mkProps = listOfNotNull(mikronoServer, user, password)
            userLogic.save(uu.copy(
                    properties = uu.properties.filter { !mkProps.contains(it) }.toSet() +
                            setOf(
                                    TypedValue<String>(type = TypedValuesType.STRING, stringValue = credentials.serverUrl).let {
                                        mikronoServer?.copy(typedValue = it)
                                                ?: PropertyStub(type = PropertyTypeStub(identifier = "org.taktik.icure.be.plugins.mikrono.url", type = TypedValuesType.STRING), typedValue = it)
                                    },
                                    TypedValue<String>(type = TypedValuesType.STRING, stringValue = credentials.user).let {
                                        user?.copy(typedValue = it)
                                                ?: PropertyStub(type = PropertyTypeStub(identifier = "org.taktik.icure.be.plugins.mikrono.user", type = TypedValuesType.STRING), typedValue = it)
                                    },
                                    TypedValue<String>(type = TypedValuesType.STRING, stringValue = credentials.password).let {
                                        password?.copy(typedValue = it)
                                                ?: PropertyStub(type = PropertyTypeStub(identifier = "org.taktik.icure.be.plugins.mikrono.password", type = TypedValuesType.STRING), typedValue = it)
                                    }
                            )
            ))?.let { userMapper.map(it) }

        }

    }

    @Operation(summary = "Send message using mikrono from logged user")
    @PostMapping("/sendMessage")
    fun sendMessage(@RequestBody message: EmailOrSmsMessageDto) = mono {
        val loggedUser = sessionLogic.getCurrentSessionContext().getUser()
        val loggedMikronoUser = loggedUser.properties.find { p -> p.type?.identifier == "org.taktik.icure.be.plugins.mikrono.user" }?.typedValue?.stringValue
        val loggedMikronoPassword = loggedUser.properties.find { p -> p.type?.identifier == "org.taktik.icure.be.plugins.mikrono.password" }?.typedValue?.stringValue

        if (loggedMikronoUser != null && loggedMikronoPassword != null) {
            try {
                mikronoLogic.sendMessage(null, loggedMikronoUser, loggedMikronoPassword, emailOrSmsMessageMapper.map(message))
            } catch (e: IOException) {
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
            }
        }
    }

    @Operation(summary = "Notify of an appointment change")
    @GetMapping("/notify/{appointmentId}/{action}")
    fun notify(@PathVariable appointmentId: String,
               @PathVariable action: String) {
    }

    @Operation(summary = "Get appointments for patient")
    @GetMapping("/appointments/byDate/{calendarDate}")
    fun appointmentsByDate(@PathVariable calendarDate: Long) = mono {
        val loggedUser = sessionLogic.getCurrentSessionContext().getUser()
        val loggedMikronoUser = loggedUser.properties.find { p -> p.type?.identifier == "org.taktik.icure.be.plugins.mikrono.user" }?.typedValue?.stringValue
        val loggedMikronoPassword = loggedUser.properties.find { p -> p.type?.identifier == "org.taktik.icure.be.plugins.mikrono.password" }?.typedValue?.stringValue

        if (loggedMikronoUser != null && loggedMikronoPassword != null) {
            mikronoLogic.getAppointmentsByDate(null, loggedMikronoUser, loggedMikronoPassword, loggedUser.id, calendarDate)
        } else listOf()
    }

    @Operation(summary = "Get appointments for patient")
    @GetMapping("/appointments/byPatient/{patientId}")
    fun appointmentsByPatient(@PathVariable patientId: String,
                              @RequestParam(required = false) from: Long?,
                              @RequestParam(required = false) to: Long?) = mono {
        val loggedUser = sessionLogic.getCurrentSessionContext().getUser()
        val loggedMikronoUser = loggedUser.properties.find { p -> p.type?.identifier == "org.taktik.icure.be.plugins.mikrono.user" }?.typedValue?.stringValue
        val loggedMikronoPassword = loggedUser.properties.find { p -> p.type?.identifier == "org.taktik.icure.be.plugins.mikrono.password" }?.typedValue?.stringValue

        if (loggedMikronoUser != null && loggedMikronoPassword != null) {
            mikronoLogic.getAppointmentsByPatient(null, loggedMikronoUser, loggedMikronoPassword, loggedUser.id, patientId, from, to)
        } else listOf()
    }

    @Operation(summary = "Create appointments for owner")
    @PostMapping("/appointments")
    fun createAppointments(@RequestBody appointments: List<AppointmentImportDto>) = mono {
        val loggedUser = sessionLogic.getCurrentSessionContext().getUser()
        val loggedMikronoUser = loggedUser.properties.find { p -> p.type?.identifier == "org.taktik.icure.be.plugins.mikrono.user" }?.typedValue?.stringValue
        val loggedMikronoPassword = loggedUser.properties.find { p -> p.type?.identifier == "org.taktik.icure.be.plugins.mikrono.password" }?.typedValue?.stringValue

        if (loggedMikronoUser != null && loggedMikronoPassword != null) {
            mikronoLogic.createAppointments(null, loggedMikronoUser, loggedMikronoPassword, appointments)
        } else throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing Mikrono username/password for user")
    }

    @PostMapping("/appointmentTypes")
    @Throws(IOException::class)
    fun createAppointmentTypes(appointmentTypes: List<MikronoAppointmentTypeRestDto?>?) = mono {
        val loggedUser = sessionLogic.getCurrentSessionContext().getUser()
        val loggedMikronoUser = loggedUser.properties.find { p -> p.type?.identifier == "org.taktik.icure.be.plugins.mikrono.user" }?.typedValue?.stringValue
        val loggedMikronoPassword = loggedUser.properties.find { p -> p.type?.identifier == "org.taktik.icure.be.plugins.mikrono.password" }?.typedValue?.stringValue

        if (loggedMikronoUser != null && loggedMikronoPassword != null) {
            mikronoLogic.createAppointmentTypes(null, loggedMikronoUser, loggedMikronoPassword, appointmentTypes ?: listOf())
        } else throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing Mikrono username/password for user")
    }


}
