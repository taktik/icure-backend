//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.encounter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Details about the admission to a healthcare service
 *
 * Details about the admission to a healthcare service.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class EncounterHospitalization(
  /**
   * From where patient was admitted (physician referral, transfer)
   */
  val admitSource: CodeableConcept? = null,
  /**
   * Location/organization to which the patient is discharged
   */
  val destination: Reference? = null,
  val dietPreference: List<CodeableConcept> = listOf(),
  /**
   * Category or kind of location after discharge
   */
  val dischargeDisposition: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The location/organization from which the patient came before admission
   */
  val origin: Reference? = null,
  /**
   * Pre-admission identifier
   */
  val preAdmissionIdentifier: Identifier? = null,
  /**
   * The type of hospital re-admission that has occurred (if any). If the value is absent, then this
   * is not identified as a readmission
   */
  val reAdmission: CodeableConcept? = null,
  val specialArrangement: List<CodeableConcept> = listOf(),
  val specialCourtesy: List<CodeableConcept> = listOf()
) : BackboneElement
