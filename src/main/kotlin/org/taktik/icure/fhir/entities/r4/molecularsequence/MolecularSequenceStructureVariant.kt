//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.molecularsequence

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Structural variant
 *
 * Information about chromosome structure variation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MolecularSequenceStructureVariant(
  /**
   * Does the structural variant have base pair resolution breakpoints?
   */
  val exact: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Structural variant inner
   */
  val inner: MolecularSequenceStructureVariantInner? = null,
  /**
   * Structural variant length
   */
  val length: Int? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Structural variant outer
   */
  val outer: MolecularSequenceStructureVariantOuter? = null,
  /**
   * Structural variant change type
   */
  val variantType: CodeableConcept? = null
) : BackboneElement
