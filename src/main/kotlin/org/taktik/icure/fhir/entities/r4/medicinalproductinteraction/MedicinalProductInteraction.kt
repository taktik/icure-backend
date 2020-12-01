//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.medicinalproductinteraction

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * MedicinalProductInteraction
 *
 * The interactions of the medicinal product with other medicinal products, or other forms of
 * interactions.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductInteraction(
  override val contained: List<Resource> = listOf(),
  /**
   * The interaction described
   */
  val description: String? = null,
  /**
   * The effect of the interaction, for example "reduced gastric absorption of primary medication"
   */
  val effect: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * The incidence of the interaction, e.g. theoretical, observed
   */
  val incidence: CodeableConcept? = null,
  val interactant: List<MedicinalProductInteractionInteractant> = listOf(),
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Actions for managing the interaction
   */
  val management: CodeableConcept? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val subject: List<Reference> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * The type of the interaction e.g. drug-drug interaction, drug-food interaction, drug-lab test
   * interaction
   */
  val type: CodeableConcept? = null
) : DomainResource
