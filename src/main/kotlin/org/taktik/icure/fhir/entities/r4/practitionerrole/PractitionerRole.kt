//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.practitionerrole

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.contactpoint.ContactPoint
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Roles/organizations the practitioner is associated with
 *
 * A specific set of Roles/Locations/specialties/services that a practitioner may perform at an
 * organization for a period of time.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PractitionerRole(
  /**
   * Whether this practitioner role record is in active use
   */
  val active: Boolean? = null,
  /**
   * Description of availability exceptions
   */
  val availabilityExceptions: String? = null,
  val availableTime: List<PractitionerRoleAvailableTime> = listOf(),
  val code: List<CodeableConcept> = listOf(),
  override val contained: List<Resource> = listOf(),
  val endpoint: List<Reference> = listOf(),
  override val extension: List<Extension> = listOf(),
  val healthcareService: List<Reference> = listOf(),
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
  val location: List<Reference> = listOf(),
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val notAvailable: List<PractitionerRoleNotAvailable> = listOf(),
  /**
   * Organization where the roles are available
   */
  val organization: Reference? = null,
  /**
   * The period during which the practitioner is authorized to perform in these role(s)
   */
  val period: Period? = null,
  /**
   * Practitioner that is able to provide the defined services for the organization
   */
  val practitioner: Reference? = null,
  val specialty: List<CodeableConcept> = listOf(),
  val telecom: List<ContactPoint> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
