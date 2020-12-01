//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.conceptmap

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Concept in target system for element
 *
 * A concept from the target value set that this concept maps to.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ConceptMapGroupElementTarget(
  /**
   * Code that identifies the target element
   */
  val code: String? = null,
  /**
   * Description of status/issues in mapping
   */
  val comment: String? = null,
  val dependsOn: List<ConceptMapGroupElementTargetDependsOn> = listOf(),
  /**
   * Display for the code
   */
  val display: String? = null,
  /**
   * relatedto | equivalent | equal | wider | subsumes | narrower | specializes | inexact |
   * unmatched | disjoint
   */
  val equivalence: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val product: List<ConceptMapGroupElementTargetDependsOn> = listOf()
) : BackboneElement
