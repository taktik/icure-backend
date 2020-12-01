//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.contactpoint

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.period.Period

/**
 * Details of a Technology mediated contact point (phone, fax, email, etc.)
 *
 * Details for all kinds of technology mediated contact points for a person or organization,
 * including telephone, email, etc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ContactPoint(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Time period when the contact point was/is in use
   */
  val period: Period? = null,
  /**
   * Specify preferred order of use (1 = highest)
   */
  val rank: Int? = null,
  /**
   * phone | fax | email | pager | url | sms | other
   */
  val system: String? = null,
  /**
   * home | work | temp | old | mobile - purpose of this contact point
   */
  val use: String? = null,
  /**
   * The actual contact point details
   */
  val value: String? = null
) : Element
