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
 * Include one or more codes from a code system or other value set(s)
 *
 * Include one or more codes from a code system or other value set(s).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ValueSetComposeInclude(
  val concept: List<ValueSetComposeIncludeConcept> = listOf(),
  override val extension: List<Extension> = listOf(),
  val filter: List<ValueSetComposeIncludeFilter> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The system the codes come from
   */
  val system: String? = null,
  val valueSet: List<String> = listOf(),
  /**
   * Specific version of the code system referred to
   */
  val version: String? = null
) : BackboneElement
