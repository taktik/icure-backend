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
import org.taktik.icure.services.external.rest.fhir.dto.r4.money.Money

/**
 * Benefit Summary
 *
 * Benefits Used to date.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ExplanationOfBenefitBenefitBalanceFinancial(
  /**
   * Benefits allowed
   */
  val allowedMoney: Money? = null,
  /**
   * Benefits allowed
   */
  val allowedString: String? = null,
  /**
   * Benefits allowed
   */
  val allowedUnsignedInt: Int? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Benefit classification
   */
  val type: CodeableConcept,
  /**
   * Benefits used
   */
  val usedMoney: Money? = null,
  /**
   * Benefits used
   */
  val usedUnsignedInt: Int? = null
) : BackboneElement
