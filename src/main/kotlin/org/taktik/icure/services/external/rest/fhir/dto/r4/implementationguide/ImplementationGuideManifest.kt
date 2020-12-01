//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.implementationguide

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Information about an assembled IG
 *
 * Information about an assembled implementation guide, created by the publication tooling.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ImplementationGuideManifest(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val image: List<String> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  val other: List<String> = listOf(),
  val page: List<ImplementationGuideManifestPage> = listOf(),
  /**
   * Location of rendered implementation guide
   */
  val rendering: String? = null,
  val resource: List<ImplementationGuideManifestResource> = listOf()
) : BackboneElement
