/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.ektorp.Attachment
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.Encryptable
import org.taktik.icure.entities.base.ICureDocument
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.*
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CalendarItem(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String?,
        @NotNull(autoFix = AutoFix.NOW) override val created: Long?,
        @NotNull(autoFix = AutoFix.NOW) override val modified: Long?,
        @NotNull(autoFix = AutoFix.CURRENTUSERID) override val author: String?,
        @NotNull(autoFix = AutoFix.CURRENTHCPID) override val responsible: String?,
        @ValidCode(autoFix = AutoFix.NORMALIZECODE) override val tags: Set<CodeStub>,
        @ValidCode(autoFix = AutoFix.NORMALIZECODE) override val codes: Set<CodeStub>,
        override val endOfLife: Long?,
        @JsonProperty("deleted") override val deletionDate: Long?,
        @NotNull val title: String? = null,
        val calendarItemTypeId: String? = null,
        val masterCalendarItemId: String? = null,
        @Deprecated("Use crypedForeignKeys instead") val patientId: String? = null,
        val important: Boolean? = null,
        val homeVisit: Boolean? = null,
        val phoneNumber: String? = null,
        val placeId: String? = null,
        val address: Address? = null,
        val addressText: String? = null,
        @NotNull(autoFix = AutoFix.FUZZYNOW) val startTime: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        val endTime: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        val confirmationTime: Long? = null, // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20150101235960.
        val confirmationId: String? = null,
        val duration: Long? = null,
        val allDay: Boolean? = null,
        val details: String? = null,
        val wasMigrated: Boolean? = null,
        val agendaId: String? = null,
        val meetingTags: Set<CalendarItemTag> = setOf(),
        val flowItem: FlowItem? = null,
        override val secretForeignKeys: Set<String> = setOf(),
        override val cryptedForeignKeys: Map<String, Set<Delegation>> = mapOf(),
        override val delegations: Map<String, Set<Delegation>> = mapOf(),
        override val encryptionKeys: Map<String, Set<Delegation>> = mapOf(),
        override val encryptedSelf: String? = null,
        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null,
        @JsonProperty("java_type") override val _type: String = CalendarItem::javaClass.name
) : StoredDocument, ICureDocument, Encryptable {
    companion object : DynamicInitializer<CalendarItem>
    fun merge(other: CalendarItem) = CalendarItem(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: CalendarItem) = super<StoredDocument>.solveConflictsWith(other) + super<ICureDocument>.solveConflictsWith(other) + super<Encryptable>.solveConflictsWith(other) + mapOf(
                    "title" to (this.title ?: other.title),
                    "calendarItemTypeId" to (this.calendarItemTypeId ?: other.calendarItemTypeId),
                    "masterCalendarItemId" to (this.masterCalendarItemId ?: other.masterCalendarItemId),
                    "patientId" to (this.patientId ?: other.patientId),
                    "important" to (this.important ?: other.important),
                    "homeVisit" to (this.homeVisit ?: other.homeVisit),
                    "phoneNumber" to (this.phoneNumber ?: other.phoneNumber),
                    "placeId" to (this.placeId ?: other.placeId),
                    "address" to (this.address ?: other.address),
                    "addressText" to (this.addressText ?: other.addressText),
                    "startTime" to (this.startTime ?: other.startTime),
                    "endTime" to (this.endTime ?: other.endTime),
                    "confirmationTime" to (this.confirmationTime ?: other.confirmationTime),
                    "confirmationId" to (this.confirmationId ?: other.confirmationId),
                    "duration" to (this.duration ?: other.duration),
                    "allDay" to (this.allDay ?: other.allDay),
                    "details" to (this.details ?: other.details),
                    "wasMigrated" to (this.wasMigrated ?: other.wasMigrated),
                    "agendaId" to (this.agendaId ?: other.agendaId),
                    "meetingTags" to (other.meetingTags + this.meetingTags),
                    "flowItem" to (this.flowItem ?: other.flowItem)
    )
}
