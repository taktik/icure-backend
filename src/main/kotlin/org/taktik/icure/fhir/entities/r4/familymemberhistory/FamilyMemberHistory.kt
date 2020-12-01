//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.familymemberhistory

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
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
 * Information about patient's relatives, relevant for patient
 *
 * Significant health conditions for a person related to the patient relevant in the context of care
 * for the patient.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class FamilyMemberHistory(
  /**
   * (approximate) age
   */
  val ageAge: Age? = null,
  /**
   * (approximate) age
   */
  val ageRange: Range? = null,
  /**
   * (approximate) age
   */
  val ageString: String? = null,
  /**
   * (approximate) date of birth
   */
  val bornDate: String? = null,
  /**
   * (approximate) date of birth
   */
  val bornPeriod: Period? = null,
  /**
   * (approximate) date of birth
   */
  val bornString: String? = null,
  val condition: List<FamilyMemberHistoryCondition> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * subject-unknown | withheld | unable-to-obtain | deferred
   */
  val dataAbsentReason: CodeableConcept? = null,
  /**
   * When history was recorded or last updated
   */
  val date: String? = null,
  /**
   * Dead? How old/when?
   */
  val deceasedAge: Age? = null,
  /**
   * Dead? How old/when?
   */
  val deceasedBoolean: Boolean? = null,
  /**
   * Dead? How old/when?
   */
  val deceasedDate: String? = null,
  /**
   * Dead? How old/when?
   */
  val deceasedRange: Range? = null,
  /**
   * Dead? How old/when?
   */
  val deceasedString: String? = null,
  /**
   * Age is estimated?
   */
  val estimatedAge: Boolean? = null,
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
  val instantiatesCanonical: List<String> = listOf(),
  val instantiatesUri: List<String> = listOf(),
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The family member described
   */
  val name: String? = null,
  val note: List<Annotation> = listOf(),
  /**
   * Patient history is about
   */
  val patient: Reference,
  val reasonCode: List<CodeableConcept> = listOf(),
  val reasonReference: List<Reference> = listOf(),
  /**
   * Relationship to the subject
   */
  val relationship: CodeableConcept,
  /**
   * male | female | other | unknown
   */
  val sex: CodeableConcept? = null,
  /**
   * partial | completed | entered-in-error | health-unknown
   */
  val status: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
