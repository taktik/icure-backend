//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.medicinalproduct

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.marketingstatus.MarketingStatus
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Detailed definition of a medicinal product, typically for uses other than direct patient care
 * (e.g. regulatory use)
 *
 * Detailed definition of a medicinal product, typically for uses other than direct patient care
 * (e.g. regulatory use).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProduct(
  /**
   * Whether the Medicinal Product is subject to additional monitoring for regulatory reasons
   */
  val additionalMonitoringIndicator: CodeableConcept? = null,
  val attachedDocument: List<Reference> = listOf(),
  val clinicalTrial: List<Reference> = listOf(),
  /**
   * The dose form for a single part product, or combined form of a multiple part product
   */
  val combinedPharmaceuticalDoseForm: CodeableConcept? = null,
  val contact: List<Reference> = listOf(),
  override val contained: List<Resource> = listOf(),
  val crossReference: List<Identifier> = listOf(),
  /**
   * If this medicine applies to human or veterinary uses
   */
  val domain: Coding? = null,
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
   * The legal status of supply of the medicinal product as classified by the regulator
   */
  val legalStatusOfSupply: CodeableConcept? = null,
  val manufacturingBusinessOperation: List<MedicinalProductManufacturingBusinessOperation> =
      listOf(),
  val marketingStatus: List<MarketingStatus> = listOf(),
  val masterFile: List<Reference> = listOf(),
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val name: List<MedicinalProductName> = listOf(),
  val packagedMedicinalProduct: List<Reference> = listOf(),
  /**
   * If authorised for use in children
   */
  val paediatricUseIndicator: CodeableConcept? = null,
  val pharmaceuticalProduct: List<Reference> = listOf(),
  val productClassification: List<CodeableConcept> = listOf(),
  val specialDesignation: List<MedicinalProductSpecialDesignation> = listOf(),
  val specialMeasures: List<String> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Regulatory type, e.g. Investigational or Authorized
   */
  val type: CodeableConcept? = null
) : DomainResource
