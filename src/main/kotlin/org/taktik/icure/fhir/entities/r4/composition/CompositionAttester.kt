//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.composition

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Attests to accuracy of composition
 *
 * A participant who has attested to the accuracy of the composition/document.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CompositionAttester(
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  /**
   * personal | professional | legal | official
   */
  val mode: String? = null,
  override val modifierExtension: List<Extension> = listOf(),
  /**
   * Who attested the composition
   */
  val party: Reference? = null,
  /**
   * When the composition was attested
   */
  val time: String? = null
) : BackboneElement
