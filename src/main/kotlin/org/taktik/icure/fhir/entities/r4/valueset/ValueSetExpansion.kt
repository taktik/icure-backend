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
 * Used when the value set is "expanded"
 *
 * A value set can also be "expanded", where the value set is turned into a simple collection of
 * enumerated codes. This element holds the expansion, if it has been performed.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ValueSetExpansion(
  val contains: List<ValueSetExpansionContains> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Identifies the value set expansion (business identifier)
   */
  val identifier: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Offset at which this resource starts
   */
  val offset: Int? = null,
  val parameter: List<ValueSetExpansionParameter> = listOf(),
  /**
   * Time ValueSet expansion happened
   */
  val timestamp: String? = null,
  /**
   * Total number of codes in the expansion
   */
  val total: Int? = null
) : BackboneElement
