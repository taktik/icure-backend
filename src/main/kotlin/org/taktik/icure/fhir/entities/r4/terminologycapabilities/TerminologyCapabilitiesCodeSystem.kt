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
 * A code system supported by the server
 *
 * Identifies a code system that is supported by the server. If there is a no code system URL, then
 * this declares the general assumptions a client can make about support for any CodeSystem resource.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TerminologyCapabilitiesCodeSystem(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Whether subsumption is supported
   */
  val subsumption: Boolean? = null,
  /**
   * URI for the Code System
   */
  val uri: String? = null,
  val version: List<TerminologyCapabilitiesCodeSystemVersion> = listOf()
) : BackboneElement
