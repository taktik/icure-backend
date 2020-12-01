//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.molecularsequence

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Receiver Operator Characteristic (ROC) Curve
 *
 * Receiver Operator Characteristic (ROC) Curve  to give sensitivity/specificity tradeoff.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MolecularSequenceQualityRoc(
  override val extension: List<Extension> = listOf(),
  val fMeasure: List<Float> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  val numFN: List<Int> = listOf(),
  val numFP: List<Int> = listOf(),
  val numTP: List<Int> = listOf(),
  val precision: List<Float> = listOf(),
  val score: List<Int> = listOf(),
  val sensitivity: List<Float> = listOf()
) : BackboneElement
