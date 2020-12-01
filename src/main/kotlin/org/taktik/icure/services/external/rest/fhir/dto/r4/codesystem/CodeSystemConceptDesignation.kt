//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.codesystem

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.coding.Coding
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Additional representations for the concept
 *
 * Additional representations for the concept - other languages, aliases, specialized purposes, used
 * for particular purposes, etc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CodeSystemConceptDesignation(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Human language of the designation
   */
  val language: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Details how this designation would be used
   */
  val use: Coding? = null,
  /**
   * The text value for this designation
   */
  val value: String? = null
) : BackboneElement
