//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.substancenucleicacid

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier

/**
 * The linkages between sugar residues will also be captured
 *
 * The linkages between sugar residues will also be captured.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceNucleicAcidSubunitLinkage(
  /**
   * The entity that links the sugar residues together should also be captured for nearly all
   * naturally occurring nucleic acid the linkage is a phosphate group. For many synthetic
   * oligonucleotides phosphorothioate linkages are often seen. Linkage connectivity is assumed to be
   * 3’-5’. If the linkage is either 3’-3’ or 5’-5’ this should be specified
   */
  val connectivity: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Each linkage will be registered as a fragment and have an ID
   */
  val identifier: Identifier? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Each linkage will be registered as a fragment and have at least one name. A single name shall
   * be assigned to each linkage
   */
  val name: String? = null,
  /**
   * Residues shall be captured as described in 5.3.6.8.3
   */
  val residueSite: String? = null
) : BackboneElement
