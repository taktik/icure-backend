//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.services.external.rest.fhir.dto.r4.substancepolymer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.fhir.dto.r4.DomainResource
import org.taktik.icure.services.external.rest.fhir.dto.r4.Meta
import org.taktik.icure.services.external.rest.fhir.dto.r4.Resource
import org.taktik.icure.services.external.rest.fhir.dto.r4.codeableconcept.CodeableConcept
import org.taktik.icure.services.external.rest.fhir.dto.r4.extension.Extension
import org.taktik.icure.services.external.rest.fhir.dto.r4.narrative.Narrative

/**
 * Todo
 *
 * Todo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstancePolymer(
  /**
   * Todo
   */
  @JsonProperty("class")
  val class_fhir: CodeableConcept? = null,
  override val contained: List<Resource> = listOf(),
  val copolymerConnectivity: List<CodeableConcept> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Todo
   */
  val geometry: CodeableConcept? = null,
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
  val modification: List<String> = listOf(),
  override val modifierExtension: List<Extension> = listOf(),
  val monomerSet: List<SubstancePolymerMonomerSet> = listOf(),
  val repeat: List<SubstancePolymerRepeat> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
