//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.healthcareservice

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Times the Service Site is available
 *
 * A collection of times that the Service Site is available.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class HealthcareServiceAvailableTime(
  /**
   * Always available? e.g. 24 hour service
   */
  val allDay: Boolean? = null,
  /**
   * Closing time of day (ignored if allDay = true)
   */
  val availableEndTime: String? = null,
  /**
   * Opening time of day (ignored if allDay = true)
   */
  val availableStartTime: String? = null,
  val daysOfWeek: List<String> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement
