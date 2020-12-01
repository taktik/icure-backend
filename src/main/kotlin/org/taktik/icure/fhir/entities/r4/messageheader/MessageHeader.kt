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
package org.taktik.icure.fhir.entities.r4.messageheader

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * A resource that describes a message that is exchanged between systems
 *
 * The header for a message exchange that is either requesting or responding to an action.  The
 * reference(s) that are the subject of the action as well as other information related to the action
 * are typically transmitted in a bundle in which the MessageHeader resource instance is the first
 * resource in the bundle.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MessageHeader(
  /**
   * The source of the decision
   */
  val author: Reference? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Link to the definition for this message
   */
  val definition: String? = null,
  val destination: List<MessageHeaderDestination> = listOf(),
  /**
   * The source of the data entry
   */
  val enterer: Reference? = null,
  /**
   * Code for the event this message represents or link to event definition
   */
  val eventCoding: Coding,
  /**
   * Code for the event this message represents or link to event definition
   */
  val eventUri: String? = null,
  override val extension: List<Extension> = listOf(),
  val focus: List<Reference> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Cause of event
   */
  val reason: CodeableConcept? = null,
  /**
   * If this is a reply to prior message
   */
  val response: MessageHeaderResponse? = null,
  /**
   * Final responsibility for event
   */
  val responsible: Reference? = null,
  /**
   * Real world sender of the message
   */
  val sender: Reference? = null,
  /**
   * Message source application
   */
  val source: MessageHeaderSource,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
