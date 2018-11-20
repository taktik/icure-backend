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


import io.swagger.annotations.ApiModelProperty;
import org.taktik.icure.services.external.rest.v1.dto.embed.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings("UnusedDeclaration")
public class PatientDto extends IcureDto {
    protected String mergeToPatientId;
	protected Set<String> mergedIds = new HashSet<>();

    protected String firstName;
    protected String lastName;
    protected String alias;
    protected Boolean active = true;
    protected DeactivationReasonDto deactivationReason = DeactivationReasonDto.none;
    protected String chronicalDisease;
    protected String ssin;
    protected String civility;
    @ApiModelProperty(dataType = "string")
    protected Gender gender = Gender.unknown;
    protected String maidenName; // Never changes (nom de jeune fille)
    protected String spouseName; // Name of the spouse after marriage
    protected String partnerName; // Name of the partner, sometimes equal to spouseName

    @ApiModelProperty(dataType = "string")
    protected PersonalStatusDto personalStatus = PersonalStatusDto.unknown;
    protected Integer dateOfBirth; //YYYYMMDD if unknown, 00, ex:20010000 or
    protected Integer dateOfDeath; //YYYYMMDD if unknown, 00, ex:20010000 or
    protected String placeOfBirth;
    protected String placeOfDeath;
    protected String education;
    protected String profession;
    protected String note;
    protected String administrativeNote;
	protected String warning;
    protected String nationality;
	protected String preferredUserId;

    @ApiModelProperty(dataType = "string")
    protected byte[] picture;

    protected String userId;

    //No guarantee of unicity
    protected String externalId;

    protected SortedSet<AddressDto> addresses = new TreeSet<>();
	protected List<InsurabilityDto> insurabilities = new ArrayList<>();
	protected List<String> languages = new ArrayList<>(); //http://www.loc.gov/standards/iso639-2/ascii_8bits.html
    protected List<PartnershipDto> partnerships = new ArrayList<>();
	protected List<PatientHealthCarePartyDto> patientHealthCareParties = new ArrayList<>();

        protected List<MedicalHouseContractDto> medicalHouseContracts = new ArrayList<>();

    protected List<FinancialInstitutionInformationDto> financialInstitutionInformation = new ArrayList<>();

    protected Map<String,List<String>> parameters = new HashMap<>();

    protected java.util.List<CodeDto> patientProfessions = new java.util.ArrayList<>();


    public String getMergeToPatientId() {
        return mergeToPatientId;
    }

    public void setMergeToPatientId(String mergeToPatientId) {
        this.mergeToPatientId = mergeToPatientId;
    }

	public Set<String> getMergedIds() {
		return mergedIds;
	}

	public void setMergedIds(Set<String> mergedIds) {
		this.mergedIds = mergedIds;
	}

	public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getChronicalDisease() {
        return chronicalDisease;
    }

    public void setChronicalDisease(String chronicalDisease) {
        this.chronicalDisease = chronicalDisease;
    }

    public String getSsin() {
        return ssin;
    }

    public void setSsin(String ssin) {
        this.ssin = ssin;
    }

    public String getCivility() {
        return civility;
    }

    public void setCivility(String civility) {
        this.civility = civility;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getMaidenName() {
        return maidenName;
    }

    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public PersonalStatusDto getPersonalStatus() {
        return personalStatus;
    }

    public void setPersonalStatus(PersonalStatusDto personalStatus) {
        this.personalStatus = personalStatus;
    }

    public Integer getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Integer dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(Integer dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getPlaceOfDeath() {
        return placeOfDeath;
    }

    public void setPlaceOfDeath(String placeOfDeath) {
        this.placeOfDeath = placeOfDeath;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public SortedSet<AddressDto> getAddresses() {
        return addresses;
    }

    public void setAddresses(SortedSet<AddressDto> addresses) {
        this.addresses = addresses;
    }

    public List<InsurabilityDto> getInsurabilities() {
        return insurabilities;
    }

    public void setInsurabilities(List<InsurabilityDto> insurabilities) {
        this.insurabilities = insurabilities;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<PartnershipDto> getPartnerships() {
        return partnerships;
    }

    public void setPartnerships(List<PartnershipDto> partnershipDtoEmbeds) {
        this.partnerships = partnershipDtoEmbeds;
    }

    public List<PatientHealthCarePartyDto> getPatientHealthCareParties() {
        return patientHealthCareParties;
    }

    public void setPatientHealthCareParties(List<PatientHealthCarePartyDto> patientHealthCareParties) {
        this.patientHealthCareParties = patientHealthCareParties;
    }

    public List<CodeDto> getPatientProfessions() {
        return patientProfessions;
    }

	public String getPreferredUserId() {
		return preferredUserId;
	}

	public void setPreferredUserId(String preferredUserId) {
		this.preferredUserId = preferredUserId;
	}

	public void setPatientProfessions(List<CodeDto> patientProfessions) {
        this.patientProfessions = patientProfessions;
    }

    public List<FinancialInstitutionInformationDto> getFinancialInstitutionInformation() {
        return financialInstitutionInformation;
    }

    public void setFinancialInstitutionInformation(List<FinancialInstitutionInformationDto> financialInstitutionInformation) {
        this.financialInstitutionInformation = financialInstitutionInformation;
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	private String encryptedSelf;
	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}

    public List<MedicalHouseContractDto> getMedicalHouseContracts() {
        return medicalHouseContracts;
    }

    public void setMedicalHouseContracts(List<MedicalHouseContractDto> medicalHouseContracts) {
        this.medicalHouseContracts = medicalHouseContracts;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		result = prime * result
				+ ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((ssin == null) ? 0 : ssin.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PatientDto other = (PatientDto) obj;

		if (getId() != null) {
			if (getId().equals(other.getId()))
				return true;
		}

		if (dateOfBirth == null) {
			if (other.dateOfBirth != null)
				return false;
		} else if (!dateOfBirth.equals(other.dateOfBirth))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (ssin == null) {
			if (other.ssin != null)
				return false;
		} else if (!ssin.equals(other.ssin))
			return false;
		return true;
	}

    public String getAdministrativeNote() {
        return administrativeNote;
    }

    public void setAdministrativeNote(String administrativeNote) {
        this.administrativeNote = administrativeNote;
    }

    public DeactivationReasonDto getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(DeactivationReasonDto deactivationReason) {
        this.deactivationReason = deactivationReason;
    }
}
