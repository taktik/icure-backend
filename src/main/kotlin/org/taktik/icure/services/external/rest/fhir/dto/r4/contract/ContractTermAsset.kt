//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.contract

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.coding.Coding
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Contract Term Asset List
 *
 * Contract Term Asset List.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ContractTermAsset(
  val answer: List<ContractTermOfferAnswer> = listOf(),
  /**
   * Quality desctiption of asset
   */
  val condition: String? = null,
  val context: List<ContractTermAssetContext> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val linkId: List<String> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  val period: List<Period> = listOf(),
  val periodType: List<CodeableConcept> = listOf(),
  /**
   * Kinship of the asset
   */
  val relationship: Coding? = null,
  /**
   * Range of asset
   */
  val scope: CodeableConcept? = null,
  val securityLabelNumber: List<Int> = listOf(),
  val subtype: List<CodeableConcept> = listOf(),
  /**
   * Asset clause or question text
   */
  val text: String? = null,
  val type: List<CodeableConcept> = listOf(),
  val typeReference: List<Reference> = listOf(),
  val usePeriod: List<Period> = listOf(),
  val valuedItem: List<ContractTermAssetValuedItem> = listOf()
) : BackboneElement
