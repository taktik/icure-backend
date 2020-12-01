//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.timing

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.duration.Duration
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range

/**
 * When the event is to occur
 *
 * A set of rules that describe when the event is scheduled.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TimingRepeat(
  /**
   * Length/Range of lengths, or (Start and/or end) limits
   */
  val boundsDuration: Duration? = null,
  /**
   * Length/Range of lengths, or (Start and/or end) limits
   */
  val boundsPeriod: Period? = null,
  /**
   * Length/Range of lengths, or (Start and/or end) limits
   */
  val boundsRange: Range? = null,
  /**
   * Number of times to repeat
   */
  val count: Int? = null,
  /**
   * Maximum number of times to repeat
   */
  val countMax: Int? = null,
  val dayOfWeek: List<String> = listOf(),
  /**
   * How long when it happens
   */
  val duration: Float? = null,
  /**
   * How long when it happens (Max)
   */
  val durationMax: Float? = null,
  /**
   * s | min | h | d | wk | mo | a - unit of time (UCUM)
   */
  val durationUnit: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Event occurs frequency times per period
   */
  val frequency: Int? = null,
  /**
   * Event occurs up to frequencyMax times per period
   */
  val frequencyMax: Int? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Minutes from event (before or after)
   */
  val offset: Int? = null,
  /**
   * Event occurs frequency times per period
   */
  val period: Float? = null,
  /**
   * Upper limit of period (3-4 hours)
   */
  val periodMax: Float? = null,
  /**
   * s | min | h | d | wk | mo | a - unit of time (UCUM)
   */
  val periodUnit: String? = null,
  val timeOfDay: List<String> = listOf(),
  @JsonProperty("when")
  val when_fhir: List<String> = listOf()
) : Element
