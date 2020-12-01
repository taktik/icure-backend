//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.messageheader

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.contactpoint.ContactPoint
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Message source application
 *
 * The source application from which this message originated.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MessageHeaderSource(
  /**
   * Human contact for problems
   */
  val contact: ContactPoint? = null,
  /**
   * Actual message source address or id
   */
  val endpoint: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Name of system
   */
  val name: String? = null,
  /**
   * Name of software running the system
   */
  val software: String? = null,
  /**
   * Version of software running
   */
  val version: String? = null
) : BackboneElement
