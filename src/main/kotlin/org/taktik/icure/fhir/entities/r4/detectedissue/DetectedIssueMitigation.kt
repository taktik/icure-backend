//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.detectedissue

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.backboneelement.BackboneElement
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Step taken to address
 *
 * Indicates an action that has been taken or is committed to reduce or eliminate the likelihood of
 * the risk identified by the detected issue from manifesting.  Can also reflect an observation of
 * known mitigating factors that may reduce/eliminate the need for any action.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DetectedIssueMitigation(
  /**
   * What mitigation?
   */
  val action: CodeableConcept,
  /**
   * Who is committing?
   */
  val author: Reference? = null,
  /**
   * Date committed
   */
  val date: String? = null,
  override val extension: List<Extension> = listOf(),
  /**
   * Unique id for inter-element referencing
   */
  override val id: String? = null,
  override val modifierExtension: List<Extension> = listOf()
) : BackboneElement
