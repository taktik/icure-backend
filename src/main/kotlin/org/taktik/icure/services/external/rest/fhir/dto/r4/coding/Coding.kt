//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.coding

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.Element
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * A reference to a code defined by a terminology system
 *
 * A reference to a code defined by a terminology system.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Coding(
  /**
   * Symbol in syntax defined by the system
   */
  val code: String? = null,
  /**
   * Representation defined by the system
   */
  val display: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Identity of the terminology system
   */
  val system: String? = null,
  /**
   * If this coding was chosen directly by the user
   */
  val userSelected: Boolean? = null,
  /**
   * Version of the system - if relevant
   */
  val version: String? = null
) : Element
