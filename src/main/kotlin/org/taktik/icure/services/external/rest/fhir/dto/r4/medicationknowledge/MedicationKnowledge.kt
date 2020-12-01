//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicationknowledge

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Definition of Medication Knowledge
 *
 * Information about a medication that is used to support knowledge.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicationKnowledge(
  val administrationGuidelines: List<MedicationKnowledgeAdministrationGuidelines> = listOf(),
  /**
   * Amount of drug in package
   */
  val amount: Quantity? = null,
  val associatedMedication: List<Reference> = listOf(),
  /**
   * Code that identifies this medication
   */
  val code: CodeableConcept? = null,
  override val contained: List<Resource> = listOf(),
  val contraindication: List<Reference> = listOf(),
  val cost: List<MedicationKnowledgeCost> = listOf(),
  /**
   * powder | tablets | capsule +
   */
  val doseForm: CodeableConcept? = null,
  val drugCharacteristic: List<MedicationKnowledgeDrugCharacteristic> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val ingredient: List<MedicationKnowledgeIngredient> = listOf(),
  val intendedRoute: List<CodeableConcept> = listOf(),
  val kinetics: List<MedicationKnowledgeKinetics> = listOf(),
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Manufacturer of the item
   */
  val manufacturer: Reference? = null,
  val medicineClassification: List<MedicationKnowledgeMedicineClassification> = listOf(),
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val monitoringProgram: List<MedicationKnowledgeMonitoringProgram> = listOf(),
  val monograph: List<MedicationKnowledgeMonograph> = listOf(),
  /**
   * Details about packaged medications
   */
  val packaging: MedicationKnowledgePackaging? = null,
  /**
   * The instructions for preparing the medication
   */
  val preparationInstruction: String? = null,
  val productType: List<CodeableConcept> = listOf(),
  val regulatory: List<MedicationKnowledgeRegulatory> = listOf(),
  val relatedMedicationKnowledge: List<MedicationKnowledgeRelatedMedicationKnowledge> = listOf(),
  /**
   * active | inactive | entered-in-error
   */
  val status: String? = null,
  val synonym: List<String> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
