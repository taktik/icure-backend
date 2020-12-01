//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.terminologycapabilities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.contactdetail.ContactDetail
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.usagecontext.UsageContext

/**
 * A statement of system capabilities
 *
 * A TerminologyCapabilities resource documents a set of capabilities (behaviors) of a FHIR
 * Terminology Server that may be used as a statement of actual server functionality or a statement of
 * required or desired server implementation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TerminologyCapabilities(
  /**
   * Information about the [ConceptMap/$closure](conceptmap-operation-closure.html) operation
   */
  val closure: TerminologyCapabilitiesClosure? = null,
  /**
   * explicit | all
   */
  val codeSearch: String? = null,
  val codeSystem: List<TerminologyCapabilitiesCodeSystem> = listOf(),
  val contact: List<ContactDetail> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Use and/or publishing restrictions
   */
  val copyright: String? = null,
  /**
   * Date last changed
   */
  val date: String? = null,
  /**
   * Natural language description of the terminology capabilities
   */
  val description: String? = null,
  /**
   * Information about the [ValueSet/$expand](valueset-operation-expand.html) operation
   */
  val expansion: TerminologyCapabilitiesExpansion? = null,
  /**
   * For testing purposes, not real usage
   */
  val experimental: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * If this describes a specific instance
   */
  val implementation: TerminologyCapabilitiesImplementation? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val jurisdiction: List<CodeableConcept> = listOf(),
  /**
   * instance | capability | requirements
   */
  val kind: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Whether lockedDate is supported
   */
  val lockedDate: Boolean? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name for this terminology capabilities (computer friendly)
   */
  val name: String? = null,
  /**
   * Name of the publisher (organization or individual)
   */
  val publisher: String? = null,
  /**
   * Why this terminology capabilities is defined
   */
  val purpose: String? = null,
  /**
   * Software that is covered by this terminology capability statement
   */
  val software: TerminologyCapabilitiesSoftware? = null,
  /**
   * draft | active | retired | unknown
   */
  val status: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * Name for this terminology capabilities (human friendly)
   */
  val title: String? = null,
  /**
   * Information about the [ConceptMap/$translate](conceptmap-operation-translate.html) operation
   */
  val translation: TerminologyCapabilitiesTranslation? = null,
  /**
   * Canonical identifier for this terminology capabilities, represented as a URI (globally unique)
   */
  val url: String? = null,
  val useContext: List<UsageContext> = listOf(),
  /**
   * Information about the [ValueSet/$validate-code](valueset-operation-validate-code.html)
   * operation
   */
  val validateCode: TerminologyCapabilitiesValidateCode? = null,
  /**
   * Business version of the terminology capabilities
   */
  val version: String? = null
) : DomainResource
