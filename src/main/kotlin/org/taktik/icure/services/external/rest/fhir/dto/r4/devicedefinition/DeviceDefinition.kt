//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.devicedefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.annotation.Annotation
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.contactpoint.ContactPoint
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.prodcharacteristic.ProdCharacteristic
import org.taktik.icure.services.external.rest.fhir.dto.r4.productshelflife.ProductShelfLife
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * An instance of a medical-related component of a medical device
 *
 * The characteristics, operational status and capabilities of a medical-related component of a
 * medical device.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DeviceDefinition(
  val capability: List<DeviceDefinitionCapability> = listOf(),
  val contact: List<ContactPoint> = listOf(),
  override val contained: List<Resource> = listOf(),
  val deviceName: List<DeviceDefinitionDeviceName> = listOf(),
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
  val languageCode: List<CodeableConcept> = listOf(),
  /**
   * Name of device manufacturer
   */
  val manufacturerReference: Reference? = null,
  /**
   * Name of device manufacturer
   */
  val manufacturerString: String? = null,
  val material: List<DeviceDefinitionMaterial> = listOf(),
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
   * Access to on-line information
   */
  val onlineInformation: String? = null,
  /**
   * Organization responsible for device
   */
  val owner: Reference? = null,
  /**
   * The parent device it can be part of
   */
  val parentDevice: Reference? = null,
  /**
   * Dimensions, color etc.
   */
  val physicalCharacteristics: ProdCharacteristic? = null,
  val property: List<DeviceDefinitionProperty> = listOf(),
  /**
   * The quantity of the device present in the packaging (e.g. the number of devices present in a
   * pack, or the number of devices in the same package of the medicinal product)
   */
  val quantity: Quantity? = null,
  val safety: List<CodeableConcept> = listOf(),
  val shelfLifeStorage: List<ProductShelfLife> = listOf(),
  val specialization: List<DeviceDefinitionSpecialization> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * What kind of device or device system this is
   */
  val type: CodeableConcept? = null,
  val udiDeviceIdentifier: List<DeviceDefinitionUdiDeviceIdentifier> = listOf(),
  /**
   * Network address to contact device
   */
  val url: String? = null,
  val version: List<String> = listOf()
) : DomainResource
