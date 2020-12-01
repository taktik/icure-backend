//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.coverageeligibilityresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Benefits and authorization details
 *
 * Benefits and optionally current balances, and authorization details by category or service.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CoverageEligibilityResponseInsuranceItem(
  /**
   * Authorization required flag
   */
  val authorizationRequired: Boolean? = null,
  val authorizationSupporting: List<CodeableConcept> = listOf(),
  /**
   * Preauthorization requirements endpoint
   */
  val authorizationUrl: String? = null,
  val benefit: List<CoverageEligibilityResponseInsuranceItemBenefit> = listOf(),
  /**
   * Benefit classification
   */
  val category: CodeableConcept? = null,
  /**
   * Description of the benefit or services covered
   */
  val description: String? = null,
  /**
   * Excluded from the plan
   */
  val excluded: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val modifier: List<CodeableConcept> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Short name for the benefit
   */
  val name: String? = null,
  /**
   * In or out of network
   */
  val network: CodeableConcept? = null,
  /**
   * Billing, service, product, or drug code
   */
  val productOrService: CodeableConcept? = null,
  /**
   * Performing practitioner
   */
  val provider: Reference? = null,
  /**
   * Annual or lifetime
   */
  val term: CodeableConcept? = null,
  /**
   * Individual or family
   */
  val unit: CodeableConcept? = null
) : BackboneElement
