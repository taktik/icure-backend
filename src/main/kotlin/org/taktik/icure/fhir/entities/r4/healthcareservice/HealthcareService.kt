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
package org.taktik.icure.fhir.entities.r4.healthcareservice

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
import org.taktik.icure.fhir.entities.r4.contactpoint.ContactPoint
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * The details of a healthcare service available at a location
 *
 * The details of a healthcare service available at a location.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class HealthcareService(
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
   * Whether this HealthcareService record is in active use
   */
  val active: Boolean? = null,
        /**
   * If an appointment is required for access to this service
   */
  val appointmentRequired: Boolean? = null,
        /**
   * Description of availability exceptions
   */
  val availabilityExceptions: String? = null,
        val availableTime: List<HealthcareServiceAvailableTime> = listOf(),
        val category: List<CodeableConcept> = listOf(),
        val characteristic: List<CodeableConcept> = listOf(),
        /**
   * Additional description and/or any specific issues not covered elsewhere
   */
  val comment: String? = null,
        val communication: List<CodeableConcept> = listOf(),
        override val contained: List<Resource> = listOf(),
        val coverageArea: List<Reference> = listOf(),
        val eligibility: List<HealthcareServiceEligibility> = listOf(),
        val endpoint: List<Reference> = listOf(),
        override val extension: List<Extension> = listOf(),
        /**
   * Extra details about the service that can't be placed in the other fields
   */
  val extraDetails: String? = null,
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
        val location: List<Reference> = listOf(),
        /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
        override val modifierExtension: List<Extension> = listOf(),
        /**
   * Description of service as presented to a consumer while searching
   */
  val name: String? = null,
        val notAvailable: List<HealthcareServiceNotAvailable> = listOf(),
        /**
   * Facilitates quick identification of the service
   */
  val photo: org.taktik.icure.fhir.entities.r4.attachment.Attachment? = null,
        val program: List<CodeableConcept> = listOf(),
        /**
   * Organization that provides this service
   */
  val providedBy: Reference? = null,
        val referralMethod: List<CodeableConcept> = listOf(),
        val serviceProvisionCode: List<CodeableConcept> = listOf(),
        val specialty: List<CodeableConcept> = listOf(),
        val telecom: List<ContactPoint> = listOf(),
        /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
        val type: List<CodeableConcept> = listOf()
) : StoredDocument, DomainResource {
  override fun withIdRev(id: String?, rev: String): HealthcareService = if (id != null) this.copy(id
      = id, rev = rev) else this.copy(rev = rev)

  override fun withDeletionDate(deletionDate: Long?): HealthcareService = this.copy(deletionDate =
      deletionDate)
}
