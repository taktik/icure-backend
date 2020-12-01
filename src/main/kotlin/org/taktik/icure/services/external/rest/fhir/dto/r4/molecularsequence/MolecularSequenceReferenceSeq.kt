//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.molecularsequence

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * A sequence used as reference
 *
 * A sequence that is used as a reference to describe variants that are present in a sequence
 * analyzed.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MolecularSequenceReferenceSeq(
  /**
   * Chromosome containing genetic finding
   */
  val chromosome: CodeableConcept? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * The Genome Build used for reference, following GRCh build versions e.g. 'GRCh 37'
   */
  val genomeBuild: String? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * sense | antisense
   */
  val orientation: String? = null,
  /**
   * Reference identifier
   */
  val referenceSeqId: CodeableConcept? = null,
  /**
   * A pointer to another MolecularSequence entity as reference sequence
   */
  val referenceSeqPointer: Reference? = null,
  /**
   * A string to represent reference sequence
   */
  val referenceSeqString: String? = null,
  /**
   * watson | crick
   */
  val strand: String? = null,
  /**
   * End position of the window on the reference sequence
   */
  val windowEnd: Int? = null,
  /**
   * Start position of the window on the  reference sequence
   */
  val windowStart: Int? = null
) : BackboneElement
