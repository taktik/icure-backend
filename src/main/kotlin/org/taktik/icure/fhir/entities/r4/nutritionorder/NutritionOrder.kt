//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.nutritionorder

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.annotation.Annotation
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Diet, formula or nutritional supplement request
 *
 * A request to supply a diet, formula feeding (enteral) or oral nutritional supplement to a
 * patient/resident.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class NutritionOrder(
  val allergyIntolerance: List<Reference> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Date and time the nutrition order was requested
   */
  val dateTime: String? = null,
  /**
   * The encounter associated with this nutrition order
   */
  val encounter: Reference? = null,
  /**
   * Enteral formula components
   */
  val enteralFormula: NutritionOrderEnteralFormula? = null,
  val excludeFoodModifier: List<CodeableConcept> = listOf(),
  override val extension: List<Extension> = listOf(),
  val foodPreferenceModifier: List<CodeableConcept> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val instantiates: List<String> = listOf(),
  val instantiatesCanonical: List<String> = listOf(),
  val instantiatesUri: List<String> = listOf(),
  /**
   * proposal | plan | directive | order | original-order | reflex-order | filler-order |
   * instance-order | option
   */
  val intent: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val note: List<Annotation> = listOf(),
  /**
   * Oral diet components
   */
  val oralDiet: NutritionOrderOralDiet? = null,
  /**
   * Who ordered the diet, formula or nutritional supplement
   */
  val orderer: Reference? = null,
  /**
   * The person who requires the diet, formula or nutritional supplement
   */
  val patient: Reference,
  /**
   * draft | active | on-hold | revoked | completed | entered-in-error | unknown
   */
  val status: String? = null,
  val supplement: List<NutritionOrderSupplement> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
