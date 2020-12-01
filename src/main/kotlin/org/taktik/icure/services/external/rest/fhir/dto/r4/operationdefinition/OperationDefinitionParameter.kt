//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.operationdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

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
