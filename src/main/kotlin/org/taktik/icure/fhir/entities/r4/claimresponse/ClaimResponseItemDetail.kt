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
 * Adjudication for claim details
 *
 * A claim detail. Either a simple (a product or service) or a 'group' of sub-details which are
 * simple items.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ClaimResponseItemDetail(
  val adjudication: List<ClaimResponseItemAdjudication> = listOf(),
  /**
   * Claim detail instance identifier
   */
  val detailSequence: Int? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val noteNumber: List<Int> = listOf(),
  val subDetail: List<ClaimResponseItemDetailSubDetail> = listOf()
) : BackboneElement
