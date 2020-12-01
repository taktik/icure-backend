//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.coverage

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
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
 * Insurance or medical plan or a payment agreement
 *
 * Financial instrument which may be used to reimburse or pay for health care products and services.
 * Includes both insurance and self-payment.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Coverage(
  /**
   * Plan beneficiary
   */
  val beneficiary: Reference,
  @JsonProperty("class")
  val class_fhir: List<CoverageClass> = listOf(),
  override val contained: List<Resource> = listOf(),
  val contract: List<Reference> = listOf(),
  val costToBeneficiary: List<CoverageCostToBeneficiary> = listOf(),
  /**
   * Dependent number
   */
  val dependent: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
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
   * Insurer network
   */
  val network: String? = null,
  /**
   * Relative order of the coverage
   */
  val order: Int? = null,
  val payor: List<Reference> = listOf(),
  /**
   * Coverage start and end dates
   */
  val period: Period? = null,
  /**
   * Owner of the policy
   */
  val policyHolder: Reference? = null,
  /**
   * Beneficiary relationship to the subscriber
   */
  val relationship: CodeableConcept? = null,
  /**
   * active | cancelled | draft | entered-in-error
   */
  val status: String? = null,
  /**
   * Reimbursement to insurer
   */
  val subrogation: Boolean? = null,
  /**
   * Subscriber to the policy
   */
  val subscriber: Reference? = null,
  /**
   * ID assigned to the subscriber
   */
  val subscriberId: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Coverage category such as medical or accident
   */
  val type: CodeableConcept? = null
) : DomainResource
