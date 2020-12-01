//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.specimendefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * The specimen's container
 *
 * The specimen's container.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SpecimenDefinitionTypeTestedContainer(
  val additive: List<SpecimenDefinitionTypeTestedContainerAdditive> = listOf(),
  /**
   * Color of container cap
   */
  val cap: CodeableConcept? = null,
  /**
   * Container capacity
   */
  val capacity: Quantity? = null,
  /**
   * Container description
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Container material
   */
  val material: CodeableConcept? = null,
  /**
   * Minimum volume
   */
  val minimumVolumeQuantity: Quantity? = null,
  /**
   * Minimum volume
   */
  val minimumVolumeString: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Specimen container preparation
   */
  val preparation: String? = null,
  /**
   * Kind of container associated with the kind of specimen
   */
  val type: CodeableConcept? = null
) : BackboneElement
