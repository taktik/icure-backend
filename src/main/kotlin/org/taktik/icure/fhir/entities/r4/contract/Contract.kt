//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.contract

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
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Legal Agreement
 *
 * Legally enforceable, formally recorded unilateral or bilateral directive i.e., a policy or
 * agreement.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Contract(
  val alias: List<String> = listOf(),
  /**
   * Effective time
   */
  val applies: Period? = null,
  /**
   * Source of Contract
   */
  val author: Reference? = null,
  val authority: List<Reference> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Contract precursor content
   */
  val contentDefinition: ContractContentDefinition? = null,
  /**
   * Content derived from the basal information
   */
  val contentDerivative: CodeableConcept? = null,
  val domain: List<Reference> = listOf(),
  /**
   * Contract cessation cause
   */
  val expirationType: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  val friendly: List<ContractFriendly> = listOf(),
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
   * Source Contract Definition
   */
  val instantiatesCanonical: Reference? = null,
  /**
   * External Contract Definition
   */
  val instantiatesUri: String? = null,
  /**
   * When this Contract was issued
   */
  val issued: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  val legal: List<ContractLegal> = listOf(),
  /**
   * Negotiation status
   */
  val legalState: CodeableConcept? = null,
  /**
   * Binding Contract
   */
  val legallyBindingAttachment: Attachment? = null,
  /**
   * Binding Contract
   */
  val legallyBindingReference: Reference? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Computer friendly designation
   */
  val name: String? = null,
  val relevantHistory: List<Reference> = listOf(),
  val rule: List<ContractRule> = listOf(),
  /**
   * Range of Legal Concerns
   */
  val scope: CodeableConcept? = null,
  val signer: List<ContractSigner> = listOf(),
  val site: List<Reference> = listOf(),
  /**
   * amended | appended | cancelled | disputed | entered-in-error | executable | executed |
   * negotiable | offered | policy | rejected | renewed | revoked | resolved | terminated
   */
  val status: String? = null,
  val subType: List<CodeableConcept> = listOf(),
  val subject: List<Reference> = listOf(),
  /**
   * Subordinate Friendly name
   */
  val subtitle: String? = null,
  val supportingInfo: List<Reference> = listOf(),
  val term: List<ContractTerm> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Human Friendly name
   */
  val title: String? = null,
  /**
   * Focus of contract interest
   */
  val topicCodeableConcept: CodeableConcept? = null,
  /**
   * Focus of contract interest
   */
  val topicReference: Reference? = null,
  /**
   * Legal instrument category
   */
  val type: CodeableConcept? = null,
  /**
   * Basal definition
   */
  val url: String? = null,
  /**
   * Business edition
   */
  val version: String? = null
) : DomainResource
