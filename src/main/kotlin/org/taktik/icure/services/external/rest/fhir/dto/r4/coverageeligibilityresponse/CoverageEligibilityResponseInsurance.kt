//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.coverageeligibilityresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Patient insurance information
 *
 * Financial instruments for reimbursement for the health care products and services.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CoverageEligibilityResponseInsurance(
  /**
   * When the benefits are applicable
   */
  val benefitPeriod: Period? = null,
  /**
   * Insurance information
   */
  val coverage: Reference,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Coverage inforce indicator
   */
  val inforce: Boolean? = null,
  val item: List<CoverageEligibilityResponseInsuranceItem> = listOf(),
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement
