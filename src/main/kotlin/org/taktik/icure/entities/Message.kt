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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.google.common.base.Objects
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import org.taktik.icure.services.external.rest.v1.dto.MessageReadStatus
import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.function.BiFunction

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Message(id: String,
              rev: String? = null,
              revisionsInfo: Array<RevisionInfo> = arrayOf(),
              conflicts: Array<String> = arrayOf(),
              revHistory: Map<String, String> = mapOf()) : StoredICureDocument(id, rev, revisionsInfo, conflicts, revHistory), Serializable {
    var fromAddress: String? = null
    var fromHealthcarePartyId: String? = null
    var formId: String? = null
    var status: Int? = null
    var recipientsType: String? = null
    private var recipients: MutableSet<String> = HashSet() //The id of the hcp whose the message is addressed to
    private var toAddresses: MutableSet<String> = HashSet() //The address of the recipient of the message. Format is of an email address with extra domains defined for mycarenet and ehealth: (efact.mycarenet.be/eattest.mycarenet.be/chapter4.mycarenet.be/ehbox.ehealth.fgov.be)
    var received: Long? = null
    var sent: Long? = null
    private var metas: MutableMap<String, String> = HashMap()
    var readStatus: MutableMap<String, MessageReadStatus> = HashMap()

    /*
		CHAP4:IN:   ${Mycarenet message ref}
		CHAP4:OUT:  ${Mycarenet message ref}
		EFACT:BATCH:${Mycarenet message ref}
		EFACT:IN:   ${Mycarenet message ref}
		EFACT:OUT:  ${Mycarenet message ref}
		GMD:IN:     ${Mycarenet message ref}
		INBOX:      ${Ehealth box message ref}
		SENTBOX:    ${Ehealth box message ref}
		BININBOX:   ${Ehealth box message ref}
		BINSENTBOX: ${Ehealth box message ref}
		REPORT:IN:  ${iCure ref}
		REPORT:OUT: ${iCure ref}
	 */
    var transportGuid //Each message should have a transportGuid: see above for formats
            : String? = null
    var remark: String? = null
    var conversationGuid: String? = null
    var subject: String? = null
    var invoiceIds: List<String> = ArrayList()
    var parentId //ID of parent in a message conversation
            : String? = null
    var externalRef: String? = null
    var unassignedResults //refs
            : Set<String>? = null
    var assignedResults //ContactId -> ref
            : Map<String, String>? = null
    var senderReferences: Map<String, String>? = null
    fun solveConflictsWith(other: Message): Message {
        super.solveConflictsWith(other)
        fromAddress = if (fromAddress == null) other.fromAddress else fromAddress
        fromHealthcarePartyId = if (fromHealthcarePartyId == null) other.fromHealthcarePartyId else fromHealthcarePartyId
        formId = if (formId == null) other.formId else formId
        recipients.addAll(other.recipients)
        toAddresses.addAll(other.toAddresses)
        received = if (other.received == null) received else if (received == null) other.received else java.lang.Long.valueOf(Math.min(received!!, other.received!!))
        sent = if (other.sent == null) sent else if (sent == null) other.sent else java.lang.Long.valueOf(Math.min(sent!!, other.sent!!))
        remark = if (remark == null) other.remark else remark
        transportGuid = if (transportGuid == null) other.transportGuid else transportGuid
        conversationGuid = if (conversationGuid == null) other.conversationGuid else conversationGuid
        subject = if (subject == null) other.subject else subject
        parentId = if (parentId == null) other.parentId else parentId
        externalRef = if (externalRef == null) other.externalRef else externalRef
        invoiceIds = mergeListsDistinct(invoiceIds, other.invoiceIds, { a: String?, b: String? -> Objects.equal(a, b) }, { a: String, b: String? -> a })
        other.metas.forEach { (k: String, v: String) -> metas.putIfAbsent(k, v) }
        return this
    }

    fun getToAddresses(): Set<String> {
        return toAddresses
    }

    fun setToAddresses(toAddresses: MutableSet<String>) {
        this.toAddresses = toAddresses
    }

    fun getMetas(): Map<String, String> {
        return metas
    }

    fun setMetas(metas: MutableMap<String, String>) {
        this.metas = metas
    }

    @get:JsonIgnore
    @set:JsonIgnore
    var secretContactKeys: MutableSet<String>?
        get() = super.secretForeignKeys
        set(secretContactKeys) {
            super.secretForeignKeys = secretContactKeys
        }

    @get:JsonIgnore
    val cryptedContactIds: Map<String, Set<Delegation>>
        get() = super.cryptedForeignKeys

    @JsonIgnore
    fun setCryptedContactIds(cryptedContactIds: MutableMap<String, MutableSet<Delegation>>) {
        super.cryptedForeignKeys = cryptedContactIds
    }

    fun getRecipients(): Set<String> {
        return recipients
    }

    fun setRecipients(recipients: MutableSet<String>) {
        this.recipients = recipients
    }

    companion object {
        const val STATUS_LABO_RESULT = 1 shl 0
        const val STATUS_UNREAD = 1 shl 1
        const val STATUS_IMPORTANT = 1 shl 2
        const val STATUS_ENCRYPTED = 1 shl 3
        const val STATUS_HAS_ANNEX = 1 shl 4
        const val STATUS_HAS_FREE_INFORMATION = 1 shl 5
        const val STATUS_EFACT = 1 shl 6
        const val STATUS_SENT = 1 shl 7
        const val STATUS_SUBMITTED = 1 shl 8 //tack
        const val STATUS_RECEIVED = 1 shl 9 //tack
        const val STATUS_ACCEPTED_FOR_TREATMENT = 1 shl 10 //931000
        const val STATUS_ACCEPTED = 1 shl 11 //920098 920900 920099
        const val STATUS_REJECTED = 1 shl 12 //920999
        const val STATUS_TACK = 1 shl 13
        const val STATUS_MASKED = 1 shl 14
        const val STATUS_FULL_SUCCESS = 1 shl 15 //920900 920098
        const val STATUS_PARTIAL_SUCCESS = 1 shl 16 //920900
        const val STATUS_FULL_ERROR = 1 shl 17 //920099 920999
        const val STATUS_ANALYZED = 1 shl 18
        const val STATUS_DELETED_ON_SERVER = 1 shl 19
        const val STATUS_SHOULD_BE_DELETED_ON_SERVER = 1 shl 20
        const val STATUS_ARCHIVED = 1 shl 21
        const val STATUS_ERRORS_IN_PRELIMINARY_CONTROL = 1 shl 22
        const val STATUS_DRAFT = 1 shl 23
        const val STATUS_SCANNED = 1 shl 24
        const val STATUS_IMPORTED = 1 shl 25
        const val STATUS_TREATED = 1 shl 26
    }
}
