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

/**
 * Specimen in container intended for testing by lab
 *
 * Specimen conditioned in a container as expected by the testing laboratory.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SpecimenDefinitionTypeTested(
  /**
   * The specimen's container
   */
  val container: SpecimenDefinitionTypeTestedContainer? = null,
  override val extension: List<Extension> = listOf(),
  val handling: List<SpecimenDefinitionTypeTestedHandling> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * Primary or secondary specimen
   */
  val isDerived: Boolean? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * preferred | alternate
   */
  val preference: String? = null,
  val rejectionCriterion: List<CodeableConcept> = listOf(),
  /**
   * Specimen requirements
   */
  val requirement: String? = null,
  /**
   * Specimen retention time
   */
  val retentionTime: Duration? = null,
  /**
   * Type of intended specimen
   */
  val type: CodeableConcept? = null
) : BackboneElement
