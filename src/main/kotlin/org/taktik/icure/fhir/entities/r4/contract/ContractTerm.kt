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
package org.taktik.icure.fhir.entities.r4.contract

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.period.Period
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Contract Term List
 *
 * One or more Contract Provisions, which may be related and conveyed as a group, and may contain
 * nested groups.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ContractTerm(
  val action: List<ContractTermAction> = listOf(),
  /**
   * Contract Term Effective Time
   */
  val applies: Period? = null,
  val asset: List<ContractTermAsset> = listOf(),
  override val extension: List<Extension> = listOf(),
  val group: List<ContractTerm> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Contract Term Number
   */
  val identifier: Identifier? = null,
  /**
   * Contract Term Issue Date Time
   */
  val issued: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Context of the Contract term
   */
  val offer: ContractTermOffer,
  val securityLabel: List<ContractTermSecurityLabel> = listOf(),
  /**
   * Contract Term Type specific classification
   */
  val subType: CodeableConcept? = null,
  /**
   * Term Statement
   */
  val text: String? = null,
  /**
   * Term Concern
   */
  val topicCodeableConcept: CodeableConcept? = null,
  /**
   * Term Concern
   */
  val topicReference: Reference? = null,
  /**
   * Contract Term Type or Form
   */
  val type: CodeableConcept? = null
) : BackboneElement
