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
package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.ICureDocument
import org.taktik.icure.entities.utils.MergeUtil
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode

/**
 * Created by aduchate on 06/07/13, 10:09
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SubContact(
	@JsonProperty("_id") override val id: String? = null,
	@field:NotNull(autoFix = AutoFix.NOW) override val created: Long? = null,
	@field:NotNull(autoFix = AutoFix.NOW) override val modified: Long? = null,
	@field:NotNull(autoFix = AutoFix.CURRENTUSERID) override val author: String? = null,
	@field:NotNull(autoFix = AutoFix.CURRENTHCPID) override val responsible: String? = null,
	override val medicalLocationId: String? = null,
	@field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val tags: Set<CodeStub> = emptySet(),
	@field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val codes: Set<CodeStub> = emptySet(),
	override val endOfLife: Long? = null,
	val descr: String? = null,
	val protocol: String? = null,
	val status: Int? = null, //To be refactored
	val formId: String? = null, // form or subform unique ID. Several subcontacts with the same form ID can coexist as long as they are in different contacts or they relate to a different planOfActionID
	val planOfActionId: String? = null,
	val healthElementId: String? = null,
	val classificationId: String? = null,
	val services: List<ServiceLink> = emptyList(),
	override val encryptedSelf: String? = null
) : Encrypted, ICureDocument<String?> {
	companion object : DynamicInitializer<SubContact> {
		const val STATUS_LABO_RESULT = 1
		const val STATUS_UNREAD = 2
		const val STATUS_ALWAYS_DISPLAY = 4
		const val RESET_TO_DEFAULT_VALUES = 8
		const val STATUS_COMPLETE = 16
		const val STATUS_PROTOCOL_RESULT = 32
		const val STATUS_UPLOADED_FILES = 64
	}

	fun merge(other: SubContact) = SubContact(args = this.solveConflictsWith(other))
	fun solveConflictsWith(other: SubContact) = super<Encrypted>.solveConflictsWith(other) + super<ICureDocument>.solveConflictsWith(other) + mapOf(
		"descr" to (this.descr ?: other.descr),
		"protocol" to (this.protocol ?: other.protocol),
		"status" to (this.status ?: other.status),
		"formId" to (this.formId ?: other.formId),
		"planOfActionId" to (this.planOfActionId ?: other.planOfActionId),
		"healthElementId" to (this.healthElementId ?: other.healthElementId),
		"classificationId" to (this.classificationId ?: other.classificationId),
		"services" to MergeUtil.mergeListsDistinct(this.services, other.services, { a, b -> a.serviceId == b.serviceId })
	)

	override fun withTimestamps(created: Long?, modified: Long?) =
		when {
			created != null && modified != null -> this.copy(created = created, modified = modified)
			created != null -> this.copy(created = created)
			modified != null -> this.copy(modified = modified)
			else -> this
		}
}
