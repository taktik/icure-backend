//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.molecularsequence

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Variant in sequence
 *
 * The definition of variant here originates from Sequence ontology
 * ([variant_of](http://www.sequenceontology.org/browser/current_svn/term/variant_of)). This element
 * can represent amino acid or nucleic sequence change(including insertion,deletion,SNP,etc.)  It can
 * represent some complex mutation or segment variation with the assist of CIGAR string.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MolecularSequenceVariant(
  /**
   * Extended CIGAR string for aligning the sequence with reference bases
   */
  val cigar: String? = null,
  /**
   * End position of the variant on the reference sequence
   */
  val end: Int? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Allele that was observed
   */
  val observedAllele: String? = null,
  /**
   * Allele in the reference sequence
   */
  val referenceAllele: String? = null,
  /**
   * Start position of the variant on the  reference sequence
   */
  val start: Int? = null,
  /**
   * Pointer to observed variant information
   */
  val variantPointer: Reference? = null
) : BackboneElement
