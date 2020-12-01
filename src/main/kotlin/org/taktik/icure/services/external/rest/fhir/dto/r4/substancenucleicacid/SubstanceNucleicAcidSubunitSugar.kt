//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.substancenucleicacid

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier

/**
 * 5.3.6.8.1 Sugar ID (Mandatory)
 *
 * 5.3.6.8.1 Sugar ID (Mandatory).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceNucleicAcidSubunitSugar(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * The Substance ID of the sugar or sugar-like component that make up the nucleotide
   */
  val identifier: Identifier? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The name of the sugar or sugar-like component that make up the nucleotide
   */
  val name: String? = null,
  /**
   * The residues that contain a given sugar will be captured. The order of given residues will be
   * captured in the 5‘-3‘direction consistent with the base sequences listed above
   */
  val residueSite: String? = null
) : BackboneElement
