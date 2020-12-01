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
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Resource served on the REST interface
 *
 * A specification of the restful capabilities of the solution for a specific resource type.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CapabilityStatementRestResource(
  /**
   * If allows/uses conditional create
   */
  val conditionalCreate: Boolean? = null,
  /**
   * not-supported | single | multiple - how conditional delete is supported
   */
  val conditionalDelete: String? = null,
  /**
   * not-supported | modified-since | not-match | full-support
   */
  val conditionalRead: String? = null,
  /**
   * If allows/uses conditional update
   */
  val conditionalUpdate: Boolean? = null,
  /**
   * Additional information about the use of the resource type
   */
  val documentation: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val interaction: List<CapabilityStatementRestResourceInteraction> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  val operation: List<CapabilityStatementRestResourceOperation> = listOf(),
  /**
   * Base System profile for all uses of resource
   */
  val profile: String? = null,
  /**
   * Whether vRead can return past versions
   */
  val readHistory: Boolean? = null,
  val referencePolicy: List<String> = listOf(),
  val searchInclude: List<String> = listOf(),
  val searchParam: List<CapabilityStatementRestResourceSearchParam> = listOf(),
  val searchRevInclude: List<String> = listOf(),
  val supportedProfile: List<String> = listOf(),
  /**
   * A resource type that is supported
   */
  val type: String? = null,
  /**
   * If update can commit to a new identity
   */
  val updateCreate: Boolean? = null,
  /**
   * no-version | versioned | versioned-update
   */
  val versioning: String? = null
) : BackboneElement
