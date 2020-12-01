//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.address

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period

/**
 * An address expressed using postal conventions (as opposed to GPS or other location definition
 * formats)
 *
 * An address expressed using postal conventions (as opposed to GPS or other location definition
 * formats).  This data type may be used to convey addresses for use in delivering mail as well as for
 * visiting locations which might not be valid for mail delivery.  There are a variety of postal
 * address formats defined around the world.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Address(
  /**
   * Name of city, town etc.
   */
  val city: String? = null,
  /**
   * Country (e.g. can be ISO 3166 2 or 3 letter code)
   */
  val country: String? = null,
  /**
   * District name (aka county)
   */
  val district: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val line: List<String> = listOf(),
  /**
   * Time period when address was/is in use
   */
  val period: Period? = null,
  /**
   * Postal code for area
   */
  val postalCode: String? = null,
  /**
   * Sub-unit of country (abbreviations ok)
   */
  val state: String? = null,
  /**
   * Text representation of the address
   */
  val text: String? = null,
  /**
   * postal | physical | both
   */
  val type: String? = null,
  /**
   * home | work | temp | old | billing - purpose of this address
   */
  val use: String? = null
) : Element
