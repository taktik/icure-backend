//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.capabilitystatement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Resource served on the REST interface
 *
 * A specification of the restful capabilities of the solution for a specific resource type.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CapabilityStatementRestResource(
  /**
   * If allows/uses conditional create
   */
  val conditionalCreate: Boolean? = null,
  /**
   * not-supported | single | multiple - how conditional delete is supported
   */
  val conditionalDelete: String? = null,
  /**
   * not-supported | modified-since | not-match | full-support
   */
  val conditionalRead: String? = null,
  /**
   * If allows/uses conditional update
   */
  val conditionalUpdate: Boolean? = null,
  /**
   * Additional information about the use of the resource type
   */
  val documentation: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val interaction: List<CapabilityStatementRestResourceInteraction> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  val operation: List<CapabilityStatementRestResourceOperation> = listOf(),
  /**
   * Base System profile for all uses of resource
   */
  val profile: String? = null,
  /**
   * Whether vRead can return past versions
   */
  val readHistory: Boolean? = null,
  val referencePolicy: List<String> = listOf(),
  val searchInclude: List<String> = listOf(),
  val searchParam: List<CapabilityStatementRestResourceSearchParam> = listOf(),
  val searchRevInclude: List<String> = listOf(),
  val supportedProfile: List<String> = listOf(),
  /**
   * A resource type that is supported
   */
  val type: String? = null,
  /**
   * If update can commit to a new identity
   */
  val updateCreate: Boolean? = null,
  /**
   * no-version | versioned | versioned-update
   */
  val versioning: String? = null
) : BackboneElement
