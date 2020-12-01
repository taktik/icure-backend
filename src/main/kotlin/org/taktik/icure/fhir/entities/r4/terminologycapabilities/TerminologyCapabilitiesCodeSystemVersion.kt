//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.terminologycapabilities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Version of Code System supported
 *
 * For the code system, a list of versions that are supported by the server.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TerminologyCapabilitiesCodeSystemVersion(
  /**
   * Version identifier for this version
   */
  val code: String? = null,
  /**
   * If compositional grammar is supported
   */
  val compositional: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  val filter: List<TerminologyCapabilitiesCodeSystemVersionFilter> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * If this is the default version for this code system
   */
  val isDefault: Boolean? = null,
  val language: List<String> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  val property: List<String> = listOf()
) : BackboneElement
