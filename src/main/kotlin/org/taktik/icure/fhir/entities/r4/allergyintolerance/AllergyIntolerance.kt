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
package org.taktik.icure.fhir.entities.r4.allergyintolerance

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
 * Allergy or Intolerance (generally: Risk of adverse reaction to a substance)
 *
 * Risk of harmful or undesirable, physiological response which is unique to an individual and
 * associated with exposure to a substance.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AllergyIntolerance(
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
   * Source of the information about the allergy
   */
  val asserter: Reference? = null,
        val category: List<String> = listOf(),
        /**
   * active | inactive | resolved
   */
  val clinicalStatus: CodeableConcept? = null,
        /**
   * Code that identifies the allergy or intolerance
   */
  val code: CodeableConcept? = null,
        override val contained: List<Resource> = listOf(),
        /**
   * low | high | unable-to-assess
   */
  val criticality: String? = null,
        /**
   * Encounter when the allergy or intolerance was asserted
   */
  val encounter: Reference? = null,
        override val extension: List<Extension> = listOf(),
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
   * Date(/time) of last known occurrence of a reaction
   */
  val lastOccurrence: String? = null,
        /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
        override val modifierExtension: List<Extension> = listOf(),
        val note: List<Annotation> = listOf(),
        /**
   * When allergy or intolerance was identified
   */
  val onsetAge: Age? = null,
        /**
   * When allergy or intolerance was identified
   */
  val onsetDateTime: String? = null,
        /**
   * When allergy or intolerance was identified
   */
  val onsetPeriod: Period? = null,
        /**
   * When allergy or intolerance was identified
   */
  val onsetRange: Range? = null,
        /**
   * When allergy or intolerance was identified
   */
  val onsetString: String? = null,
        /**
   * Who the sensitivity is for
   */
  val patient: Reference,
        val reaction: List<AllergyIntoleranceReaction> = listOf(),
        /**
   * Date first version of the resource instance was recorded
   */
  val recordedDate: String? = null,
        /**
   * Who recorded the sensitivity
   */
  val recorder: Reference? = null,
        /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
        /**
   * allergy | intolerance - Underlying mechanism (if known)
   */
  val type: String? = null,
        /**
   * unconfirmed | confirmed | refuted | entered-in-error
   */
  val verificationStatus: CodeableConcept? = null
) : StoredDocument, DomainResource {
  override fun withIdRev(id: String?, rev: String): AllergyIntolerance = if (id != null)
      this.copy(id = id, rev = rev) else this.copy(rev = rev)

  override fun withDeletionDate(deletionDate: Long?): AllergyIntolerance = this.copy(deletionDate =
      deletionDate)
}
