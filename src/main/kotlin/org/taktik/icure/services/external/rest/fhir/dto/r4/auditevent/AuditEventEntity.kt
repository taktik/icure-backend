//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.auditevent

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.coding.Coding
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * Data or objects used
 *
 * Specific instances of data or objects that have been accessed.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AuditEventEntity(
  /**
   * Descriptive text
   */
  val description: String? = null,
  val detail: List<AuditEventEntityDetail> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Life-cycle stage for the entity
   */
  val lifecycle: Coding? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Descriptor for entity
   */
  val name: String? = null,
  /**
   * Query parameters
   */
  val query: String? = null,
  /**
   * What role the entity played
   */
  val role: Coding? = null,
  val securityLabel: List<Coding> = listOf(),
  /**
   * Type of entity involved
   */
  val type: Coding? = null,
  /**
   * Specific instance of resource
   */
  val what: Reference? = null
) : BackboneElement
