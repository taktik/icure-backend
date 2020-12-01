//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.conceptmap

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Same source and target systems
 *
 * A group of mappings that all have the same source and target system.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ConceptMapGroup(
  val element: List<ConceptMapGroupElement> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Source system where concepts to be mapped are defined
   */
  val source: String? = null,
  /**
   * Specific version of the  code system
   */
  val sourceVersion: String? = null,
  /**
   * Target system that the concepts are to be mapped to
   */
  val target: String? = null,
  /**
   * Specific version of the  code system
   */
  val targetVersion: String? = null,
  /**
   * What to do when there is no mapping for the source concept
   */
  val unmapped: ConceptMapGroupUnmapped? = null
) : BackboneElement
