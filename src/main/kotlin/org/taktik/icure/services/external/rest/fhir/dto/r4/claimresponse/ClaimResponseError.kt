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

/**
 * Processing errors
 *
 * Errors encountered during the processing of the adjudication.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ClaimResponseError(
  /**
   * Error code detailing processing issues
   */
  val code: CodeableConcept,
  /**
   * Detail sequence number
   */
  val detailSequence: Int? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Item sequence number
   */
  val itemSequence: Int? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Subdetail sequence number
   */
  val subDetailSequence: Int? = null
) : BackboneElement
