//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.claimresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.money.Money

/**
 * Adjudication details
 *
 * If this item is a group then the values here are a summary of the adjudication of the detail
 * items. If this item is a simple product or service then this is the result of the adjudication of
 * this item.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ClaimResponseItemAdjudication(
  /**
   * Monetary amount
   */
  val amount: Money? = null,
  /**
   * Type of adjudication information
   */
  val category: CodeableConcept,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Explanation of adjudication outcome
   */
  val reason: CodeableConcept? = null,
  /**
   * Non-monetary value
   */
  val value: Float? = null
) : BackboneElement
