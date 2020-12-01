//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.coverageeligibilityrequest

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
 * CoverageEligibilityRequest resource
 *
 * The CoverageEligibilityRequest provides patient and insurance coverage information to an insurer
 * for them to respond, in the form of an CoverageEligibilityResponse, with information regarding
 * whether the stated coverage is valid and in-force and optionally to provide the insurance details of
 * the policy.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CoverageEligibilityRequest(
  override val contained: List<Resource> = listOf(),
  /**
   * Creation date
   */
  val created: String? = null,
  /**
   * Author
   */
  val enterer: Reference? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Servicing facility
   */
  val facility: Reference? = null,
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val insurance: List<CoverageEligibilityRequestInsurance> = listOf(),
  /**
   * Coverage issuer
   */
  val insurer: Reference,
  val item: List<CoverageEligibilityRequestItem> = listOf(),
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
   * Intended recipient of products and services
   */
  val patient: Reference,
  /**
   * Desired processing priority
   */
  val priority: CodeableConcept? = null,
  /**
   * Party responsible for the request
   */
  val provider: Reference? = null,
  val purpose: List<String> = listOf(),
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
  val supportingInfo: List<CoverageEligibilityRequestSupportingInfo> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
