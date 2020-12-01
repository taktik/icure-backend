/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.claimresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.attachment.Attachment
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Response to a claim predetermination or preauthorization
 *
 * This resource provides the adjudication details from the processing of a Claim resource.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ClaimResponse(
  val addItem: List<ClaimResponseAddItem> = listOf(),
  val adjudication: List<ClaimResponseItemAdjudication> = listOf(),
  val communicationRequest: List<Reference> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Response creation date
   */
  val created: String? = null,
  /**
   * Disposition Message
   */
  val disposition: String? = null,
  val error: List<ClaimResponseError> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Printed reference or actual form
   */
  val form: Attachment? = null,
  /**
   * Printed form identifier
   */
  val formCode: CodeableConcept? = null,
  /**
   * Funds reserved status
   */
  val fundsReserve: CodeableConcept? = null,
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val insurance: List<ClaimResponseInsurance> = listOf(),
  /**
   * Party responsible for reimbursement
   */
  val insurer: Reference,
  val item: List<ClaimResponseItem> = listOf(),
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
   * queued | complete | error | partial
   */
  val outcome: String? = null,
  /**
   * The recipient of the products and services
   */
  val patient: Reference,
  /**
   * Party to be paid any benefits payable
   */
  val payeeType: CodeableConcept? = null,
  /**
   * Payment Details
   */
  val payment: ClaimResponsePayment? = null,
  /**
   * Preauthorization reference effective period
   */
  val preAuthPeriod: Period? = null,
  /**
   * Preauthorization reference
   */
  val preAuthRef: String? = null,
  val processNote: List<ClaimResponseProcessNote> = listOf(),
  /**
   * Id of resource triggering adjudication
   */
  val request: Reference? = null,
  /**
   * Party responsible for the claim
   */
  val requestor: Reference? = null,
  /**
   * active | cancelled | draft | entered-in-error
   */
  val status: String? = null,
  /**
   * More granular claim type
   */
  val subType: CodeableConcept? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  val total: List<ClaimResponseTotal> = listOf(),
  /**
   * More granular claim type
   */
  val type: CodeableConcept,
  /**
   * claim | preauthorization | predetermination
   */
  val use: String? = null
) : DomainResource
