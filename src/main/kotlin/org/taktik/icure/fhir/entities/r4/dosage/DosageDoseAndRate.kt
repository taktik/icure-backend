//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.dosage

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Element
import org.taktik.icure.fhir.entities.r4.Quantity
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.range.Range
import org.taktik.icure.fhir.entities.r4.ratio.Ratio

/**
 * Amount of medication administered
 *
 * The amount of medication administered.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DosageDoseAndRate(
  /**
   * Amount of medication per dose
   */
  val doseQuantity: Quantity? = null,
  /**
   * Amount of medication per dose
   */
  val doseRange: Range? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Amount of medication per unit of time
   */
  val rateQuantity: Quantity? = null,
  /**
   * Amount of medication per unit of time
   */
  val rateRange: Range? = null,
  /**
   * Amount of medication per unit of time
   */
  val rateRatio: Ratio? = null,
  /**
   * The kind of dose or rate specified
   */
  val type: CodeableConcept? = null
) : Element
