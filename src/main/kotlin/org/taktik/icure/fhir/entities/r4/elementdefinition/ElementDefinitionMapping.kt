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
 * Map element to another set of definitions
 *
 * Identifies a concept from an external specification that roughly corresponds to this element.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ElementDefinitionMapping(
  /**
   * Comments about the mapping or its use
   */
  val comment: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Reference to mapping declaration
   */
  val identity: String? = null,
  /**
   * Computable language of mapping
   */
  val language: String? = null,
  /**
   * Details of the mapping
   */
  val map: String? = null
) : Element
