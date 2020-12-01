//
//  Generated from FHIR Version 4.0.1-9346c8cc45
//
package org.taktik.icure.fhir.entities.r4.substanceprotein

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.codeableconcept.CodeableConcept
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.narrative.Narrative

/**
 * A SubstanceProtein is defined as a single unit of a linear amino acid sequence, or a combination
 * of subunits that are either covalently linked or have a defined invariant stoichiometric
 * relationship. This includes all synthetic, recombinant and purified SubstanceProteins of defined
 * sequence, whether the use is therapeutic or prophylactic. This set of elements will be used to
 * describe albumins, coagulation factors, cytokines, growth factors, peptide/SubstanceProtein
 * hormones, enzymes, toxins, toxoids, recombinant vaccines, and immunomodulators
 *
 * A SubstanceProtein is defined as a single unit of a linear amino acid sequence, or a combination
 * of subunits that are either covalently linked or have a defined invariant stoichiometric
 * relationship. This includes all synthetic, recombinant and purified SubstanceProteins of defined
 * sequence, whether the use is therapeutic or prophylactic. This set of elements will be used to
 * describe albumins, coagulation factors, cytokines, growth factors, peptide/SubstanceProtein
 * hormones, enzymes, toxins, toxoids, recombinant vaccines, and immunomodulators.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubstanceProtein(
  override val contained: List<Resource> = listOf(),
  val disulfideLinkage: List<String> = listOf(),
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
   * Number of linear sequences of amino acids linked through peptide bonds. The number of subunits
   * constituting the SubstanceProtein shall be described. It is possible that the number of subunits
   * can be variable
   */
  val numberOfSubunits: Int? = null,
  /**
   * The SubstanceProtein descriptive elements will only be used when a complete or partial amino
   * acid sequence is available or derivable from a nucleic acid sequence
   */
  val sequenceType: CodeableConcept? = null,
  val subunit: List<SubstanceProteinSubunit> = listOf(),
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
