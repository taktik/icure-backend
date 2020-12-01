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
package org.taktik.icure.fhir.entities.r4.substancesourcematerial

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension

/**
 * 4.9.13.8.1 Hybrid species maternal organism ID (Optional)
 *
 * 4.9.13.8.1 Hybrid species maternal organism ID (Optional).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceSourceMaterialOrganismHybrid(
  override val extension: List<Extension> = listOf(),
  /**
   * The hybrid type of an organism shall be specified
   */
  val hybridType: CodeableConcept? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * The identifier of the maternal species constituting the hybrid organism shall be specified
   * based on a controlled vocabulary. For plants, the parents aren’t always known, and it is unlikely
   * that it will be known which is maternal and which is paternal
   */
  val maternalOrganismId: String? = null,
  /**
   * The name of the maternal species constituting the hybrid organism shall be specified. For
   * plants, the parents aren’t always known, and it is unlikely that it will be known which is
   * maternal and which is paternal
   */
  val maternalOrganismName: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The identifier of the paternal species constituting the hybrid organism shall be specified
   * based on a controlled vocabulary
   */
  val paternalOrganismId: String? = null,
  /**
   * The name of the paternal species constituting the hybrid organism shall be specified
   */
  val paternalOrganismName: String? = null
) : BackboneElement
