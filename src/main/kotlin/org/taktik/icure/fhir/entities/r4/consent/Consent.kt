//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.consent

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.attachment.Attachment
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * A healthcare consumer's  choices to permit or deny recipients or roles to perform actions for
 * specific purposes and periods of time
 *
 * A record of a healthcare consumer’s  choices, which permits or denies identified recipient(s) or
 * recipient role(s) to perform one or more actions within a given policy context, for specific
 * purposes and periods of time.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Consent(
  val category: List<CodeableConcept> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * When this Consent was created or indexed
   */
  val dateTime: String? = null,
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
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val organization: List<Reference> = listOf(),
  /**
   * Who the consent applies to
   */
  val patient: Reference? = null,
  val performer: List<Reference> = listOf(),
  val policy: List<ConsentPolicy> = listOf(),
  /**
   * Regulation that this consents to
   */
  val policyRule: CodeableConcept? = null,
  /**
   * Constraints to the base Consent.policyRule
   */
  val provision: ConsentProvision? = null,
  /**
   * Which of the four areas this resource covers (extensible)
   */
  val scope: CodeableConcept,
  /**
   * Source from which this consent is taken
   */
  val sourceAttachment: Attachment? = null,
  /**
   * Source from which this consent is taken
   */
  val sourceReference: Reference? = null,
  /**
   * draft | proposed | active | rejected | inactive | entered-in-error
   */
  val status: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  val verification: List<ConsentVerification> = listOf()
) : DomainResource
