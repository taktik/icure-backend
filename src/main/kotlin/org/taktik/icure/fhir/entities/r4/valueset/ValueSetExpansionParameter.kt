//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.valueset

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Parameter that controlled the expansion process
 *
 * A parameter that controlled the expansion process. These parameters may be used by users of
 * expanded value sets to check whether the expansion is suitable for a particular purpose, or to pick
 * the correct expansion.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ValueSetExpansionParameter(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name as assigned by the client or server
   */
  val name: String? = null,
  /**
   * Value of the named parameter
   */
  val valueBoolean: Boolean? = null,
  /**
   * Value of the named parameter
   */
  val valueCode: String? = null,
  /**
   * Value of the named parameter
   */
  val valueDateTime: String? = null,
  /**
   * Value of the named parameter
   */
  val valueDecimal: Float? = null,
  /**
   * Value of the named parameter
   */
  val valueInteger: Int? = null,
  /**
   * Value of the named parameter
   */
  val valueString: String? = null,
  /**
   * Value of the named parameter
   */
  val valueUri: String? = null
) : BackboneElement
