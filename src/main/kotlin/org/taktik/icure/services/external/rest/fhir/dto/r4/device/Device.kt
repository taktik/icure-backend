//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.device

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.contactpoint.ContactPoint
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

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
) : DomainResource
