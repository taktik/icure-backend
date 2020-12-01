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
package org.taktik.icure.services.external.rest.fhir.dto.r4.medicinalproduct

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.identifier.Identifier
import org.taktik.icure.services.external.rest.fhir.dto.r4.reference.Reference

/**
 * An operation applied to the product, for manufacturing or adminsitrative purpose
 *
 * An operation applied to the product, for manufacturing or adminsitrative purpose.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicinalProductManufacturingBusinessOperation(
  /**
   * Regulatory authorization reference number
   */
  val authorisationReferenceNumber: Identifier? = null,
  /**
   * To indicate if this proces is commercially confidential
   */
  val confidentialityIndicator: CodeableConcept? = null,
  /**
   * Regulatory authorization date
   */
  val effectiveDate: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  val manufacturer: List<Reference> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The type of manufacturing operation
   */
  val operationType: CodeableConcept? = null,
  /**
   * A regulator which oversees the operation
   */
  val regulator: Reference? = null
) : BackboneElement
