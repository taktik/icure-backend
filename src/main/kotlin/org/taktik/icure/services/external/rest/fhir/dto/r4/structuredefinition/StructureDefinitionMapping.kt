//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.structuredefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * External specification that the content is mapped to
 *
 * An external specification that the content is mapped to.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class StructureDefinitionMapping(
  /**
   * Versions, Issues, Scope limitations etc.
   */
  val comment: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Internal id when this mapping is used
   */
  val identity: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Names what this mapping refers to
   */
  val name: String? = null,
  /**
   * Identifies what this mapping refers to
   */
  val uri: String? = null
) : BackboneElement
