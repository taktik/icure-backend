/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.taktik.icure.services.external.rest.v1.dto.embed.FinancialInstitutionInformationDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.HealthcarePartyStatus;
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.Gender;
import org.taktik.icure.services.external.rest.v1.dto.embed.TelecomType;

public class HealthcarePartyDto extends StoredDto {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

	protected Set<AddressDto> addressDtos;
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
    protected String userName;
    protected String publicKey;
    protected String nihii;
    protected String ssin;
	protected String cbe;
	protected Integer convention; //0,1,2,9

    protected String notes;

    protected Map<TelecomType, String> sendFormats;
    protected Set<AddressDto> addresses;
    protected List<String> languages;
    protected List<HealthcarePartyStatus> statuses;
    protected List<CodeDto> specialityCodes; //Speciality codes, default is first


    //One AES key per HcParty, encrypted using this hcParty public key and the other hcParty public key
    //For a pair of HcParties, this key is called the AES exchange key
    //Each HcParty always has one AES exchange key for himself
	protected Map<String, String[]> hcPartyKeys = new HashMap<>();

    protected List<FinancialInstitutionInformationDto> financialInstitutionInformation = new ArrayList<>();

	protected Map<String, String> options = new HashMap<>();


	public Set<AddressDto> getAddressDtoEmbeds() {
		return addressDtos;
	}

	public void setAddressDtoEmbeds(Set<AddressDto> addressDtos) {
		this.addressDtos = addressDtos;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getNihii() {
        return nihii;
    }

    public void setNihii(String nihii) {
        this.nihii = nihii;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBic() {
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

    public String getCbe() {
		return cbe;
	}

	public void setCbe(String cbe) {
		this.cbe = cbe;
	}

	public String getInvoiceHeader() {
        return invoiceHeader;
    }

    public void setInvoiceHeader(String invoiceHeader) {
        this.invoiceHeader = invoiceHeader;
    }

    public Map<String, String[]> getHcPartyKeys() {
        return hcPartyKeys;
    }

    public void setHcPartyKeys(Map<String, String[]> hcPartyKeys) {
        this.hcPartyKeys = hcPartyKeys;
    }

    public String getSsin() {
        return ssin;
    }

    public void setSsin(String ssin) {
        this.ssin = ssin;
    }

    public Set<AddressDto> getAddressDtos() {
        return addressDtos;
    }

    public void setAddressDtos(Set<AddressDto> addressDtos) {
        this.addressDtos = addressDtos;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getCivility() {
        return civility;
    }

    public void setCivility(String civility) {
        this.civility = civility;
    }

    public Map<TelecomType, String> getSendFormats() {
        return sendFormats;
    }

    public void setSendFormats(Map<TelecomType, String> sendFormats) {
        this.sendFormats = sendFormats;
    }

    public Set<AddressDto> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<AddressDto> addresses) {
        this.addresses = addresses;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<HealthcarePartyStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<HealthcarePartyStatus> statuses) {
        this.statuses = statuses;
    }

    public List<CodeDto> getSpecialityCodes() {
        return specialityCodes;
    }

    public void setSpecialityCodes(List<CodeDto> specialityCodes) {
        this.specialityCodes = specialityCodes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

	public Integer getConvention() {
		return convention;
	}

	public void setConvention(Integer convention) {
		this.convention = convention;
	}

	public List<FinancialInstitutionInformationDto> getFinancialInstitutionInformation() {
        return financialInstitutionInformation;
    }

    public void setFinancialInstitutionInformation(List<FinancialInstitutionInformationDto> financialInstitutionInformation) {
        this.financialInstitutionInformation = financialInstitutionInformation;
    }

	public Map<String, String> getOptions() {
		return options;
	}

	public void setOptions(Map<String, String> options) {
		this.options = options;
	}
}
