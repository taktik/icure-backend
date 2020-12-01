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
package org.taktik.icure.fhir.entities.r4.operationdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Parameters for the operation/query
 *
 * The parameters for the operation/query.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class OperationDefinitionParameter(
  /**
   * ValueSet details if this is coded
   */
  val binding: OperationDefinitionParameterBinding? = null,
  /**
   * Description of meaning/use
   */
  val documentation: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Maximum Cardinality (a number or *)
   */
  val max: String? = null,
  /**
   * Minimum Cardinality
   */
  val min: Int? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name in Parameters.parameter.name or in URL
   */
  val name: String? = null,
  val part: List<OperationDefinitionParameter> = listOf(),
  val referencedFrom: List<OperationDefinitionParameterReferencedFrom> = listOf(),
  /**
   * number | date | string | token | reference | composite | quantity | uri | special
   */
  val searchType: String? = null,
  val targetProfile: List<String> = listOf(),
  /**
   * What type this parameter has
   */
  val type: String? = null,
  /**
   * in | out
   */
  val use: String? = null
) : BackboneElement
