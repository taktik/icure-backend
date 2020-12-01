//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.contract

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Context of the Contract term
 *
 * The matter of concern in the context of this provision of the agrement.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ContractTermOffer(
  val answer: List<ContractTermOfferAnswer> = listOf(),
  /**
   * Accepting party choice
   */
  val decision: CodeableConcept? = null,
  val decisionMode: List<CodeableConcept> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  val linkId: List<String> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  val party: List<ContractTermOfferParty> = listOf(),
  val securityLabelNumber: List<Int> = listOf(),
  /**
   * Human readable offer text
   */
  val text: String? = null,
  /**
   * Negotiable offer asset
   */
  val topic: Reference? = null,
  /**
   * Contract Offer Type or Form
   */
  val type: CodeableConcept? = null
) : BackboneElement
