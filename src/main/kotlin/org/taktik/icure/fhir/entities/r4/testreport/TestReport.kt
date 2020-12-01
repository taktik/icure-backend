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
package org.taktik.icure.fhir.entities.r4.testreport

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.fhir.entities.r4.DomainResource
import org.taktik.icure.fhir.entities.r4.Meta
import org.taktik.icure.fhir.entities.r4.Resource
import org.taktik.icure.fhir.entities.r4.extension.Extension
import org.taktik.icure.fhir.entities.r4.identifier.Identifier
import org.taktik.icure.fhir.entities.r4.narrative.Narrative
import org.taktik.icure.fhir.entities.r4.reference.Reference

/**
 * Describes the results of a TestScript execution
 *
 * A summary of information based on the results of executing a TestScript.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TestReport(
  override val contained: List<Resource> = listOf(),
  override val extension: List<Extension> = listOf(),
  /**
   * Logical id of this artifact
   */
  override val id: String? = null,
  /**
   * External identifier
   */
  val identifier: Identifier? = null,
  /**
   * A set of rules under which this content was created
   */
  override val implicitRules: String? = null,
  /**
   * When the TestScript was executed and this TestReport was generated
   */
  val issued: String? = null,
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
   * Informal name of the executed TestScript
   */
  val name: String? = null,
  val participant: List<TestReportParticipant> = listOf(),
  /**
   * pass | fail | pending
   */
  val result: String? = null,
  /**
   * The final score (percentage of tests passed) resulting from the execution of the TestScript
   */
  val score: Float? = null,
  /**
   * The results of the series of required setup operations before the tests were executed
   */
  val setup: TestReportSetup? = null,
  /**
   * completed | in-progress | waiting | stopped | entered-in-error
   */
  val status: String? = null,
  /**
   * The results of running the series of required clean up steps
   */
  val teardown: TestReportTeardown? = null,
  val test: List<TestReportTest> = listOf(),
  /**
   * Reference to the  version-specific TestScript that was executed to produce this TestReport
   */
  val testScript: Reference,
  /**
   * Name of the tester producing this report (Organization or individual)
   */
  val tester: String? = null,
  /**
   * Text summary of the resource, for human interpretation
   */
  override val text: Narrative? = null
) : DomainResource
