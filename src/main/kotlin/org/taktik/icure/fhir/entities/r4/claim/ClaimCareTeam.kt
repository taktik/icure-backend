//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.claim

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Members of the care team
 *
 * The members of the team who provided the products and services.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ClaimCareTeam(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Practitioner or organization
   */
  val provider: Reference,
  /**
   * Practitioner credential or specialization
   */
  val qualification: CodeableConcept? = null,
  /**
   * Indicator of the lead practitioner
   */
  val responsible: Boolean? = null,
  /**
   * Function within the team
   */
  val role: CodeableConcept? = null,
  /**
   * Order of care team
   */
  val sequence: Int? = null
) : BackboneElement
