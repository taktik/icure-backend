//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.explanationofbenefit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Pertinent diagnosis information
 *
 * Information about diagnoses relevant to the claim items.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ExplanationOfBenefitDiagnosis(
  /**
   * Nature of illness or problem
   */
  val diagnosisCodeableConcept: CodeableConcept,
  /**
   * Nature of illness or problem
   */
  val diagnosisReference: Reference,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Present on admission
   */
  val onAdmission: CodeableConcept? = null,
  /**
   * Package billing code
   */
  val packageCode: CodeableConcept? = null,
  /**
   * Diagnosis instance identifier
   */
  val sequence: Int? = null,
  val type: List<CodeableConcept> = listOf()
) : BackboneElement
