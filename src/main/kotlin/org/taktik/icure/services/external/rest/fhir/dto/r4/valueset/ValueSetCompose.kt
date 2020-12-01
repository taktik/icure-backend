//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.valueset

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * Content logical definition of the value set (CLD)
 *
 * A set of criteria that define the contents of the value set by including or excluding codes
 * selected from the specified code system(s) that the value set draws from. This is also known as the
 * Content Logical Definition (CLD).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ValueSetCompose(
  val exclude: List<ValueSetComposeInclude> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Whether inactive codes are in the value set
   */
  val inactive: Boolean? = null,
  val include: List<ValueSetComposeInclude> = listOf(),
  /**
   * Fixed date for references with no specified version (transitive)
   */
  val lockedDate: String? = null,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement
