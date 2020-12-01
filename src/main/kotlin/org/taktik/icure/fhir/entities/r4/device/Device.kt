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
package org.taktik.icure.fhir.entities.r4.device

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
import org.taktik.icure.fhir.entities.r4.annotation.Annotation
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.contactpoint.ContactPoint
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Item used in healthcare
 *
 * A type of a manufactured item that is used in the provision of healthcare without being
 * substantially changed through that activity. The device may be a medical or non-medical device.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Device(
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
        val contact: List<ContactPoint> = listOf(),
        override val contained: List<Resource> = listOf(),
        /**
   * The reference to the definition for the device
   */
  val definition: Reference? = null,
        val deviceName: List<DeviceDeviceName> = listOf(),
        /**
   * The distinct identification string
   */
  val distinctIdentifier: String? = null,
        /**
   * Date and time of expiry of this device (if applicable)
   */
  val expirationDate: String? = null,
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
   * Where the device is found
   */
  val location: Reference? = null,
        /**
   * Lot number of manufacture
   */
  val lotNumber: String? = null,
        /**
   * Date when the device was made
   */
  val manufactureDate: String? = null,
        /**
   * Name of device manufacturer
   */
  val manufacturer: String? = null,
        /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
        /**
   * The model number for the device
   */
  val modelNumber: String? = null,
        override val modifierExtension: List<Extension> = listOf(),
        val note: List<Annotation> = listOf(),
        /**
   * Organization responsible for device
   */
  val owner: Reference? = null,
        /**
   * The parent device
   */
  val parent: Reference? = null,
        /**
   * The part number of the device
   */
  val partNumber: String? = null,
        /**
   * Patient to whom Device is affixed
   */
  val patient: Reference? = null,
        val property: List<DeviceProperty> = listOf(),
        val safety: List<CodeableConcept> = listOf(),
        /**
   * Serial number assigned by the manufacturer
   */
  val serialNumber: String? = null,
        val specialization: List<DeviceSpecialization> = listOf(),
        /**
   * active | inactive | entered-in-error | unknown
   */
  val status: String? = null,
        val statusReason: List<CodeableConcept> = listOf(),
        /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
        /**
   * The kind or type of device
   */
  val type: CodeableConcept? = null,
        val udiCarrier: List<DeviceUdiCarrier> = listOf(),
        /**
   * Network address to contact device
   */
  val url: String? = null,
        val version: List<DeviceVersion> = listOf()
) : StoredDocument, DomainResource {
  override fun withIdRev(id: String?, rev: String): Device = if (id != null) this.copy(id = id, rev
      = rev) else this.copy(rev = rev)

  override fun withDeletionDate(deletionDate: Long?): Device = this.copy(deletionDate =
      deletionDate)
}
