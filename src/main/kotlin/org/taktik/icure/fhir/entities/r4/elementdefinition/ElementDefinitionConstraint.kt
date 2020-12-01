//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.elementdefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Element
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Condition that must evaluate to true
 *
 * Formal constraints such as co-occurrence and other constraints that can be computationally
 * evaluated within the context of the instance.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ElementDefinitionConstraint(
  /**
   * FHIRPath expression of constraint
   */
  val expression: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Human description of constraint
   */
  val human: String? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Target of 'condition' reference above
   */
  val key: String? = null,
  /**
   * Why this constraint is necessary or appropriate
   */
  val requirements: String? = null,
  /**
   * error | warning
   */
  val severity: String? = null,
  /**
   * Reference to original source of constraint
   */
  val source: String? = null,
  /**
   * XPath expression of constraint
   */
  val xpath: String? = null
) : Element
