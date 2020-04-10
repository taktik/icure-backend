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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.ReceiptBlobType
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap
import java.util.function.BiFunction

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Receipt(id: String,
              rev: String? = null,
              revisionsInfo: Array<RevisionInfo> = arrayOf(),
              conflicts: Array<String> = arrayOf(),
              revHistory: Map<String, String> = mapOf()) : StoredICureDocument(id, rev, revisionsInfo, conflicts, revHistory), Serializable {
    internal var attachmentIds: MutableMap<ReceiptBlobType, String>? = HashMap()
    var references: List<String> = ArrayList() //nipReference:027263GFF152, errorCode:186, errorPath:/request/transaction, org.taktik.icure.entities;tarification:id, org.taktik.entities.Invoice:UUID

    //The ICureDocument (Invoice, Contact, ...) this document is linked to
    var documentId: String? = null
    var category: String? = null
    var subCategory: String? = null
    fun solveConflictWith(other: Receipt): Receipt {
        super.solveConflictsWith(other)
        if (attachmentIds != null && other.attachmentIds != null) {
            other.attachmentIds!!.putAll(attachmentIds!!)
        }
        if (other.attachmentIds != null) {
            attachmentIds = other.attachmentIds
        }
        mergeListsDistinct(references, other.references, { obj: String, anObject: String? -> obj.equals(anObject) }, { a: String, b: String? -> a })
        if (documentId == null && other.documentId != null) {
            documentId = other.documentId
        }
        return this
    }

    fun getAttachmentIds(): Map<ReceiptBlobType, String>? {
        return attachmentIds
    }

    fun setAttachmentIds(attachmentIds: MutableMap<ReceiptBlobType, String>?) {
        this.attachmentIds = attachmentIds
    }

}
