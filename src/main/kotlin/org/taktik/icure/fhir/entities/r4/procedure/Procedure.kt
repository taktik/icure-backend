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
package org.taktik.icure.fhir.entities.r4.procedure

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.ektorp.Attachment
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.age.Age
import org.taktik.icure.fhir.entities.r4.annotation.Annotation
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.range.Range
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * An action that is being or was performed on a patient
 *
 * An action that is or was performed on or for a patient. This can be a physical intervention like
 * an operation, or less invasive like long term services, counseling, or hypnotherapy.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Procedure(
        @JsonProperty("_rev")
  override val rev: String? = null,
        @JsonProperty("deleted")
  override val deletionDate: Long? = null,
        @JsonProperty("_attachments")
  override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info")
  override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts")
  override val conflicts: List<String>? = null,
        @JsonProperty("rev_history")
  override val revHistory: Map<String, String>? = null,
        /**
   * Person who asserts this procedure
   */
  val asserter: Reference? = null,
        val basedOn: List<Reference> = listOf(),
        val bodySite: List<CodeableConcept> = listOf(),
        /**
   * Classification of the procedure
   */
  val category: CodeableConcept? = null,
        /**
   * Identification of the procedure
   */
  val code: CodeableConcept? = null,
        val complication: List<CodeableConcept> = listOf(),
        val complicationDetail: List<Reference> = listOf(),
        override val contained: List<Resource> = listOf(),
        /**
   * Encounter created as part of
   */
  val encounter: Reference? = null,
        override val extension: List<Extension> = listOf(),
        val focalDevice: List<ProcedureFocalDevice> = listOf(),
        val followUp: List<CodeableConcept> = listOf(),
        /**
   * Logical id of this artifact
   */
  @JsonProperty("_id")
  override val id: String,
        val identifier: List<Identifier> = listOf(),
        /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
        val instantiatesCanonical: List<String> = listOf(),
        val instantiatesUri: List<String> = listOf(),
        /**
   * Language of the resource content
   */
  override val language: String? = null,
        /**
   * Where the procedure happened
   */
  val location: Reference? = null,
        /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
        override val modifierExtension: List<Extension> = listOf(),
        val note: List<Annotation> = listOf(),
        /**
   * The result of procedure
   */
  val outcome: CodeableConcept? = null,
        val partOf: List<Reference> = listOf(),
        /**
   * When the procedure was performed
   */
  val performedAge: Age? = null,
        /**
   * When the procedure was performed
   */
  val performedDateTime: String? = null,
        /**
   * When the procedure was performed
   */
  val performedPeriod: Period? = null,
        /**
   * When the procedure was performed
   */
  val performedRange: Range? = null,
        /**
   * When the procedure was performed
   */
  val performedString: String? = null,
        val performer: List<ProcedurePerformer> = listOf(),
        val reasonCode: List<CodeableConcept> = listOf(),
        val reasonReference: List<Reference> = listOf(),
        /**
   * Who recorded the procedure
   */
  val recorder: Reference? = null,
        val report: List<Reference> = listOf(),
        /**
   * preparation | in-progress | not-done | on-hold | stopped | completed | entered-in-error |
   * unknown
   */
  val status: String? = null,
        /**
   * Reason for current status
   */
  val statusReason: CodeableConcept? = null,
        /**
   * Who the procedure was performed on
   */
  val subject: Reference,
        /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
        val usedCode: List<CodeableConcept> = listOf(),
        val usedReference: List<Reference> = listOf()
) : StoredDocument, DomainResource {
  override fun withIdRev(id: String?, rev: String): Procedure = if (id != null) this.copy(id = id,
      rev = rev) else this.copy(rev = rev)

  override fun withDeletionDate(deletionDate: Long?): Procedure = this.copy(deletionDate =
      deletionDate)
}
