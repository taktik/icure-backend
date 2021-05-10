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
package org.taktik.icure.fhir.entities.r4.patient

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
import org.taktik.icure.fhir.entities.r4.address.Address
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.contactpoint.ContactPoint
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.humanname.HumanName
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Information about an individual or animal receiving health care services
 *
 * Demographics and other administrative information about an individual or animal receiving care or
 * other health-related services.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Patient(
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
   * Whether this patient's record is in active use
   */
  val active: Boolean? = null,
        val address: List<Address> = listOf(),
        /**
   * The date of birth for the individual
   */
  val birthDate: String? = null,
        val communication: List<PatientCommunication> = listOf(),
        val contact: List<PatientContact> = listOf(),
        override val contained: List<Resource> = listOf(),
        /**
   * Indicates if the individual is deceased or not
   */
  val deceasedBoolean: Boolean? = null,
        /**
   * Indicates if the individual is deceased or not
   */
  val deceasedDateTime: String? = null,
        override val extension: List<Extension> = listOf(),
        /**
   * male | female | other | unknown
   */
  val gender: String? = null,
        val generalPractitioner: List<Reference> = listOf(),
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
        val link: List<PatientLink> = listOf(),
        /**
   * Organization that is the custodian of the patient record
   */
  val managingOrganization: Reference? = null,
        /**
   * Marital (civil) status of a patient
   */
  val maritalStatus: CodeableConcept? = null,
        /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
        override val modifierExtension: List<Extension> = listOf(),
        /**
   * Whether patient is part of a multiple birth
   */
  val multipleBirthBoolean: Boolean? = null,
        /**
   * Whether patient is part of a multiple birth
   */
  val multipleBirthInteger: Int? = null,
        val name: List<HumanName> = listOf(),
        val photo: List<org.taktik.icure.fhir.entities.r4.attachment.Attachment> = listOf(),
        val telecom: List<ContactPoint> = listOf(),
        /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : StoredDocument, DomainResource {
  override fun withIdRev(id: String?, rev: String): Patient = if (id != null) this.copy(id = id, rev
      = rev) else this.copy(rev = rev)

  override fun withDeletionDate(deletionDate: Long?): Patient = this.copy(deletionDate =
      deletionDate)
}
