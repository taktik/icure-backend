//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.terminologycapabilities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Information about the [ValueSet/$expand](valueset-operation-expand.html) operation
 *
 * Information about the [ValueSet/$expand](valueset-operation-expand.html) operation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TerminologyCapabilitiesExpansion(
  override val extension: List<Extension> = listOf(),
  /**
   * Whether the server can return nested value sets
   */
  val hierarchical: Boolean? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Allow request for incomplete expansions?
   */
  val incomplete: Boolean? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Whether the server supports paging on expansion
   */
  val paging: Boolean? = null,
  val parameter: List<TerminologyCapabilitiesExpansionParameter> = listOf(),
  /**
   * Documentation about text searching works
   */
  val textFilter: String? = null
) : BackboneElement
