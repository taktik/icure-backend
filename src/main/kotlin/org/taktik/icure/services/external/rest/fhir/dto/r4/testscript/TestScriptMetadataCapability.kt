//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.testscript

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Capabilities  that are assumed to function correctly on the FHIR server being tested
 *
 * Capabilities that must exist and are assumed to function correctly on the FHIR server being
 * tested.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TestScriptMetadataCapability(
  /**
   * Required Capability Statement
   */
  val capabilities: String? = null,
  /**
   * The expected capabilities of the server
   */
  val description: String? = null,
  /**
   * Which server these requirements apply to
   */
  val destination: Int? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val link: List<String> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  val origin: List<Int> = listOf(),
  /**
   * Are the capabilities required?
   */
  val required: Boolean? = null,
  /**
   * Are the capabilities validated?
   */
  val validated: Boolean? = null
) : BackboneElement
