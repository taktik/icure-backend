//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.group

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Group of multiple entities
 *
 * Represents a defined collection of entities that may be discussed or acted upon collectively but
 * which are not expected to act collectively, and are not formally or legally recognized; i.e. a
 * collection of entities that isn't an Organization.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Group(
  /**
   * Whether this group's record is in active use
   */
  val active: Boolean? = null,
  /**
   * Descriptive or actual
   */
  val actual: Boolean? = null,
  val characteristic: List<GroupCharacteristic> = listOf(),
  /**
   * Kind of Group members
   */
  val code: CodeableConcept? = null,
  override val contained: List<Resource> = listOf(),
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
   * Entity that is the custodian of the Group's definition
   */
  val managingEntity: Reference? = null,
  val member: List<GroupMember> = listOf(),
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Label for Group
   */
  val name: String? = null,
  /**
   * Number of members
   */
  val quantity: Int? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * person | animal | practitioner | device | medication | substance
   */
  val type: String? = null
) : DomainResource
