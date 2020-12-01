//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.codesystem

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Additional information supplied about each concept
 *
 * A property defines an additional slot through which additional information can be provided about
 * a concept.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CodeSystemProperty(
  /**
   * Identifies the property on the concepts, and when referred to in operations
   */
  val code: String? = null,
  /**
   * Why the property is defined, and/or what it conveys
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * code | Coding | string | integer | boolean | dateTime | decimal
   */
  val type: String? = null,
  /**
   * Formal identifier for the property
   */
  val uri: String? = null
) : BackboneElement
