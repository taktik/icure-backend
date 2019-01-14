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

package org.taktik.icure.entities.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.entities.utils.MergeUtil;
import org.taktik.icure.validation.AutoFix;
import org.taktik.icure.validation.NotNull;
import org.taktik.icure.validation.ValidCode;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Created by aduchate on 05/07/13, 20:48 */

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class StoredICureDocument extends StoredDocument implements Versionable<String>, ICureDocument {
    @NotNull(autoFix = AutoFix.NOW)
    protected Long created;
    @NotNull(autoFix = AutoFix.NOW)
    protected Long modified;
    protected Long endOfLife;

    @NotNull(autoFix = AutoFix.CURRENTUSERID)
    protected String author; //userId
    @NotNull(autoFix = AutoFix.CURRENTHCPID)
    protected String responsible; //healthcarePartyId

	@ValidCode(autoFix = AutoFix.NORMALIZECODE)
	protected java.util.Set<CodeStub> codes = new HashSet<>();
	@ValidCode(autoFix = AutoFix.NORMALIZECODE)
	protected java.util.Set<CodeStub> tags = new HashSet<>();

    //Those are typically filled in the contacts
    //Used when we want to find all contacts for a specific patient
    //These keys are in clear. You can have several to partition the medical document space
    protected java.util.Set<String> secretForeignKeys = new HashSet<>();
    //Used when we want to find the patient for this contact
    //These keys are the public patient ids encrypted using the hcParty keys.
    protected Map<String,Set<Delegation>> cryptedForeignKeys = new HashMap<>();

    //When a document is created, the responsible generates a cryptographically random master key (never to be used for something else than referencing from other entities)
    //He/she encrypts it using his own AES exchange key and stores it as a delegation
    //The responsible is thus always in the delegations as well
    protected Map<String,Set<Delegation>> delegations = new HashMap<>();

	//When a document needs to be encrypted, the responsible generates a cryptographically random master key (different from the delegation key, never to appear in clear anywhere in the db)
	//He/she encrypts it using his own AES exchange key and stores it as a delegation
	protected Map<String,Set<Delegation>> encryptionKeys = new HashMap<>();

	protected String medicalLocationId;

	public void addDelegation(String healthcarePartyId, Delegation delegation) {
		delegations.computeIfAbsent(healthcarePartyId, k -> new HashSet<>()).add(delegation);
	}

    @Override
    public Long getCreated() {
        return created;
    }

    @Override
    public void setCreated(Long created) {
        this.created = created;
    }

    @Override
    public Long getModified() {
        return modified;
    }

    @Override
    public void setModified(Long modified) {
        this.modified = modified;
    }

    @Override
    public Long getEndOfLife() {
        return endOfLife;
    }

    @Override
    public void setEndOfLife(Long endOfLife) {
        this.endOfLife = endOfLife;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String getResponsible() {
        return responsible;
    }

    @Override
    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

	private String encryptedSelf;
	@Override
	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	@Override
	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}

	public Set<CodeStub> getCodes() {
        return codes;
    }

    public void setCodes(Set<CodeStub> codes) {
        this.codes = codes;
    }

    public Set<CodeStub> getTags() {
        return tags;
    }

    public void setTags(Set<CodeStub> tags) {
        this.tags = tags;
    }

	public Map<String, Set<Delegation>> getDelegations() {
		return delegations;
	}

	public void setDelegations(Map<String, Set<Delegation>> delegations) {
		this.delegations = delegations;
	}

    public Set<String> getSecretForeignKeys() {
        return secretForeignKeys;
    }

    public String getMedicalLocationId() {
        return medicalLocationId;
    }

    public void setMedicalLocationId(String medicalLocationId) {
        this.medicalLocationId = medicalLocationId;
    }

    public void setSecretForeignKeys(Set<String> secretForeignKeys) {
        this.secretForeignKeys = secretForeignKeys;
    }
    public Map<String, Set<Delegation>> getCryptedForeignKeys() {
        return cryptedForeignKeys;
    }
    public void setCryptedForeignKeys(Map<String, Set<Delegation>> cryptedForeignKeys) {
        this.cryptedForeignKeys = cryptedForeignKeys;
    }

	public void addCryptedForeignKeys(String delegateId, Delegation delegation) {
		cryptedForeignKeys.computeIfAbsent(delegateId, k -> new HashSet<>()).add(delegation);
	}

	public void addSecretForeignKey(String newKey) {
		if (secretForeignKeys == null) {
			secretForeignKeys = new HashSet<>();
		}

		secretForeignKeys.add(newKey);
	}

	public Map<String, Set<Delegation>> getEncryptionKeys() {
		return encryptionKeys;
	}

	public void setEncryptionKeys(Map<String, Set<Delegation>> encryptionKeys) {
		this.encryptionKeys = encryptionKeys;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StoredICureDocument)) return false;
		if (!super.equals(o)) return false;
		StoredICureDocument that = (StoredICureDocument) o;
		return Objects.equals(created, that.created) &&
				Objects.equals(modified, that.modified) &&
				Objects.equals(endOfLife, that.endOfLife) &&
				Objects.equals(author, that.author) &&
				Objects.equals(responsible, that.responsible);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), created, modified, endOfLife, author, responsible);
	}

	protected void solveConflictsWith(StoredICureDocument other) {
		this.created = other.created==null?this.created:this.created==null?other.created:Long.valueOf(Math.min(this.created,other.created));
		this.modified = other.modified==null?this.modified:this.modified==null?other.modified:Long.valueOf(Math.max(this.modified,other.modified));

		this.codes.addAll(other.codes);
		this.tags.addAll(other.tags);

		this.secretForeignKeys.addAll(other.secretForeignKeys);

		this.cryptedForeignKeys = MergeUtil.mergeMapsOfSets(this.cryptedForeignKeys, other.cryptedForeignKeys, Objects::equals, (a,b)->a);
		this.delegations  = MergeUtil.mergeMapsOfSets(this.delegations, other.delegations, Objects::equals, (a,b)->a);
		this.encryptionKeys  = MergeUtil.mergeMapsOfSets(this.encryptionKeys, other.encryptionKeys, Objects::equals, (a,b)->a);
	}
}
