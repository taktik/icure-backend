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
package org.taktik.icure.services.external.rest.fhir.dto.r4.adverseevent

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Medical care, research study or other healthcare event causing physical injury
 *
 * Actual or  potential/avoided event causing unintended physical injury resulting from or
 * contributed to by medical care, a research study or other healthcare setting factors that requires
 * additional monitoring, treatment, or hospitalization, or that results in death.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AdverseEvent(
  /**
   * actual | potential
   */
  val actuality: String? = null,
  val category: List<CodeableConcept> = listOf(),
  override val contained: List<Resource> = listOf(),
  val contributor: List<Reference> = listOf(),
  /**
   * When the event occurred
   */
  val date: String? = null,
  /**
   * When the event was detected
   */
  val detected: String? = null,
  /**
   * Encounter created as part of
   */
  val encounter: Reference? = null,
  /**
   * Type of the event itself in relation to the subject
   */
  val event: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * Business identifier for the event
   */
  val identifier: Identifier? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Location where adverse event occurred
   */
  val location: Reference? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * resolved | recovering | ongoing | resolvedWithSequelae | fatal | unknown
   */
  val outcome: CodeableConcept? = null,
  /**
   * When the event was recorded
   */
  val recordedDate: String? = null,
  /**
   * Who recorded the adverse event
   */
  val recorder: Reference? = null,
  val referenceDocument: List<Reference> = listOf(),
  val resultingCondition: List<Reference> = listOf(),
  /**
   * Seriousness of the event
   */
  val seriousness: CodeableConcept? = null,
  /**
   * mild | moderate | severe
   */
  val severity: CodeableConcept? = null,
  val study: List<Reference> = listOf(),
  /**
   * Subject impacted by event
   */
  val subject: Reference,
  val subjectMedicalHistory: List<Reference> = listOf(),
  val suspectEntity: List<AdverseEventSuspectEntity> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
