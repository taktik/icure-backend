//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.implementationguide

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Page/Section in the Guide
 *
 * A page / section in the implementation guide. The root page is the implementation guide home
 * page.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ImplementationGuideDefinitionPage(
  override val extension: List<Extension> = listOf(),
  /**
   * html | markdown | xml | generated
   */
  val generation: String? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Where to find that page
   */
  val nameReference: Reference,
  /**
   * Where to find that page
   */
  val nameUrl: String? = null,
  val page: List<ImplementationGuideDefinitionPage> = listOf(),
  /**
   * Short title shown for navigational assistance
   */
  val title: String? = null
) : BackboneElement
