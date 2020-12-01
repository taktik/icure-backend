//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.parameterdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Definition of a parameter to a module
 *
 * The parameters to the module. This collection specifies both the input and output parameters.
 * Input parameters are provided by the caller as part of the $evaluate operation. Output parameters
 * are included in the GuidanceResponse.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ParameterDefinition(
  /**
   * A brief description of the parameter
   */
  val documentation: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Maximum cardinality (a number of *)
   */
  val max: String? = null,
  /**
   * Minimum cardinality
   */
  val min: Int? = null,
  /**
   * Name used to access the parameter value
   */
  val name: String? = null,
  /**
   * What profile the value is expected to be
   */
  val profile: String? = null,
  /**
   * What type of value
   */
  val type: String? = null,
  /**
   * in | out
   */
  val use: String? = null
) : Element
