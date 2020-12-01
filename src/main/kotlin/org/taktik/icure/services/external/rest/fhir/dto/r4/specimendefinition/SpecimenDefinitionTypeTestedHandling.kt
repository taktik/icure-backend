//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.specimendefinition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.backboneelement.BackboneElement
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.duration.Duration
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.range.Range

/**
 * Specimen handling before testing
 *
 * Set of instructions for preservation/transport of the specimen at a defined temperature interval,
 * prior the testing process.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SpecimenDefinitionTypeTestedHandling(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Preservation instruction
   */
  val instruction: String? = null,
  /**
   * Maximum preservation time
   */
  val maxDuration: Duration? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Temperature qualifier
   */
  val temperatureQualifier: CodeableConcept? = null,
  /**
   * Temperature range
   */
  val temperatureRange: Range? = null
) : BackboneElement
