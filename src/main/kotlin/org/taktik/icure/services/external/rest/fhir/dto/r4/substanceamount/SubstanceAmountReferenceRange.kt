//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.substanceamount

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.Quantity
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Reference range of possible or expected values
 *
 * Reference range of possible or expected values.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceAmountReferenceRange(
  override val extension: List<Extension> = listOf(),
  /**
   * Upper limit possible or expected
   */
  val highLimit: Quantity? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Lower limit possible or expected
   */
  val lowLimit: Quantity? = null
) : Element
