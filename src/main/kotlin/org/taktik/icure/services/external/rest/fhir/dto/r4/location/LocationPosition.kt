//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.location

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * The absolute geographic location
 *
 * The absolute geographic location of the Location, expressed using the WGS84 datum (This is the
 * same co-ordinate system used in KML).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class LocationPosition(
  /**
   * Altitude with WGS84 datum
   */
  val altitude: Float? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Latitude with WGS84 datum
   */
  val latitude: Float? = null,
  /**
   * Longitude with WGS84 datum
   */
  val longitude: Float? = null,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement
