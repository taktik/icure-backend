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
package org.taktik.icure.services.external.rest.fhir.dto.r4.location

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.address.Address
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.coding.Coding
import org.taktik.icure.services.external.rest.fhir.dto.r4.contactpoint.ContactPoint
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Details and position information for a physical place
 *
 * Details and position information for a physical place where services are provided and resources
 * and participants may be stored, found, contained, or accommodated.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Location(
  /**
   * Physical location
   */
  val address: Address? = null,
  val alias: List<String> = listOf(),
  /**
   * Description of availability exceptions
   */
  val availabilityExceptions: String? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Additional details about the location that could be displayed as further information to
   * identify the location beyond its name
   */
  val description: String? = null,
  val endpoint: List<Reference> = listOf(),
  override val extension: List<Extension> = listOf(),
  val hoursOfOperation: List<LocationHoursOfOperation> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Organization responsible for provisioning and upkeep
   */
  val managingOrganization: Reference? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  /**
   * instance | kind
   */
  val mode: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name of the location as used by humans
   */
  val name: String? = null,
  /**
   * The operational status of the location (typically only for a bed/room)
   */
  val operationalStatus: Coding? = null,
  /**
   * Another Location this one is physically a part of
   */
  val partOf: Reference? = null,
  /**
   * Physical form of the location
   */
  val physicalType: CodeableConcept? = null,
  /**
   * The absolute geographic location
   */
  val position: LocationPosition? = null,
  /**
   * active | suspended | inactive
   */
  val status: String? = null,
  val telecom: List<ContactPoint> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  val type: List<CodeableConcept> = listOf()
) : DomainResource
