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

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.auditevent

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.coding.Coding
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Actor involved in the event
 *
 * An actor taking an active role in the event or activity that is logged.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AuditEventAgent(
  /**
   * Alternative User identity
   */
  val altId: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Where
   */
  val location: Reference? = null,
  /**
   * Type of media
   */
  val media: Coding? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Human friendly name for the agent
   */
  val name: String? = null,
  /**
   * Logical network location for application activity
   */
  val network: AuditEventAgentNetwork? = null,
  val policy: List<String> = listOf(),
  val purposeOfUse: List<CodeableConcept> = listOf(),
  /**
   * Whether user is initiator
   */
  val requestor: Boolean? = null,
  val role: List<CodeableConcept> = listOf(),
  /**
   * How agent participated
   */
  val type: CodeableConcept? = null,
  /**
   * Identifier of who
   */
  val who: Reference? = null
) : BackboneElement
