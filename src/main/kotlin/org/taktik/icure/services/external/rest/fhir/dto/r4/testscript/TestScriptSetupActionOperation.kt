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
package org.taktik.icure.services.external.rest.fhir.dto.r4.testscript

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.coding.Coding
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension

/**
 * The setup operation to perform
 *
 * The operation to perform.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TestScriptSetupActionOperation(
  /**
   * Mime type to accept in the payload of the response, with charset etc.
   */
  val accept: String? = null,
  /**
   * Mime type of the request payload contents, with charset etc.
   */
  val contentType: String? = null,
  /**
   * Tracking/reporting operation description
   */
  val description: String? = null,
  /**
   * Server responding to the request
   */
  val destination: Int? = null,
  /**
   * Whether or not to send the request url in encoded format
   */
  val encodeRequestUrl: Boolean? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Tracking/logging operation label
   */
  val label: String? = null,
  /**
   * delete | get | options | patch | post | put | head
   */
  val method: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Server initiating the request
   */
  val origin: Int? = null,
  /**
   * Explicitly defined path parameters
   */
  val params: String? = null,
  val requestHeader: List<TestScriptSetupActionOperationRequestHeader> = listOf(),
  /**
   * Fixture Id of mapped request
   */
  val requestId: String? = null,
  /**
   * Resource type
   */
  val resource: String? = null,
  /**
   * Fixture Id of mapped response
   */
  val responseId: String? = null,
  /**
   * Fixture Id of body for PUT and POST requests
   */
  val sourceId: String? = null,
  /**
   * Id of fixture used for extracting the [id],  [type], and [vid] for GET requests
   */
  val targetId: String? = null,
  /**
   * The operation code type that will be executed
   */
  val type: Coding? = null,
  /**
   * Request URL
   */
  val url: String? = null
) : BackboneElement
