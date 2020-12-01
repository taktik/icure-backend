//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.codesystem

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Property value for the concept
 *
 * A property value for this concept.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CodeSystemConceptProperty(
  /**
   * Reference to CodeSystem.property.code
   */
  val code: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Value of the property for this concept
   */
  val valueBoolean: Boolean? = null,
  /**
   * Value of the property for this concept
   */
  val valueCode: String? = null,
  /**
   * Value of the property for this concept
   */
  val valueCoding: Coding,
  /**
   * Value of the property for this concept
   */
  val valueDateTime: String? = null,
  /**
   * Value of the property for this concept
   */
  val valueDecimal: Float? = null,
  /**
   * Value of the property for this concept
   */
  val valueInteger: Int? = null,
  /**
   * Value of the property for this concept
   */
  val valueString: String? = null
) : BackboneElement
