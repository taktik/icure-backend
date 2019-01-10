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

package org.taktik.icure.services.external.rest.v1.dto;

import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto;

import java.util.*;

public abstract class IcureDto extends StoredDto {
    protected Long created;
    protected Long modified;
    protected Long endOfLife;

    protected String author; //userId
    protected String responsible; //healthcarePartyId

    protected String medicalLocationId;

    protected java.util.Set<CodeDto> codes = new HashSet<>();
    protected java.util.Set<CodeDto> tags = new HashSet<>();

    //Those are typically filled in the contacts
    //Used when we want to find all contacts for a specific patient
    //These keys are in clear. You can have several to partition the medical document space
    protected java.util.Set<String> secretForeignKeys = new HashSet<>();
    //Used when we want to find the patient for this contact
    //These keys are the public patient ids encrypted using the hcParty keys.
    protected Map<String,List<DelegationDto>> cryptedForeignKeys = new HashMap<>();

    //This is typically filled in the patient
    //When a document is created, the responsible generates a cryptographically random master key
    //He/she encrypts it using his own AES exchange key and stores it as a delegation
    //The responsible is thus always in the delegations as well
    protected Map<String,List<DelegationDto>> delegations = new HashMap<>();

    //When a document needs to be encrypted, the responsible generates a cryptographically random master key (different from the delegation key, never to appear in clear anywhere in the db)
    //He/she encrypts it using his own AES exchange key and stores it as a delegation
    protected Map<String,Set<DelegationDto>> encryptionKeys = new HashMap<>();

    public void addDelegation(String healthcarePartyId, DelegationDto delegation) {
		List<DelegationDto> delegationsForHealthcarePartyId = delegations.get(healthcarePartyId);
		if (delegationsForHealthcarePartyId == null) {
			delegationsForHealthcarePartyId = new ArrayList<>();
			delegations.put(healthcarePartyId, delegationsForHealthcarePartyId);
		}

		delegations.get(healthcarePartyId).add(delegation);
	}


    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public Long getEndOfLife() {
        return endOfLife;
    }

    public void setEndOfLife(Long endOfLife) {
        this.endOfLife = endOfLife;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public Set<CodeDto> getCodes() {
        return codes;
    }

    public void setCodes(Set<CodeDto> codes) {
        this.codes = codes;
    }

    public Set<CodeDto> getTags() {
        return tags;
    }

    public void setTags(Set<CodeDto> tags) {
        this.tags = tags;
    }

    public Set<String> getSecretForeignKeys() {
        return secretForeignKeys;
    }

    public void setSecretForeignKeys(Set<String> secretForeignKeys) {
        this.secretForeignKeys = secretForeignKeys;
    }

    public Map<String, List<DelegationDto>> getCryptedForeignKeys() {
        return cryptedForeignKeys;
    }

    public void setCryptedForeignKeys(Map<String, List<DelegationDto>> cryptedForeignKeys) {
        this.cryptedForeignKeys = cryptedForeignKeys;
    }

    public Map<String, List<DelegationDto>> getDelegations() {
        return delegations;
    }

    public void setDelegations(Map<String, List<DelegationDto>> delegations) {
        this.delegations = delegations;
    }

    public String getMedicalLocationId() {
        return medicalLocationId;
    }

    public void setMedicalLocationId(String medicalLocationId) {
        this.medicalLocationId = medicalLocationId;
    }

    public void addCryptedForeignKeys(String delegateId, DelegationDto delegation) {
        List<DelegationDto> delegateCryptedForeignKeys = cryptedForeignKeys.get(delegateId);
        if (delegateCryptedForeignKeys == null) {
            delegateCryptedForeignKeys = new ArrayList<>();
			cryptedForeignKeys.put(delegateId, delegateCryptedForeignKeys);
        }

		cryptedForeignKeys.get(delegateId).add(delegation);
    }

	public void addSecretForeignKey(String newKey) {
		if (secretForeignKeys == null) {
			secretForeignKeys = new HashSet<>();
		}

		secretForeignKeys.add(newKey);
	}

    public Map<String, Set<DelegationDto>> getEncryptionKeys() {
        return encryptionKeys;
    }

    public void setEncryptionKeys(Map<String, Set<DelegationDto>> encryptionKeys) {
        this.encryptionKeys = encryptionKeys;
    }
}
