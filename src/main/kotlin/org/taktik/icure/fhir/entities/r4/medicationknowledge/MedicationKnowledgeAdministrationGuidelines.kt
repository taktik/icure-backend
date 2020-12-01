//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.medicationknowledge

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Guidelines for administration of the medication
 *
 * Guidelines for the administration of the medication.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicationKnowledgeAdministrationGuidelines(
  val dosage: List<MedicationKnowledgeAdministrationGuidelinesDosage> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Indication for use that apply to the specific administration guidelines
   */
  val indicationCodeableConcept: CodeableConcept? = null,
  /**
   * Indication for use that apply to the specific administration guidelines
   */
  val indicationReference: Reference? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val patientCharacteristics:
      List<MedicationKnowledgeAdministrationGuidelinesPatientCharacteristics> = listOf()
) : BackboneElement
