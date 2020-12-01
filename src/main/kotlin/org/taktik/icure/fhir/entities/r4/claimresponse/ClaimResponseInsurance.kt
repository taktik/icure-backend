//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.claimresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Patient insurance information
 *
 * Financial instruments for reimbursement for the health care products and services specified on
 * the claim.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ClaimResponseInsurance(
  /**
   * Additional provider contract number
   */
  val businessArrangement: String? = null,
  /**
   * Adjudication results
   */
  val claimResponse: Reference? = null,
  /**
   * Insurance information
   */
  val coverage: Reference,
  override val extension: List<Extension> = listOf(),
  /**
   * Coverage to be used for adjudication
   */
  val focal: Boolean? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Insurance instance identifier
   */
  val sequence: Int? = null
) : BackboneElement
