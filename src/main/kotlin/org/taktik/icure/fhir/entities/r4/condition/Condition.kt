//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.condition

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
 * Detailed information about conditions, problems or diagnoses
 *
 * A clinical condition, problem, diagnosis, or other event, situation, issue, or clinical concept
 * that has risen to a level of concern.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Condition(
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
) : StoredDocument, DomainResource {
  override fun withIdRev(id: String?, rev: String): Condition = if (id != null) this.copy(id = id,
      rev = rev) else this.copy(rev = rev)

  override fun withDeletionDate(deletionDate: Long?): Condition = this.copy(deletionDate =
      deletionDate)
}
