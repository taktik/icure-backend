//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.humanname

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Element
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.period.Period

/**
 * Name of a human - parts and usage
 *
 * A human's name with the ability to identify parts and usage.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class HumanName(
  override val extension: List<Extension> = listOf(),
  /**
   * Family name (often called 'Surname')
   */
  val family: String? = null,
  val given: List<String> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Time period when name was/is in use
   */
  val period: Period? = null,
  val prefix: List<String> = listOf(),
  val suffix: List<String> = listOf(),
  /**
   * Text representation of the full name
   */
  val text: String? = null,
  /**
   * usual | official | temp | nickname | anonymous | old | maiden
   */
  val use: String? = null
) : Element
