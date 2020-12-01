//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.substancesourcematerial

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * 4.9.13.8.1 Hybrid species maternal organism ID (Optional)
 *
 * 4.9.13.8.1 Hybrid species maternal organism ID (Optional).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceSourceMaterialOrganismHybrid(
  override val extension: List<Extension> = listOf(),
  /**
   * The hybrid type of an organism shall be specified
   */
  val hybridType: CodeableConcept? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * The identifier of the maternal species constituting the hybrid organism shall be specified
   * based on a controlled vocabulary. For plants, the parents aren’t always known, and it is unlikely
   * that it will be known which is maternal and which is paternal
   */
  val maternalOrganismId: String? = null,
  /**
   * The name of the maternal species constituting the hybrid organism shall be specified. For
   * plants, the parents aren’t always known, and it is unlikely that it will be known which is
   * maternal and which is paternal
   */
  val maternalOrganismName: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The identifier of the paternal species constituting the hybrid organism shall be specified
   * based on a controlled vocabulary
   */
  val paternalOrganismId: String? = null,
  /**
   * The name of the paternal species constituting the hybrid organism shall be specified
   */
  val paternalOrganismName: String? = null
) : BackboneElement
