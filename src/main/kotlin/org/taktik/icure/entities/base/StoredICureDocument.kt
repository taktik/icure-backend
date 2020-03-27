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
package org.taktik.icure.entities.base

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.utils.MergeUtil
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode
import java.util.HashMap
import java.util.HashSet
import java.util.Objects

/** Created by aduchate on 05/07/13, 20:48  */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class StoredICureDocument : StoredDocument(), Versionable<String?>, ICureDocument {
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
    override var encryptedSelf: String? = null
    override var codes: MutableSet<CodeStub> = HashSet()

    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    override protected var tags: MutableSet<CodeStub> = HashSet()

    //Those are typically filled in the contacts
    //Used when we want to find all contacts for a specific patient
    //These keys are in clear. You can have several to partition the medical document space
    protected var secretForeignKeys: MutableSet<String>? = HashSet()

    //Used when we want to find the patient for this contact
    //These keys are the public patient ids encrypted using the hcParty keys.
    protected var cryptedForeignKeys: MutableMap<String, MutableSet<Delegation>> = HashMap()

    //When a document is created, the responsible generates a cryptographically random master key (never to be used for something else than referencing from other entities)
    //He/she encrypts it using his own AES exchange key and stores it as a delegation
    //The responsible is thus always in the delegations as well
    protected var delegations: MutableMap<String, MutableSet<Delegation>> = HashMap()

    //When a document needs to be encrypted, the responsible generates a cryptographically random master key (different from the delegation key, never to appear in clear anywhere in the db)
    //He/she encrypts it using his own AES exchange key and stores it as a delegation
    var encryptionKeys: Map<String, Set<Delegation>> = HashMap()
    var medicalLocationId: String? = null
    fun addDelegation(healthcarePartyId: String, delegation: Delegation) {
        delegations.computeIfAbsent(healthcarePartyId) { k: String? -> HashSet() }.add(delegation)
    }

    fun addCryptedForeignKeys(delegateId: String, delegation: Delegation) {
        cryptedForeignKeys.computeIfAbsent(delegateId) { k: String? -> HashSet() }.add(delegation)
    }

    fun addSecretForeignKey(newKey: String) {
        if (secretForeignKeys == null) {
            secretForeignKeys = HashSet()
        }
        secretForeignKeys!!.add(newKey)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is StoredICureDocument) return false
        if (!super.equals(o)) return false
        val that = o
        return created == that.created &&
                modified == that.modified &&
                endOfLife == that.endOfLife &&
                author == that.author &&
                responsible == that.responsible
    }

    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), created, modified, endOfLife, author, responsible)
    }

    protected fun solveConflictsWith(other: StoredICureDocument) {
        created = if (other.created == null) created else if (created == null) other.created else java.lang.Long.valueOf(Math.min(created!!, other.created!!))
        modified = if (other.modified == null) modified else if (modified == null) other.modified else java.lang.Long.valueOf(Math.max(modified!!, other.modified!!))
        codes.addAll(other.codes)
        tags.addAll(other.tags)
        secretForeignKeys!!.addAll(other.secretForeignKeys!!)
        cryptedForeignKeys = MergeUtil.mergeMapsOfSets(cryptedForeignKeys, other.cryptedForeignKeys, { a: Delegation?, b: Delegation? -> a == b }) { a: Delegation, b: Delegation? -> a }
        delegations = MergeUtil.mergeMapsOfSets(delegations, other.delegations, { a: Delegation?, b: Delegation? -> a == b }) { a: Delegation, b: Delegation? -> a }
        encryptionKeys = MergeUtil.mergeMapsOfSets(encryptionKeys, other.encryptionKeys, { a: Delegation?, b: Delegation? -> a == b }) { a: Delegation, b: Delegation? -> a }
    }
}
