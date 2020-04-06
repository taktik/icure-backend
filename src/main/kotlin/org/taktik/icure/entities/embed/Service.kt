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
package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.squareup.moshi.Json
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.ICureDocument
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode
import java.io.Serializable
import java.util.HashMap
import java.util.HashSet

/**
 * Services are created in the course a contact. Information like temperature, blood pressure and so on.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Service : ICureDocument, Serializable, Comparable<Service> {
    @NotNull(autoFix = AutoFix.UUID)
    @JsonProperty("_id")
    @Json(name = "_id")
    override var id //Two version of the same service in two separate contacts have the same id
            : String? = null

    @JsonIgnore
    var contactId //Only used when the Service is emitted outside of its contact
            : String? = null

    @JsonIgnore
    var subContactIds //Only used when the Service is emitted outside of its contact
            : Set<String>? = null

    @JsonIgnore
    var plansOfActionIds //Only used when the Service is emitted outside of its contact
            : Set<String>? = null

    @JsonIgnore
    var healthElementsIds //Only used when the Service is emitted outside of its contact
            : Set<String>? = null

    @JsonIgnore
    var formIds //Only used when the Service is emitted outside of its contact
            : Set<String>? = null

    @JsonIgnore
    var secretForeignKeys: Set<String>? = HashSet() //Only used when the Service is emitted outside of its contact

    @JsonIgnore
    var cryptedForeignKeys: Map<String, Set<Delegation>> = HashMap() //Only used when the Service is emitted outside of its contact

    @JsonIgnore
    var delegations: Map<String, Set<Delegation>> = HashMap() //Only used when the Service is emitted outside of its contact

    @JsonIgnore
    var encryptionKeys: Map<String, Set<Delegation>> = HashMap() //Only used when the Service is emitted outside of its contact

    @NotNull
    var label: String? = null
    var dataClassName: String? = null
    var index //Used for sorting
            : Long? = null
    var content: Map<String, Content> = HashMap() //Localized, in the case when the service contains a document, the document id is the SerializableValue
    var encryptedContent //Crypted (AES+base64) version of the above, deprecated, use encryptedSelf instead
            : String? = null
    var textIndexes: Map<String, String> = HashMap() //Same structure as content but used for full text indexation

    @NotNull(autoFix = AutoFix.FUZZYNOW)
    var valueDate // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
            : Long? = null

    @NotNull(autoFix = AutoFix.FUZZYNOW)
    var openingDate // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
            : Long? = null
    var closingDate // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
            : Long? = null
    var formId //Used to group logically related services
            : String? = null

    @NotNull(autoFix = AutoFix.NOW)
    override var created: Long? = null

    @NotNull(autoFix = AutoFix.NOW)
    override var modified: Long? = null
    override var endOfLife: Long? = null

    @NotNull(autoFix = AutoFix.CURRENTUSERID)
    override var author //userId
            : String? = null

    @NotNull(autoFix = AutoFix.CURRENTHCPID)
    override var responsible //healthcarePartyId
            : String? = null
    var comment: String? = null
    var status //bit 0: active/inactive, bit 1: relevant/irrelevant, bit2 : present/absent, ex: 0 = active,relevant and present
            : Int? = null
    protected var invoicingCodes: MutableSet<String> = HashSet()

    //For the content of the Service
    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    override var codes: MutableSet<CodeStub> = HashSet() //stub object of the Code

    //For the type of the Service
    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    override var tags: MutableSet<CodeStub> = HashSet() //stub object of the tag
    override var encryptedSelf: String? = null
    fun solveConflictWith(other: Service): Service {
        created = if (other.created == null) created else if (created == null) other.created else java.lang.Long.valueOf(Math.min(created!!, other.created!!))
        modified = if (other.modified == null) modified else if (modified == null) other.modified else java.lang.Long.valueOf(Math.max(modified!!, other.modified!!))
        openingDate = if (other.openingDate == null) openingDate else if (openingDate == null) other.openingDate else java.lang.Long.valueOf(Math.min(openingDate!!, other.openingDate!!))
        closingDate = if (other.closingDate == null) closingDate else if (closingDate == null) other.closingDate else java.lang.Long.valueOf(Math.max(closingDate!!, other.closingDate!!))
        valueDate = if (other.valueDate == null) valueDate else if (valueDate == null) other.valueDate else java.lang.Long.valueOf(Math.max(valueDate!!, other.valueDate!!))
        codes.addAll(other.codes)
        tags.addAll(other.tags)
        invoicingCodes.addAll(other.invoicingCodes)
        formId = if (formId == null) other.formId else formId
        return this
    }

    override fun toString(): String {
        return "Service{" +
                "id='" + id + '\'' +
                ", contactId='" + contactId + '\'' +
                ", label='" + label + '\'' +
                ", dataClassName='" + dataClassName + '\'' +
                ", index=" + index +
                ", content=" + content +
                ", textIndexes=" + textIndexes +
                ", valueDate=" + valueDate +
                ", openingDate=" + openingDate +
                ", closingDate=" + closingDate +
                ", comment='" + comment + '\'' +
                ", status=" + status +
                ", invoicingCodes=" + invoicingCodes +
                ", codes=" + codes +
                ", tags=" + tags +
                '}'
    }

    override fun compareTo(@NotNull other: Service): Int {
        if (this == other) {
            return 0
        }
        var idx = if (index != null && other.index != null) index!!.compareTo(other.index!!) else 0
        if (idx != 0) return idx
        idx = (if (id != null) id else "")!!.compareTo((if (other.id != null) other.id else "")!!)
        return if (idx != 0) idx else 1
    }
}
