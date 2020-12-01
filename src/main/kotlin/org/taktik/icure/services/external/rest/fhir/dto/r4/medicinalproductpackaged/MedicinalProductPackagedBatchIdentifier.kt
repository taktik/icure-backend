//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicinalproductpackaged

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier

/**
 * Batch numbering
 *
 * Batch numbering.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductPackagedBatchIdentifier(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * A number appearing on the immediate packaging (and not the outer packaging)
   */
  val immediatePackaging: Identifier? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * A number appearing on the outer packaging of a specific batch
   */
  val outerPackaging: Identifier
) : BackboneElement
