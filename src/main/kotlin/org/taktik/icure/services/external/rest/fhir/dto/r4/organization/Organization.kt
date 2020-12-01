//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.organization

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.address.Address
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.contactpoint.ContactPoint
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * A grouping of people or organizations with a common purpose
 *
 * A formally or informally recognized grouping of people or organizations formed for the purpose of
 * achieving some form of collective action.  Includes companies, institutions, corporations,
 * departments, community groups, healthcare practice groups, payer/insurer, etc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Organization(
  /**
   * Whether the organization's record is still in active use
   */
  val active: Boolean? = null,
  val address: List<Address> = listOf(),
  val alias: List<String> = listOf(),
  val contact: List<OrganizationContact> = listOf(),
  override val contained: List<Resource> = listOf(),
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
   * Name used for the organization
   */
  val name: String? = null,
  /**
   * The organization of which this organization forms a part
   */
  val partOf: Reference? = null,
  val telecom: List<ContactPoint> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  val type: List<CodeableConcept> = listOf()
) : DomainResource
