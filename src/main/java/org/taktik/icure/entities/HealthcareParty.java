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

package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.Nullable;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.base.CodeStub;
import org.taktik.icure.entities.base.Person;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.entities.embed.Address;
import org.taktik.icure.entities.embed.FinancialInstitutionInformation;
import org.taktik.icure.entities.embed.Gender;
import org.taktik.icure.entities.embed.HealthcarePartyStatus;
import org.taktik.icure.entities.embed.TelecomType;
import org.taktik.icure.validation.AutoFix;
import org.taktik.icure.validation.ValidCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthcareParty extends StoredDocument implements Person {
	protected String name;
	protected String lastName;
    protected String firstName;
    protected Gender gender;
    protected String civility;
	protected String speciality;
	protected String companyName;
	protected String bankAccount;
	protected String bic;
	protected String proxyBankAccount;
	protected String proxyBic;
	protected String invoiceHeader;
	protected String cbe;

	protected String userId;

	protected String parentId;

	protected Integer convention; //0,1,2,9
    protected String supervisorId;

    protected String nihii; //institution, person
	protected String nihiiSpecCode; //don't show field in the GUI

    protected String ssin;

    protected Set<Address> addresses = new HashSet<>();
    protected List<String> languages =  new LinkedList<>();

    protected List<HealthcarePartyStatus> statuses;

    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    protected List<CodeStub> specialityCodes; //Speciality codes, default is first
	protected Map<TelecomType, String> sendFormats;

	protected String notes;

	//One AES key per HcParty, encrypted using this hcParty public key and the other hcParty public key
	//For a pair of HcParties, this key is called the AES exchange key
	//Each HcParty always has one AES exchange key for himself
	// The map's keys are the delegate id.
    // In the table, we get at the first position: the key encrypted using owner (this)'s public key and in 2nd pos.
    // the key encrypted using delegate's public key.
    protected Map<String, String[]> hcPartyKeys = new HashMap<String, String[]>();

    protected List<FinancialInstitutionInformation> financialInstitutionInformation = new ArrayList<>();
	protected Map<String, String> options = new HashMap<>();

    protected String publicKey;


    // Medical houses
    protected String billingType;                       // "serviceFee" (à l'acte) or "flatRate" (forfait)
    protected String type;                              // "persphysician" or "medicalHouse"
    protected String contactPerson;


	public HealthcareParty() {

	}

	public @Nullable
	String getNihiiSpecCode() {
		return nihiiSpecCode;
	}

	public void setNihiiSpecCode(String nihiiSpecCode) {
		this.nihiiSpecCode = nihiiSpecCode;
	}

	public @Nullable String getSsin() {
        return ssin;
    }

    public void setSsin(String ssin) {
        this.ssin = ssin;
    }

    public @Nullable String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public @Nullable String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public @Nullable String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public @Nullable Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public @Nullable String getCivility() {
        return civility;
    }

    public void setCivility(String civility) {
        this.civility = civility;
    }

    public @Nullable String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public @Nullable String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public @Nullable String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

	public @Nullable String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public String getProxyBankAccount() {
		return proxyBankAccount;
	}

	public void setProxyBankAccount(String proxyBankAccount) {
		this.proxyBankAccount = proxyBankAccount;
	}

	public String getProxyBic() {
		return proxyBic;
	}

	public void setProxyBic(String proxyBic) {
		this.proxyBic = proxyBic;
	}

	public @Nullable String getCbe() {
		return cbe;
	}

	public void setCbe(String cbe) {
		this.cbe = cbe;
	}

	public @Nullable String getInvoiceHeader() {
        return invoiceHeader;
    }

    public void setInvoiceHeader(String invoiceHeader) {
        this.invoiceHeader = invoiceHeader;
    }

    public @Nullable String getNihii() {
        return nihii;
    }

    public void setNihii(String nihii) {
        this.nihii = nihii;
    }

    public Map<TelecomType, String> getSendFormats() {
        return sendFormats;
    }

    public void setSendFormats(Map<TelecomType, String> sendFormats) {
        this.sendFormats = sendFormats;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public Map<String, String[]> getHcPartyKeys() {
        return hcPartyKeys;
    }

    public void setHcPartyKeys(Map<String, String[]> hcPartyKeys) {
        this.hcPartyKeys = hcPartyKeys;
    }

	public @Nullable String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

    public List<HealthcarePartyStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<HealthcarePartyStatus> statuses) {
        this.statuses = statuses;
    }

    public List<CodeStub> getSpecialityCodes() {
        return specialityCodes;
    }

    public void setSpecialityCodes(List<CodeStub> specialityCodes) {
        this.specialityCodes = specialityCodes;
    }

	public @Nullable String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public @Nullable Integer getConvention() {
		return convention;
	}

	public void setConvention(Integer convention) {
		this.convention = convention;
	}

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

	public List<FinancialInstitutionInformation> getFinancialInstitutionInformation() {
        return financialInstitutionInformation;
    }

    public void setFinancialInstitutionInformation(List<FinancialInstitutionInformation> financialInstitutionInformation) {
        this.financialInstitutionInformation = financialInstitutionInformation;
    }

	public Map<String, String> getOptions() {
		return options;
	}

	public void setOptions(Map<String, String> options) {
		this.options = options;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@JsonIgnore
	public @Nullable String getFullName() {
		String full;
		full= lastName;
		if(firstName!=null ) {
			if(full!=null) full = full+" "+firstName;
			else full = firstName;
		}

		return full;
	}

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getBillingType() {
        return billingType;
    }

    public void setBillingType(String billingType) {
        this.billingType = billingType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
}
