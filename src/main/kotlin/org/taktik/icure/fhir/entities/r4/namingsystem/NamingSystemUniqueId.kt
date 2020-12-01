//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.namingsystem

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.period.Period

/**
 * Unique identifiers used for system
 *
 * Indicates how the system may be identified when referenced in electronic exchange.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class NamingSystemUniqueId(
  /**
   * Notes about identifier usage
   */
  val comment: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * When is identifier valid?
   */
  val period: Period? = null,
  /**
   * Is this the id that should be used for this type
   */
  val preferred: Boolean? = null,
  /**
   * oid | uuid | uri | other
   */
  val type: String? = null,
  /**
   * The unique identifier
   */
  val value: String? = null
) : BackboneElement
