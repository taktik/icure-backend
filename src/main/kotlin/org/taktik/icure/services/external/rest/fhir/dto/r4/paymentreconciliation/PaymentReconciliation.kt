//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.paymentreconciliation

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.money.Money
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * PaymentReconciliation resource
 *
 * This resource provides the details including amount of a payment and allocates the payment items
 * being paid.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PaymentReconciliation(
  override val contained: List<Resource> = listOf(),
  /**
   * Creation date
   */
  val created: String? = null,
  val detail: List<PaymentReconciliationDetail> = listOf(),
  /**
   * Disposition message
   */
  val disposition: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Printed form identifier
   */
  val formCode: CodeableConcept? = null,
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
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
   * Total amount of Payment
   */
  val paymentAmount: Money,
  /**
   * When payment issued
   */
  val paymentDate: String? = null,
  /**
   * Business identifier for the payment
   */
  val paymentIdentifier: Identifier? = null,
  /**
   * Party generating payment
   */
  val paymentIssuer: Reference? = null,
  /**
   * Period covered
   */
  val period: Period? = null,
  val processNote: List<PaymentReconciliationProcessNote> = listOf(),
  /**
   * Reference to requesting resource
   */
  val request: Reference? = null,
  /**
   * Responsible practitioner
   */
  val requestor: Reference? = null,
  /**
   * active | cancelled | draft | entered-in-error
   */
  val status: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
