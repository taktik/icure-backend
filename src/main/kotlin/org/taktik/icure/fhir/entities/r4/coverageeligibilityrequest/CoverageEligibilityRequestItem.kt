//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.coverageeligibilityrequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.money.Money
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Item to be evaluated for eligibiity
 *
 * Service categories or billable services for which benefit details and/or an authorization prior
 * to service delivery may be required by the payor.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CoverageEligibilityRequestItem(
  /**
   * Benefit classification
   */
  val category: CodeableConcept? = null,
  val detail: List<Reference> = listOf(),
  val diagnosis: List<CoverageEligibilityRequestItemDiagnosis> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Servicing facility
   */
  val facility: Reference? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val modifier: List<CodeableConcept> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Billing, service, product, or drug code
   */
  val productOrService: CodeableConcept? = null,
  /**
   * Perfoming practitioner
   */
  val provider: Reference? = null,
  /**
   * Count of products or services
   */
  val quantity: Quantity? = null,
  val supportingInfoSequence: List<Int> = listOf(),
  /**
   * Fee, charge or cost per item
   */
  val unitPrice: Money? = null
) : BackboneElement
