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
package org.taktik.icure.services.external.rest.fhir.dto.r4.substancenucleicacid

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative

/**
 * Nucleic acids are defined by three distinct elements: the base, sugar and linkage. Individual
 * substance/moiety IDs will be created for each of these elements. The nucleotide sequence will be
 * always entered in the 5’-3’ direction
 *
 * Nucleic acids are defined by three distinct elements: the base, sugar and linkage. Individual
 * substance/moiety IDs will be created for each of these elements. The nucleotide sequence will be
 * always entered in the 5’-3’ direction.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceNucleicAcid(
  /**
   * The area of hybridisation shall be described if applicable for double stranded RNA or DNA. The
   * number associated with the subunit followed by the number associated to the residue shall be
   * specified in increasing order. The underscore “” shall be used as separator as follows:
   * “Subunitnumber Residue”
   */
  val areaOfHybridisation: String? = null,
  override val contained: List<Resource> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * Language of the resource content
   */
  override val language: String? = null,
  /**
   * Metadata about the resource
   */
  override val meta: Meta? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * The number of linear sequences of nucleotides linked through phosphodiester bonds shall be
   * described. Subunits would be strands of nucleic acids that are tightly associated typically
   * through Watson-Crick base pairing. NOTE: If not specified in the reference source, the assumption
   * is that there is 1 subunit
   */
  val numberOfSubunits: Int? = null,
  /**
   * (TBC)
   */
  val oligoNucleotideType: CodeableConcept? = null,
  /**
   * The type of the sequence shall be specified based on a controlled vocabulary
   */
  val sequenceType: CodeableConcept? = null,
  val subunit: List<SubstanceNucleicAcidSubunit> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
