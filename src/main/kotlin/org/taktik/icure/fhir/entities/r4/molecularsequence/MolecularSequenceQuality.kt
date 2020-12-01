/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.molecularsequence

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * An set of value as quality of sequence
 *
 * An experimental feature attribute that defines the quality of the feature in a quantitative way,
 * such as a phred quality score
 * ([SO:0001686](http://www.sequenceontology.org/browser/current_svn/term/SO:0001686)).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MolecularSequenceQuality(
  /**
   * End position of the sequence
   */
  val end: Int? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * F-score
   */
  val fScore: Float? = null,
  /**
   * False positives where the non-REF alleles in the Truth and Query Call Sets match
   */
  val gtFP: Float? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Method to get quality
   */
  val method: CodeableConcept? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Precision of comparison
   */
  val precision: Float? = null,
  /**
   * False positives
   */
  val queryFP: Float? = null,
  /**
   * True positives from the perspective of the query data
   */
  val queryTP: Float? = null,
  /**
   * Recall of comparison
   */
  val recall: Float? = null,
  /**
   * Receiver Operator Characteristic (ROC) Curve
   */
  val roc: MolecularSequenceQualityRoc? = null,
  /**
   * Quality score for the comparison
   */
  val score: Quantity? = null,
  /**
   * Standard sequence for comparison
   */
  val standardSequence: CodeableConcept? = null,
  /**
   * Start position of the sequence
   */
  val start: Int? = null,
  /**
   * False negatives
   */
  val truthFN: Float? = null,
  /**
   * True positives from the perspective of the truth data
   */
  val truthTP: Float? = null,
  /**
   * indel | snp | unknown
   */
  val type: String? = null
) : BackboneElement
