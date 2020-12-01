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
package org.taktik.icure.services.external.rest.fhir.dto.r4.verificationresult

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference
import org.taktik.icure.services.external.rest.fhir.dto.r4.timing.Timing

/**
 * Describes validation requirements, source(s), status and dates for one or more elements
 *
 * Describes validation requirements, source(s), status and dates for one or more elements.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class VerificationResult(
  /**
   * Information about the entity attesting to information
   */
  val attestation: VerificationResultAttestation? = null,
  override val contained: List<Resource> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * fatal | warn | rec-only | none
   */
  val failureAction: CodeableConcept? = null,
  /**
   * Frequency of revalidation
   */
  val frequency: Timing? = null,
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * The date/time validation was last completed (including failed validations)
   */
  val lastPerformed: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * none | initial | periodic
   */
  val need: CodeableConcept? = null,
  /**
   * The date when target is next validated, if appropriate
   */
  val nextScheduled: String? = null,
  val primarySource: List<VerificationResultPrimarySource> = listOf(),
  /**
   * attested | validated | in-process | req-revalid | val-fail | reval-fail
   */
  val status: String? = null,
  /**
   * When the validation status was updated
   */
  val statusDate: String? = null,
  val target: List<Reference> = listOf(),
  val targetLocation: List<String> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  val validationProcess: List<CodeableConcept> = listOf(),
  /**
   * nothing | primary | multiple
   */
  val validationType: CodeableConcept? = null,
  val validator: List<VerificationResultValidator> = listOf()
) : DomainResource
