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
package org.taktik.icure.services.external.rest.fhir.dto.r4.condition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.age.Age
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Detailed information about conditions, problems or diagnoses
 *
 * A clinical condition, problem, diagnosis, or other event, situation, issue, or clinical concept
 * that has risen to a level of concern.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Condition(
  /**
   * When in resolution/remission
   */
  val abatementAge: Age? = null,
  /**
   * When in resolution/remission
   */
  val abatementDateTime: String? = null,
  /**
   * When in resolution/remission
   */
  val abatementPeriod: Period? = null,
  /**
   * When in resolution/remission
   */
  val abatementRange: Range? = null,
  /**
   * When in resolution/remission
   */
  val abatementString: String? = null,
  /**
   * Person who asserts this condition
   */
  val asserter: Reference? = null,
  val bodySite: List<CodeableConcept> = listOf(),
  val category: List<CodeableConcept> = listOf(),
  /**
   * active | recurrence | relapse | inactive | remission | resolved
   */
  val clinicalStatus: CodeableConcept? = null,
  /**
   * Identification of the condition, problem or diagnosis
   */
  val code: CodeableConcept? = null,
  override val contained: List<Resource> = listOf(),
  /**
   * Encounter created as part of
   */
  val encounter: Reference? = null,
  val evidence: List<ConditionEvidence> = listOf(),
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
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  /**
   * Estimated or actual date,  date-time, or age
   */
  val onsetAge: Age? = null,
  /**
   * Estimated or actual date,  date-time, or age
   */
  val onsetDateTime: String? = null,
  /**
   * Estimated or actual date,  date-time, or age
   */
  val onsetPeriod: Period? = null,
  /**
   * Estimated or actual date,  date-time, or age
   */
  val onsetRange: Range? = null,
  /**
   * Estimated or actual date,  date-time, or age
   */
  val onsetString: String? = null,
  /**
   * Date record was first recorded
   */
  val recordedDate: String? = null,
  /**
   * Who recorded the condition
   */
  val recorder: Reference? = null,
  /**
   * Subjective severity of condition
   */
  val severity: CodeableConcept? = null,
  val stage: List<ConditionStage> = listOf(),
  /**
   * Who has the condition?
   */
  val subject: Reference,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * unconfirmed | provisional | differential | confirmed | refuted | entered-in-error
   */
  val verificationStatus: CodeableConcept? = null
) : DomainResource
