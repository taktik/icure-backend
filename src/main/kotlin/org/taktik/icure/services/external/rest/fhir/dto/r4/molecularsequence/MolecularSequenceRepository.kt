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
 * External repository which contains detailed report related with observedSeq in this resource
 *
 * Configurations of the external repository. The repository shall store target's observedSeq or
 * records related with target's observedSeq.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MolecularSequenceRepository(
  /**
   * Id of the dataset that used to call for dataset in repository
   */
  val datasetId: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Repository's name
   */
  val name: String? = null,
  /**
   * Id of the read
   */
  val readsetId: String? = null,
  /**
   * directlink | openapi | login | oauth | other
   */
  val type: String? = null,
  /**
   * URI of the repository
   */
  val url: String? = null,
  /**
   * Id of the variantset that used to call for variantset in repository
   */
  val variantsetId: String? = null
) : BackboneElement
