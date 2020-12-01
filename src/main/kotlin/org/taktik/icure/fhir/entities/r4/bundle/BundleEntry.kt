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
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * Entry in the bundle - will have a resource or information
 *
 * An entry in a bundle resource - will either contain a resource or information about a resource
 * (transactions and history only).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class BundleEntry(
  override val extension: List<Extension> = listOf(),
  /**
   * URI for resource (Absolute URL server address or URI for UUID/OID)
   */
  val fullUrl: String? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val link: List<BundleLink> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Additional execution information (transaction/batch/history)
   */
  val request: BundleEntryRequest? = null,
  /**
   * A resource in the bundle
   */
  val resource: Resource? = null,
  /**
   * Results of execution (transaction/batch/history)
   */
  val response: BundleEntryResponse? = null,
  /**
   * Search related information
   */
  val search: BundleEntrySearch? = null
) : BackboneElement
