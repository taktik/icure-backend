//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.patient

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.address.Address
import org.taktik.icure.services.external.rest.fhir.dto.r4.attachment.Attachment
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.contactpoint.ContactPoint
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.humanname.HumanName
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

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
  val photo: List<Attachment> = listOf(),
  val telecom: List<ContactPoint> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
