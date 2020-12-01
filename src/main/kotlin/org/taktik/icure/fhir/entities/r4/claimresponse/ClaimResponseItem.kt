//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.claimresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Adjudication for claim line items
 *
 * A claim line. Either a simple (a product or service) or a 'group' of details which can also be a
 * simple items or groups of sub-details.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ClaimResponseItem(
  val adjudication: List<ClaimResponseItemAdjudication> = listOf(),
  val detail: List<ClaimResponseItemDetail> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Claim item instance identifier
   */
  val itemSequence: Int? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val noteNumber: List<Int> = listOf()
) : BackboneElement
