//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.paymentnotice

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
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * PaymentNotice request
 *
 * This resource provides the status of the payment for goods and services rendered, and the request
 * and response resource references.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PaymentNotice(
  /**
   * Monetary amount of the payment
   */
  val amount: Money,
  override val contained: List<Resource> = listOf(),
  /**
   * Creation date
   */
  val created: String? = null,
  override val extension: List<Extension> = listOf(),
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
   * Party being paid
   */
  val payee: Reference? = null,
  /**
   * Payment reference
   */
  val payment: Reference,
  /**
   * Payment or clearing date
   */
  val paymentDate: String? = null,
  /**
   * Issued or cleared Status of the payment
   */
  val paymentStatus: CodeableConcept? = null,
  /**
   * Responsible practitioner
   */
  val provider: Reference? = null,
  /**
   * Party being notified
   */
  val recipient: Reference,
  /**
   * Request reference
   */
  val request: Reference? = null,
  /**
   * Response reference
   */
  val response: Reference? = null,
  /**
   * active | cancelled | draft | entered-in-error
   */
  val status: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
