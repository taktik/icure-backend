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
package org.taktik.icure.fhir.entities.r4.encounter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.couchdb.entity.Attachment
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.duration.Duration
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * An interaction during which services are provided to the patient
 *
 * An interaction between a patient and healthcare provider(s) for the purpose of providing
 * healthcare service(s) or assessing the health status of a patient.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Encounter(
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
        val account: List<Reference> = listOf(),
        val appointment: List<Reference> = listOf(),
        val basedOn: List<Reference> = listOf(),
        val classHistory: List<EncounterClassHistory> = listOf(),
        /**
   * Classification of patient encounter
   */
  @JsonProperty("class")
  val class_fhir: Coding,
        override val contained: List<Resource> = listOf(),
        val diagnosis: List<EncounterDiagnosis> = listOf(),
        val episodeOfCare: List<Reference> = listOf(),
        override val extension: List<Extension> = listOf(),
        /**
   * Details about the admission to a healthcare service
   */
  val hospitalization: EncounterHospitalization? = null,
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
        /**
   * Language of the resource content
   */
  override val language: String? = null,
        /**
   * Quantity of time the encounter lasted (less time absent)
   */
  val length: Duration? = null,
        val location: List<EncounterLocation> = listOf(),
        /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
        override val modifierExtension: List<Extension> = listOf(),
        /**
   * Another Encounter this encounter is part of
   */
  val partOf: Reference? = null,
        val participant: List<EncounterParticipant> = listOf(),
        /**
   * The start and end time of the encounter
   */
  val period: Period? = null,
        /**
   * Indicates the urgency of the encounter
   */
  val priority: CodeableConcept? = null,
        val reasonCode: List<CodeableConcept> = listOf(),
        val reasonReference: List<Reference> = listOf(),
        /**
   * The organization (facility) responsible for this encounter
   */
  val serviceProvider: Reference? = null,
        /**
   * Specific type of service
   */
  val serviceType: CodeableConcept? = null,
        /**
   * planned | arrived | triaged | in-progress | onleave | finished | cancelled +
   */
  val status: String? = null,
        val statusHistory: List<EncounterStatusHistory> = listOf(),
        /**
   * The patient or group present at the encounter
   */
  val subject: Reference? = null,
        /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
        val type: List<CodeableConcept> = listOf()
) : StoredDocument, DomainResource {
  override fun withIdRev(id: String?, rev: String): Encounter = if (id != null) this.copy(id = id,
      rev = rev) else this.copy(rev = rev)

  override fun withDeletionDate(deletionDate: Long?): Encounter = this.copy(deletionDate =
      deletionDate)
}
