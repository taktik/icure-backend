//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.datarequirement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.Element
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * What codes are expected
 *
 * Code filters specify additional constraints on the data, specifying the value set of interest for
 * a particular element of the data. Each code filter defines an additional constraint on the data,
 * i.e. code filters are AND'ed, not OR'ed.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DataRequirementCodeFilter(
  val code: List<Coding> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * A code-valued attribute to filter on
   */
  val path: String? = null,
  /**
   * A coded (token) parameter to search on
   */
  val searchParam: String? = null,
  /**
   * Valueset for the filter
   */
  val valueSet: String? = null
) : Element
