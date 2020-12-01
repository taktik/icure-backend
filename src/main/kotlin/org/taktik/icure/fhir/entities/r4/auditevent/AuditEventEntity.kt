/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.auditevent

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.coding.Coding
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

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
