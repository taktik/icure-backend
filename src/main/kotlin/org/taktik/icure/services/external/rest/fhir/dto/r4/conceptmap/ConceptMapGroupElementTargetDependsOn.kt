//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.conceptmap

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Other elements required for this mapping (from context)
 *
 * A set of additional dependencies for this mapping to hold. This mapping is only applicable if the
 * specified element can be resolved, and it has the specified value.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ConceptMapGroupElementTargetDependsOn(
  /**
   * Display for the code (if value is a code)
   */
  val display: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Reference to property mapping depends on
   */
  val property: String? = null,
  /**
   * Code System (if necessary)
   */
  val system: String? = null,
  /**
   * Value of the referenced element
   */
  val value: String? = null
) : BackboneElement
