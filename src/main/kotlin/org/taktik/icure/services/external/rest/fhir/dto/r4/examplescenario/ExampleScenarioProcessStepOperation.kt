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
package org.taktik.icure.services.external.rest.fhir.dto.r4.examplescenario

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Each interaction or action
 *
 * Each interaction or action.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ExampleScenarioProcessStepOperation(
  /**
   * A comment to be inserted in the diagram
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Who starts the transaction
   */
  val initiator: String? = null,
  /**
   * Whether the initiator is deactivated right after the transaction
   */
  val initiatorActive: Boolean? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The human-friendly name of the interaction
   */
  val name: String? = null,
  /**
   * The sequential number of the interaction
   */
  val number: String? = null,
  /**
   * Who receives the transaction
   */
  val receiver: String? = null,
  /**
   * Whether the receiver is deactivated right after the transaction
   */
  val receiverActive: Boolean? = null,
  /**
   * Each resource instance used by the initiator
   */
  val request: ExampleScenarioInstanceContainedInstance? = null,
  /**
   * Each resource instance used by the responder
   */
  val response: ExampleScenarioInstanceContainedInstance? = null,
  /**
   * The type of operation - CRUD
   */
  val type: String? = null
) : BackboneElement
