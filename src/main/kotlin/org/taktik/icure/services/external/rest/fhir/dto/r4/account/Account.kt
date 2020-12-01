//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.account

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Tracks balance, charges, for patient or cost center
 *
 * A financial tool for tracking value accrued for a particular purpose.  In the healthcare field,
 * used to track charges for a patient, cost centers, etc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Account(
  override val contained: List<Resource> = listOf(),
  val coverage: List<AccountCoverage> = listOf(),
  /**
   * Explanation of purpose/use
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  val guarantor: List<AccountGuarantor> = listOf(),
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
  /**
   * Human-readable label
   */
  val name: String? = null,
  /**
   * Entity managing the Account
   */
  val owner: Reference? = null,
  /**
   * Reference to a parent Account
   */
  val partOf: Reference? = null,
  /**
   * Transaction window
   */
  val servicePeriod: Period? = null,
  /**
   * active | inactive | entered-in-error | on-hold | unknown
   */
  val status: String? = null,
  val subject: List<Reference> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * E.g. patient, expense, depreciation
   */
  val type: CodeableConcept? = null
) : DomainResource
