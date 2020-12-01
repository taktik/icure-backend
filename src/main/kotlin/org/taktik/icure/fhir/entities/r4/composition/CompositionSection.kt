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
package org.taktik.icure.fhir.entities.r4.composition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Composition is broken into sections
 *
 * The root of the sections that make up the composition.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CompositionSection(
  val author: List<Reference> = listOf(),
  /**
   * Classification of section (recommended)
   */
  val code: CodeableConcept? = null,
  /**
   * Why the section is empty
   */
  val emptyReason: CodeableConcept? = null,
  val entry: List<Reference> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Who/what the section is about, when it is not about the subject of composition
   */
  val focus: Reference? = null,
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * working | snapshot | changes
   */
  val mode: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Order of section entries
   */
  val orderedBy: CodeableConcept? = null,
  val section: List<CompositionSection> = listOf(),
  /**
   * Text summary of the section, for human interpretation
   */
  val text: Narrative? = null,
  /**
   * Label for section (e.g. for ToC)
   */
  val title: String? = null
) : BackboneElement
