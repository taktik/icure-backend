//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.location

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * What days/times during a week is this location usually open
 *
 * What days/times during a week is this location usually open.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class LocationHoursOfOperation(
  /**
   * The Location is open all day
   */
  val allDay: Boolean? = null,
  /**
   * Time that the Location closes
   */
  val closingTime: String? = null,
  val daysOfWeek: List<String> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Time that the Location opens
   */
  val openingTime: String? = null
) : BackboneElement
