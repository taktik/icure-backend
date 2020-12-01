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
package org.taktik.icure.fhir.entities.r4.bundle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Additional execution information (transaction/batch/history)
 *
 * Additional information about how this entry should be processed as part of a transaction or
 * batch.  For history, it shows how the entry was processed to create the version contained in the
 * entry.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class BundleEntryRequest(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * For managing update contention
   */
  val ifMatch: String? = null,
  /**
   * For managing cache currency
   */
  val ifModifiedSince: String? = null,
  /**
   * For conditional creates
   */
  val ifNoneExist: String? = null,
  /**
   * For managing cache currency
   */
  val ifNoneMatch: String? = null,
  /**
   * GET | HEAD | POST | PUT | DELETE | PATCH
   */
  val method: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * URL for HTTP equivalent of this entry
   */
  val url: String? = null
) : BackboneElement
