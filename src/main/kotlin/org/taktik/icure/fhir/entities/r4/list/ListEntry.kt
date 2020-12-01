//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.list

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Entries in the list
 *
 * Entries in this list.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ListEntry(
  /**
   * When item added to list
   */
  val date: String? = null,
  /**
   * If this item is actually marked as deleted
   */
  val deleted: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Status/Workflow information about this item
   */
  val flag: CodeableConcept? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Actual entry
   */
  val item: Reference,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement
