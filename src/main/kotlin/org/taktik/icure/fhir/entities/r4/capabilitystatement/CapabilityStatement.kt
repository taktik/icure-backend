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
package org.taktik.icure.fhir.entities.r4.capabilitystatement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.contactdetail.ContactDetail
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.usagecontext.UsageContext

/**
 * A statement of system capabilities
 *
 * A Capability Statement documents a set of capabilities (behaviors) of a FHIR Server for a
 * particular version of FHIR that may be used as a statement of actual server functionality or a
 * statement of required or desired server implementation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CapabilityStatement(
  val contact: List<ContactDetail> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Use and/or publishing restrictions
   */
  val copyright: String? = null,
  /**
   * Date last changed
   */
  val date: String? = null,
  /**
   * Natural language description of the capability statement
   */
  val description: String? = null,
  val document: List<CapabilityStatementDocument> = listOf(),
  /**
   * For testing purposes, not real usage
   */
  val experimental: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * FHIR Version the system supports
   */
  val fhirVersion: String? = null,
  val format: List<String> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * If this describes a specific instance
   */
  val implementation: CapabilityStatementImplementation? = null,
  val implementationGuide: List<String> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val imports: List<String> = listOf(),
  val instantiates: List<String> = listOf(),
  val jurisdiction: List<CodeableConcept> = listOf(),
  /**
   * instance | capability | requirements
   */
  val kind: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  val messaging: List<CapabilityStatementMessaging> = listOf(),
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name for this capability statement (computer friendly)
   */
  val name: String? = null,
  val patchFormat: List<String> = listOf(),
  /**
   * Name of the publisher (organization or individual)
   */
  val publisher: String? = null,
  /**
   * Why this capability statement is defined
   */
  val purpose: String? = null,
  val rest: List<CapabilityStatementRest> = listOf(),
  /**
   * Software that is covered by this capability statement
   */
  val software: CapabilityStatementSoftware? = null,
  /**
   * draft | active | retired | unknown
   */
  val status: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Name for this capability statement (human friendly)
   */
  val title: String? = null,
  /**
   * Canonical identifier for this capability statement, represented as a URI (globally unique)
   */
  val url: String? = null,
  val useContext: List<UsageContext> = listOf(),
  /**
   * Business version of the capability statement
   */
  val version: String? = null
) : DomainResource
