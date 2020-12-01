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
