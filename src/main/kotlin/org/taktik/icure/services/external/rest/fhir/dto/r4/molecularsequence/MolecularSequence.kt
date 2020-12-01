//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.molecularsequence

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Information about a biological sequence
 *
 * Raw data describing a biological sequence.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MolecularSequence(
  override val contained: List<Resource> = listOf(),
  /**
   * Base number of coordinate system (0 for 0-based numbering or coordinates, inclusive start,
   * exclusive end, 1 for 1-based numbering, inclusive start, inclusive end)
   */
  val coordinateSystem: Int? = null,
  /**
   * The method for sequencing
   */
  val device: Reference? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  val identifier: List<Identifier> = listOf(),
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Sequence that was observed
   */
  val observedSeq: String? = null,
  /**
   * Who and/or what this is about
   */
  val patient: Reference? = null,
  /**
   * Who should be responsible for test result
   */
  val performer: Reference? = null,
  val pointer: List<Reference> = listOf(),
  val quality: List<MolecularSequenceQuality> = listOf(),
  /**
   * The number of copies of the sequence of interest.  (RNASeq)
   */
  val quantity: Quantity? = null,
  /**
   * Average number of reads representing a given nucleotide in the reconstructed sequence
   */
  val readCoverage: Int? = null,
  /**
   * A sequence used as reference
   */
  val referenceSeq: MolecularSequenceReferenceSeq? = null,
  val repository: List<MolecularSequenceRepository> = listOf(),
  /**
   * Specimen used for sequencing
   */
  val specimen: Reference? = null,
  val structureVariant: List<MolecularSequenceStructureVariant> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * aa | dna | rna
   */
  val type: String? = null,
  val variant: List<MolecularSequenceVariant> = listOf()
) : DomainResource
