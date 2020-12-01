//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.explanationofbenefit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Balance by Benefit Category
 *
 * Balance by Benefit Category.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ExplanationOfBenefitBenefitBalance(
  /**
   * Benefit classification
   */
  val category: CodeableConcept,
  /**
   * Description of the benefit or services covered
   */
  val description: String? = null,
  /**
   * Excluded from the plan
   */
  val excluded: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  val financial: List<ExplanationOfBenefitBenefitBalanceFinancial> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
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
   * Annual or lifetime
   */
  val term: CodeableConcept? = null,
  /**
   * Individual or family
   */
  val unit: CodeableConcept? = null
) : BackboneElement
