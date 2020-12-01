//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.capabilitystatement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Software that is covered by this capability statement
 *
 * Software that is covered by this capability statement.  It is used when the capability statement
 * describes the capabilities of a particular software version, independent of an installation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CapabilityStatementSoftware(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * A name the software is known by
   */
  val name: String? = null,
  /**
   * Date this version was released
   */
  val releaseDate: String? = null,
  /**
   * Version covered by this statement
   */
  val version: String? = null
) : BackboneElement
