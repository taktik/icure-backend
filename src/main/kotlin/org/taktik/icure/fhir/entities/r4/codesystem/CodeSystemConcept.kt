//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.codesystem

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Concepts in the code system
 *
 * Concepts that are in the code system. The concept definitions are inherently hierarchical, but
 * the definitions must be consulted to determine what the meanings of the hierarchical relationships
 * are.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CodeSystemConcept(
  /**
   * Code that identifies concept
   */
  val code: String? = null,
  val concept: List<CodeSystemConcept> = listOf(),
  /**
   * Formal definition
   */
  val definition: String? = null,
  val designation: List<CodeSystemConceptDesignation> = listOf(),
  /**
   * Text to display to the user
   */
  val display: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val property: List<CodeSystemConceptProperty> = listOf()
) : BackboneElement
