//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.implementationguide

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Resource in the implementation guide
 *
 * A resource that is part of the implementation guide. Conformance resources (value set, structure
 * definition, capability statements etc.) are obvious candidates for inclusion, but any kind of
 * resource can be included as an example resource.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ImplementationGuideDefinitionResource(
  /**
   * Reason why included in guide
   */
  val description: String? = null,
  /**
   * Is an example/What is this an example of?
   */
  val exampleBoolean: Boolean? = null,
  /**
   * Is an example/What is this an example of?
   */
  val exampleCanonical: String? = null,
  override val extension: List<Extension> = listOf(),
  val fhirVersion: List<String> = listOf(),
  /**
   * Grouping this is part of
   */
  val groupingId: String? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Human Name for the resource
   */
  val name: String? = null,
  /**
   * Location of the resource
   */
  val reference: Reference
) : BackboneElement
