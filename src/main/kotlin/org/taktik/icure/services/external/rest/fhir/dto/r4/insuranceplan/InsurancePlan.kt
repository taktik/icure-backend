//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.insuranceplan

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
 * Details of a Health Insurance product/plan provided by an organization
 *
 * Details of a Health Insurance product/plan provided by an organization.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class InsurancePlan(
  /**
   * Product administrator
   */
  val administeredBy: Reference? = null,
  val alias: List<String> = listOf(),
  val contact: List<InsurancePlanContact> = listOf(),
  override val contained: List<Resource> = listOf(),
  val coverage: List<InsurancePlanCoverage> = listOf(),
  val coverageArea: List<Reference> = listOf(),
  val endpoint: List<Reference> = listOf(),
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
  /**
   * Official name
   */
  val name: String? = null,
  val network: List<Reference> = listOf(),
  /**
   * Plan issuer
   */
  val ownedBy: Reference? = null,
  /**
   * When the product is available
   */
  val period: Period? = null,
  val plan: List<InsurancePlanPlan> = listOf(),
  /**
   * draft | active | retired | unknown
   */
  val status: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  val type: List<CodeableConcept> = listOf()
) : DomainResource
