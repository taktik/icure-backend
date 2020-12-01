//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.biologicallyderivedproduct

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period

/**
 * Any manipulation of product post-collection
 *
 * Any manipulation of product post-collection that is intended to alter the product.  For example a
 * buffy-coat enrichment or CD8 reduction of Peripheral Blood Stem Cells to make it more suitable for
 * infusion.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class BiologicallyDerivedProductManipulation(
  /**
   * Description of manipulation
   */
  val description: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Time of manipulation
   */
  val timeDateTime: String? = null,
  /**
   * Time of manipulation
   */
  val timePeriod: Period? = null
) : BackboneElement
