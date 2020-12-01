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
package org.taktik.icure.fhir.entities.r4.searchparameter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.contactdetail.ContactDetail
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.usagecontext.UsageContext

/**
 * Search parameter for a resource
 *
 * A search parameter that defines a named search item that can be used to search/filter on a
 * resource.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SearchParameter(
  val base: List<String> = listOf(),
  val chain: List<String> = listOf(),
  /**
   * Code used in URL
   */
  val code: String? = null,
  val comparator: List<String> = listOf(),
  val component: List<SearchParameterComponent> = listOf(),
  val contact: List<ContactDetail> = listOf(),
  override val contained: List<Resource> = listOf(),
  /**
   * Date last changed
   */
  val date: String? = null,
  /**
   * Original definition for the search parameter
   */
  val derivedFrom: String? = null,
  /**
   * Natural language description of the search parameter
   */
  val description: String? = null,
  /**
   * For testing purposes, not real usage
   */
  val experimental: Boolean? = null,
  /**
   * FHIRPath expression that extracts the values
   */
  val expression: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  val jurisdiction: List<CodeableConcept> = listOf(),
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  val modifier: List<String> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Allow multiple parameters (and)
   */
  val multipleAnd: Boolean? = null,
  /**
   * Allow multiple values per parameter (or)
   */
  val multipleOr: Boolean? = null,
  /**
   * Name for this search parameter (computer friendly)
   */
  val name: String? = null,
  /**
   * Name of the publisher (organization or individual)
   */
  val publisher: String? = null,
  /**
   * Why this search parameter is defined
   */
  val purpose: String? = null,
  /**
   * draft | active | retired | unknown
   */
  val status: String? = null,
  val target: List<String> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null,
  /**
   * number | date | string | token | reference | composite | quantity | uri | special
   */
  val type: String? = null,
  /**
   * Canonical identifier for this search parameter, represented as a URI (globally unique)
   */
  val url: String? = null,
  val useContext: List<UsageContext> = listOf(),
  /**
   * Business version of the search parameter
   */
  val version: String? = null,
  /**
   * XPath that extracts the values
   */
  val xpath: String? = null,
  /**
   * normal | phonetic | nearby | distance | other
   */
  val xpathUsage: String? = null
) : DomainResource
