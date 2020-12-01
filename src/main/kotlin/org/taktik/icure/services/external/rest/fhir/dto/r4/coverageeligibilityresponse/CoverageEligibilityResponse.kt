//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.coverageeligibilityresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * CoverageEligibilityResponse resource
 *
 * This resource provides eligibility and plan details from the processing of an
 * CoverageEligibilityRequest resource.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CoverageEligibilityResponse(
  override val contained: List<Resource> = listOf(),
  /**
   * Response creation date
   */
  val created: String? = null,
  /**
   * Disposition Message
   */
  val disposition: String? = null,
  val error: List<CoverageEligibilityResponseError> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Printed form identifier
   */
  val form: CodeableConcept? = null,
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val insurance: List<CoverageEligibilityResponseInsurance> = listOf(),
  /**
   * Coverage issuer
   */
  val insurer: Reference,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * queued | complete | error | partial
   */
  val outcome: String? = null,
  /**
   * Intended recipient of products and services
   */
  val patient: Reference,
  /**
   * Preauthorization reference
   */
  val preAuthRef: String? = null,
  val purpose: List<String> = listOf(),
  /**
   * Eligibility request reference
   */
  val request: Reference,
  /**
   * Party responsible for the request
   */
  val requestor: Reference? = null,
  /**
   * Estimated date or dates of service
   */
  val servicedDate: String? = null,
  /**
   * Estimated date or dates of service
   */
  val servicedPeriod: Period? = null,
  /**
   * active | cancelled | draft | entered-in-error
   */
  val status: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
