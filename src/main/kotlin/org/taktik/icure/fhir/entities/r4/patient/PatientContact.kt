//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.patient

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.address.Address
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.contactpoint.ContactPoint
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.humanname.HumanName
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * A contact party (e.g. guardian, partner, friend) for the patient
 *
 * A contact party (e.g. guardian, partner, friend) for the patient.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PatientContact(
  /**
   * Address for the contact person
   */
  val address: Address? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * male | female | other | unknown
   */
  val gender: String? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * A name associated with the contact person
   */
  val name: HumanName? = null,
  /**
   * Organization that is associated with the contact
   */
  val organization: Reference? = null,
  /**
   * The period during which this contact person or organization is valid to be contacted relating
   * to this patient
   */
  val period: Period? = null,
  val relationship: List<CodeableConcept> = listOf(),
  val telecom: List<ContactPoint> = listOf()
) : BackboneElement
