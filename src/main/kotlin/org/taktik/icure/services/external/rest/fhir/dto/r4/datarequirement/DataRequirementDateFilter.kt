//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.datarequirement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.duration.Duration
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period

/**
 * What dates/date ranges are expected
 *
 * Date filters specify additional constraints on the data in terms of the applicable date range for
 * specific elements. Each date filter specifies an additional constraint on the data, i.e. date
 * filters are AND'ed, not OR'ed.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DataRequirementDateFilter(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * A date-valued attribute to filter on
   */
  val path: String? = null,
  /**
   * A date valued parameter to search on
   */
  val searchParam: String? = null,
  /**
   * The value of the filter, as a Period, DateTime, or Duration value
   */
  val valueDateTime: String? = null,
  /**
   * The value of the filter, as a Period, DateTime, or Duration value
   */
  val valueDuration: Duration? = null,
  /**
   * The value of the filter, as a Period, DateTime, or Duration value
   */
  val valuePeriod: Period? = null
) : Element
