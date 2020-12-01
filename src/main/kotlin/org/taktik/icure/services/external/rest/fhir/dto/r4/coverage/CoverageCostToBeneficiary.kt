//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.coverage

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.money.Money

/**
 * Patient payments for services/products
 *
 * A suite of codes indicating the cost category and associated amount which have been detailed in
 * the policy and may have been  included on the health card.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CoverageCostToBeneficiary(
  val exception: List<CoverageCostToBeneficiaryException> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Cost category
   */
  val type: CodeableConcept? = null,
  /**
   * The amount or percentage due from the beneficiary
   */
  val valueMoney: Money,
  /**
   * The amount or percentage due from the beneficiary
   */
  val valueQuantity: Quantity
) : BackboneElement
