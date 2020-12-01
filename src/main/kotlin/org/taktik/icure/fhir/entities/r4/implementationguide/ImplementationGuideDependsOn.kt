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
 * Another Implementation guide this depends on
 *
 * Another implementation guide that this implementation depends on. Typically, an implementation
 * guide uses value sets, profiles etc.defined in other implementation guides.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ImplementationGuideDependsOn(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * NPM Package name for IG this depends on
   */
  val packageId: String? = null,
  /**
   * Identity of the IG that this depends on
   */
  val uri: String? = null,
  /**
   * Version of the IG
   */
  val version: String? = null
) : BackboneElement
