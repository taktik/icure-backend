//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.implementationguide

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Information needed to build the IG
 *
 * The information needed by an IG publisher tool to publish the whole implementation guide.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ImplementationGuideDefinition(
  override val extension: List<Extension> = listOf(),
  val grouping: List<ImplementationGuideDefinitionGrouping> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Page/Section in the Guide
   */
  val page: ImplementationGuideDefinitionPage? = null,
  val parameter: List<ImplementationGuideDefinitionParameter> = listOf(),
  val resource: List<ImplementationGuideDefinitionResource> = listOf(),
  val template: List<ImplementationGuideDefinitionTemplate> = listOf()
) : BackboneElement
