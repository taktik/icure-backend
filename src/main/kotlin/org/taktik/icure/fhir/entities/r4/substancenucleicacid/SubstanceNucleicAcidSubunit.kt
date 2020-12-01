//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.substancenucleicacid

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.attachment.Attachment
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Subunits are listed in order of decreasing length; sequences of the same length will be ordered
 * by molecular weight; subunits that have identical sequences will be repeated multiple times
 *
 * Subunits are listed in order of decreasing length; sequences of the same length will be ordered
 * by molecular weight; subunits that have identical sequences will be repeated multiple times.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceNucleicAcidSubunit(
  override val extension: List<Extension> = listOf(),
  /**
   * The nucleotide present at the 5’ terminal shall be specified based on a controlled vocabulary.
   * Since the sequence is represented from the 5' to the 3' end, the 5’ prime nucleotide is the letter
   * at the first position in the sequence. A separate representation would be redundant
   */
  val fivePrime: CodeableConcept? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * The length of the sequence shall be captured
   */
  val length: Int? = null,
  val linkage: List<SubstanceNucleicAcidSubunitLinkage> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Actual nucleotide sequence notation from 5' to 3' end using standard single letter codes. In
   * addition to the base sequence, sugar and type of phosphate or non-phosphate linkage should also be
   * captured
   */
  val sequence: String? = null,
  /**
   * (TBC)
   */
  val sequenceAttachment: Attachment? = null,
  /**
   * Index of linear sequences of nucleic acids in order of decreasing length. Sequences of the same
   * length will be ordered by molecular weight. Subunits that have identical sequences will be
   * repeated and have sequential subscripts
   */
  val subunit: Int? = null,
  val sugar: List<SubstanceNucleicAcidSubunitSugar> = listOf(),
  /**
   * The nucleotide present at the 3’ terminal shall be specified based on a controlled vocabulary.
   * Since the sequence is represented from the 5' to the 3' end, the 5’ prime nucleotide is the letter
   * at the last position in the sequence. A separate representation would be redundant
   */
  val threePrime: CodeableConcept? = null
) : BackboneElement
