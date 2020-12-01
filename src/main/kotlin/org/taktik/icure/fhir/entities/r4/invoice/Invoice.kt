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
package org.taktik.icure.fhir.entities.r4.invoice

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.annotation.Annotation
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.money.Money
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Invoice containing ChargeItems from an Account
 *
 * Invoice containing collected ChargeItems from an Account with calculated individual and total
 * price for Billing purpose.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Invoice(
  /**
   * Account that is being balanced
   */
  val account: Reference? = null,
  /**
   * Reason for cancellation of this Invoice
   */
  val cancelledReason: String? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Invoice date / posting date
   */
  val date: String? = null,
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
   * Issuing Organization of Invoice
   */
  val issuer: Reference? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  val lineItem: List<InvoiceLineItem> = listOf(),
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  val participant: List<InvoiceParticipant> = listOf(),
  /**
   * Payment details
   */
  val paymentTerms: String? = null,
  /**
   * Recipient of this invoice
   */
  val recipient: Reference? = null,
  /**
   * draft | issued | balanced | cancelled | entered-in-error
   */
  val status: String? = null,
  /**
   * Recipient(s) of goods and services
   */
  val subject: Reference? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Gross total of this Invoice
   */
  val totalGross: Money? = null,
  /**
   * Net total of this Invoice
   */
  val totalNet: Money? = null,
  val totalPriceComponent: List<InvoiceLineItemPriceComponent> = listOf(),
  /**
   * Type of Invoice
   */
  val type: CodeableConcept? = null
) : DomainResource
