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
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.Nullable;
import org.taktik.icure.entities.base.CodeStub;
import org.taktik.icure.entities.base.CryptoActor;
import org.taktik.icure.entities.base.Encryptable;
import org.taktik.icure.entities.base.Person;
import org.taktik.icure.entities.base.StoredICureDocument;
import org.taktik.icure.entities.embed.*;
import org.taktik.icure.entities.utils.MergeUtil;
import org.taktik.icure.validation.AutoFix;
import org.taktik.icure.validation.ValidCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("UnusedDeclaration")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient extends StoredICureDocument implements Person, Encryptable, CryptoActor {
    protected String mergeToPatientId;
	protected Set<String> mergedIds = new HashSet<>();
    protected Set<String> nonDuplicateIds = new HashSet<>();
    protected Set<String> encryptedAdministrativesDocuments = new HashSet<>();

    protected String firstName;
    protected String lastName;  //Is usually either maidenName or spouseName
    protected String alias;
    protected boolean active = true;
    protected DeactivationReason deactivationReason = DeactivationReason.none;
    protected String ssin;
    protected String civility;
    protected Gender gender = Gender.unknown;
    protected String maidenName; // Never changes (nom de jeune fille)
    protected String spouseName; // Name of the spouse after marriage
    protected String partnerName; // Name of the partner, sometimes equal to spouseName
    protected PersonalStatus personalStatus = PersonalStatus.unknown;


    protected Integer dateOfBirth; // YYYYMMDD if unknown, 00, ex:20010000 or
	protected Integer dateOfDeath; // YYYYMMDD if unknown, 00, ex:20010000 or
    protected Integer timestampOfLatestEidReading;
    protected String placeOfBirth;
    protected String placeOfDeath;
    protected String education;
    protected String profession;
    protected String note;
	protected String administrativeNote;
	protected String comment;

    protected String warning;
	protected String nationality;

	protected String preferredUserId;

    protected byte[] picture;

    //No guarantee of unicity
    protected String externalId;

    protected List<Address> addresses = new ArrayList<>();
    protected List<Insurability> insurabilities = new ArrayList<>();
    protected List<String> languages = new ArrayList<>(); //alpha-2 code http://www.loc.gov/standards/iso639-2/ascii_8bits.html
    protected List<Partnership> partnerships = new ArrayList<>();
	protected List<PatientHealthCareParty> patientHealthCareParties = new ArrayList<>();
    protected List<FinancialInstitutionInformation> financialInstitutionInformation = new ArrayList<>();
	protected List<MedicalHouseContract> medicalHouseContracts = new ArrayList<>();

    protected Map<String,List<String>> parameters = new HashMap<>();

	@ValidCode(autoFix = AutoFix.NORMALIZECODE)
	protected java.util.List<CodeStub> patientProfessions = new ArrayList<>();

    //One AES key per HcParty, encrypted using this hcParty public key and the other hcParty public key
    //For a pair of HcParties, this key is called the AES exchange key
    //Each HcParty always has one AES exchange key for himself
    // The map's keys are the delegate id.
    // In the table, we get at the first position: the key encrypted using owner (this)'s public key and in 2nd pos.
    // the key encrypted using delegate's public key.
    protected Map<String, String[]> hcPartyKeys = new HashMap<String, String[]>();
    protected String publicKey;

    protected CodeStub fatherBirthCountry;
    protected CodeStub birthCountry;
    protected CodeStub nativeCountry;
    protected CodeStub socialStatus;
    protected CodeStub mainSourceOfIncome;
    protected List<SchoolingInfo> schoolingInfos = new ArrayList<>();
    protected List<EmploymentInfo> employementInfos = new ArrayList<>();
    private Set<Property> properties = new HashSet<>();

    public @Nullable
	String getMergeToPatientId() {
        return mergeToPatientId;
    }

	public Set<String> getMergedIds() {
		return mergedIds;
	}

	public void setMergedIds(Set<String> mergedIds) {
		this.mergedIds = mergedIds;
	}
    public void setMergeToPatientId(String mergeToPatientId) {
        this.mergeToPatientId = mergeToPatientId;
    }

    public Set<String> getNonDuplicateIds() {  return nonDuplicateIds;  }

    public void setNonDuplicateIds(Set<String> nonDuplicateIds) {  this.nonDuplicateIds = nonDuplicateIds; }

    public @Nullable String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public @Nullable String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public @Nullable String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public @Nullable boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public @Nullable String getSsin() {
        return ssin;
    }

    public void setSsin(String ssin) {
        this.ssin = ssin;
    }

    public @Nullable String getCivility() {
        return civility;
    }

    public void setCivility(String civility) {
        this.civility = civility;
    }

    public @Nullable Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public @Nullable String getMaidenName() {
        return maidenName;
    }

    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }

    public @Nullable String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public @Nullable String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public @Nullable PersonalStatus getPersonalStatus() {
        return personalStatus;
    }

    public void setPersonalStatus(PersonalStatus personalStatus) {
        this.personalStatus = personalStatus;
    }

    public @Nullable Integer getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Integer dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

	public @Nullable Integer getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(Integer dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public @Nullable Integer getTimestampOfLatestEidReading() {
        return timestampOfLatestEidReading;
    }

    public void setTimestampOfLatestEidReading(Integer timestampOfLatestEidReading) {
        this.timestampOfLatestEidReading = timestampOfLatestEidReading;
    }

    public @Nullable String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public @Nullable String getPlaceOfDeath() {
        return placeOfDeath;
    }

    public void setPlaceOfDeath(String placeOfDeath) {
        this.placeOfDeath = placeOfDeath;
    }

    public @Nullable String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public @Nullable String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public @Nullable String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public @Nullable String getNationality() {
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

    public @Nullable String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<Insurability> getInsurabilities() {
        return insurabilities;
    }

    public void setInsurabilities(List<Insurability> insurabilities) {
        this.insurabilities = insurabilities;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<Partnership> getPartnerships() {
        return partnerships;
    }

    public void setPartnerships(List<Partnership> partnerships) {
        this.partnerships = partnerships;
    }

    public List<PatientHealthCareParty> getPatientHealthCareParties() {
        return patientHealthCareParties;
    }

    public void setPatientHealthCareParties(List<PatientHealthCareParty> patientHealthCareParties) {
        this.patientHealthCareParties = patientHealthCareParties;
    }

    public List<CodeStub> getPatientProfessions() {
        return patientProfessions;
    }

    public void setPatientProfessions(List<CodeStub> patientProfessions) {
        this.patientProfessions = patientProfessions;
    }

	public @Nullable String getPreferredUserId() {
		return preferredUserId;
	}

	public void setPreferredUserId(String preferredUserId) {
		this.preferredUserId = preferredUserId;
	}

	public @Nullable String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

    public List<MedicalHouseContract> getMedicalHouseContracts() {
        return medicalHouseContracts;
    }

    public void setMedicalHouseContracts(List<MedicalHouseContract> medicalHouseContracts) {
        this.medicalHouseContracts = medicalHouseContracts;
    }

    @Override
    public Map<String, String[]> getHcPartyKeys() {
        return hcPartyKeys;
    }

    @Override
    public void setHcPartyKeys(Map<String, String[]> hcPartyKeys) {
        this.hcPartyKeys = hcPartyKeys;
    }

    @Override
    public String getPublicKey() {
        return publicKey;
    }

    @Override
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
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
        Patient other = (Patient) obj;

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

    public List<FinancialInstitutionInformation> getFinancialInstitutionInformation() {
        return financialInstitutionInformation;
    }

    public void setFinancialInstitutionInformation(List<FinancialInstitutionInformation> financialInstitutionInformation) {
        this.financialInstitutionInformation = financialInstitutionInformation;
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }

    @JsonIgnore
    public @Nullable String getFullName() {
        String full;
        full = lastName;
        if (firstName != null) {
            if (full != null) full = full + " " + firstName;
            else full = firstName;
        }
        return full;
    }

	@JsonIgnore
	public String getSignature() {
		return DigestUtils.md5Hex(
		""+this.firstName+":"+this.lastName+":"+this.getPatientHealthCareParties().stream().filter(PatientHealthCareParty::isReferral).findFirst().map(phcp->""+phcp.getHealthcarePartyId()+phcp.getReferralPeriods().last().getStartDate()+phcp.getReferralPeriods().last().getEndDate()).orElse("")
			+":"+this.dateOfBirth+":"+this.dateOfDeath+":"+this.getSsin());
	}

	public Patient solveConflictWith(Patient other) {
    	super.solveConflictsWith(other);
	    this.mergeFrom(other);

	    return this;
    }

	public void mergeFrom(Patient other) {
		if (this.firstName == null && other.firstName != null) { this.firstName = other.firstName; }
		if (this.lastName == null && other.lastName != null) { this.lastName = other.lastName; }
		if (this.ssin == null && other.ssin != null) { this.ssin = other.ssin; }
		if (this.civility == null && other.civility != null) { this.civility = other.civility; }
		if (this.gender == null && other.gender != null && other.gender != Gender.unknown) { this.gender = other.gender; }
		if (this.maidenName == null && other.maidenName != null) { this.maidenName = other.maidenName; }
		if (this.spouseName == null && other.spouseName != null) { this.spouseName = other.spouseName; }
		if (this.partnerName == null && other.partnerName != null) { this.partnerName = other.partnerName; }
		if (this.personalStatus == null && other.personalStatus != null) { this.personalStatus = other.personalStatus; }
		if (this.dateOfBirth == null && other.dateOfBirth != null) { this.dateOfBirth = other.dateOfBirth; }
		if (this.dateOfDeath == null && other.dateOfDeath != null) { this.dateOfDeath = other.dateOfDeath; }
		if (this.placeOfBirth == null && other.placeOfBirth != null) { this.placeOfBirth = other.placeOfBirth; }
		if (this.placeOfDeath == null && other.placeOfDeath != null) { this.placeOfDeath = other.placeOfDeath; }
		if (this.education == null && other.education != null) { this.education = other.education; }
		if (this.profession == null && other.profession != null) { this.profession = other.profession; }
		if (this.note == null && other.note != null) { this.note = other.note; }
		if (this.nationality == null && other.nationality != null) { this.nationality = other.nationality; }
		if (this.picture == null && other.picture != null) { this.picture = other.picture; }
		if (this.externalId == null && other.externalId != null) { this.externalId = other.externalId; }
        if (this.comment != null && other.comment != null) {this.comment = other.comment;}

        if (this.alias == null && other.alias != null) { this.alias = other.alias; }
        if ((this.administrativeNote == null) || (this.administrativeNote.trim().equals("")) && other.administrativeNote != null) { this.administrativeNote = other.administrativeNote; }
        if (this.warning == null && other.warning != null) { this.warning = other.warning; }
        if (this.publicKey == null && other.publicKey != null) { this.publicKey = other.publicKey; }
        this.hcPartyKeys = MergeUtil.mergeMapsOfArraysDistinct(this.hcPartyKeys, other.hcPartyKeys, String::equals, (a, b)->a);

        this.languages = MergeUtil.mergeListsDistinct(this.languages,other.languages,String::equalsIgnoreCase,(a,b)->a);
        this.insurabilities = MergeUtil.mergeListsDistinct(this.insurabilities,other.insurabilities,
                (a,b)->(a==null&&b==null)||(a!=null&&b!=null&&Objects.equals(a.getInsuranceId(),b.getInsuranceId())&&Objects.equals(a.getStartDate(),b.getStartDate())),
                (a,b)->a.getEndDate()!=null?a:b
        );
        this.patientHealthCareParties = MergeUtil.mergeListsDistinct(this.patientHealthCareParties, other.patientHealthCareParties,
                (a,b)->(a==null&&b==null)||(a!=null&&b!=null&&Objects.equals(a.getHealthcarePartyId(),b.getHealthcarePartyId())&&Objects.equals(a.getType(),b.getType())),
                (PatientHealthCareParty a, PatientHealthCareParty b) -> {
                    a.setReferralPeriods(MergeUtil.mergeSets(a.getReferralPeriods(), b.getReferralPeriods(), new TreeSet<>(),
                            (aa,bb)->(aa==null&&bb==null)||(aa!=null&&bb!=null&&Objects.equals(aa.getStartDate(),bb.getStartDate())),
                            (aa,bb)->{
                                if (aa.getEndDate()==null) {aa.setEndDate(bb.getEndDate());}
                                return aa;
                            }
                    ));
                    return a;
                });
        this.patientProfessions = MergeUtil.mergeListsDistinct(this.patientProfessions,other.patientProfessions, Objects::equals, (a,b)->a);

        for (Address fromAddress:other.addresses) {
			Optional<Address> destAddress = this.getAddresses().stream().filter(address -> address.getAddressType() == fromAddress.getAddressType()).findAny();
			if (destAddress.isPresent()) {
				destAddress.orElseThrow(IllegalStateException::new).mergeFrom(fromAddress);
			} else {
				this.getAddresses().add(fromAddress);
			}
		}

		//insurabilities
        for(Insurability fromInsurability:other.insurabilities){
            Optional<Insurability> destInsurability = this.getInsurabilities().stream().filter(insurability -> insurability.getInsuranceId().equals(fromInsurability.getInsuranceId())).findAny();
            if(!destInsurability.isPresent()){
                this.getInsurabilities().add(fromInsurability);
            }
        }
        //Todo: cleanup insurabilities (enddates ...)

        //medicalhousecontracts
        for(MedicalHouseContract fromMedicalHouseContract:other.medicalHouseContracts){
            Optional<MedicalHouseContract> destMedicalHouseContract = this.getMedicalHouseContracts().stream().filter(medicalHouseContract -> medicalHouseContract.getMmNihii()!=null && medicalHouseContract.getMmNihii().equals(fromMedicalHouseContract.getMmNihii())).findAny();
            if(!destMedicalHouseContract.isPresent()){
                this.getMedicalHouseContracts().add(fromMedicalHouseContract);
            }
        }

        for (String fromLanguage:other.languages) {
            Optional<String> destLanguage = this.getLanguages().stream().filter(language -> language == fromLanguage).findAny();
            if (!destLanguage.isPresent()) {
                this.getLanguages().add(fromLanguage);
            }
        }

        for (Partnership fromPartnership:other.partnerships) {
            //Todo: check comparision:
            Optional<Partnership> destPartnership = this.getPartnerships().stream().filter(partnership -> partnership.getPartnerId() == fromPartnership.getPartnerId()).findAny();
            if (!destPartnership.isPresent()) {
                this.getPartnerships().add(fromPartnership);
            }
        }

        for (PatientHealthCareParty fromPatientHealthCareParty:other.patientHealthCareParties) {
            Optional<PatientHealthCareParty> destPatientHealthCareParty = this.getPatientHealthCareParties().stream().filter(patientHealthCareParty -> patientHealthCareParty.getHealthcarePartyId() == fromPatientHealthCareParty.getHealthcarePartyId()).findAny();
            if (!destPatientHealthCareParty.isPresent()) {
                this.getPatientHealthCareParties().add(fromPatientHealthCareParty);
            }
        }

        for (FinancialInstitutionInformation fromFinancialInstitutionInformation:other.financialInstitutionInformation) {
            Optional<FinancialInstitutionInformation> destFinancialInstitutionInformation = this.getFinancialInstitutionInformation().stream().filter(financialInstitutionInformation -> financialInstitutionInformation.getBankAccount() == fromFinancialInstitutionInformation.getBankAccount()).findAny();
            if (!destFinancialInstitutionInformation.isPresent()) {
                this.getFinancialInstitutionInformation().add(fromFinancialInstitutionInformation);
            }
        }

        for(SchoolingInfo fromSchoolingInfos:other.schoolingInfos){
            Optional<SchoolingInfo> destSchoolingInfos = this.getSchoolingInfos().stream().filter(schoolingInfos -> schoolingInfos.getStartDate() == fromSchoolingInfos.getStartDate()).findAny();
            if(!destSchoolingInfos.isPresent()){
                this.getSchoolingInfos().add(fromSchoolingInfos);
            }
        }

        for(EmploymentInfo fromEmploymentInfos:other.employementInfos){
            Optional<EmploymentInfo> destEmploymentInfos = this.getEmployementInfos().stream().filter(employmentInfos -> employmentInfos.getStartDate() == fromEmploymentInfos.getStartDate()).findAny();
            if(!destEmploymentInfos.isPresent()){
                this.getEmployementInfos().add(fromEmploymentInfos);
            }
        }

	}

	public void forceMergeFrom(Patient other) {
		if (other.firstName != null) { this.firstName = other.firstName; }
		if (other.lastName != null) { this.lastName = other.lastName; }
		if (other.ssin != null) { this.ssin = other.ssin; }
		if (other.civility != null) { this.civility = other.civility; }
		if (other.gender != null && other.gender != Gender.unknown) { this.gender = other.gender; }
		if (other.maidenName != null) { this.maidenName = other.maidenName; }
		if (other.spouseName != null) { this.spouseName = other.spouseName; }
		if (other.partnerName != null) { this.partnerName = other.partnerName; }
		if (other.personalStatus != null) { this.personalStatus = other.personalStatus; }
		if (other.dateOfBirth != null) { this.dateOfBirth = other.dateOfBirth; }
		if (other.dateOfDeath != null) { this.dateOfDeath = other.dateOfDeath; }
		if (other.placeOfBirth != null) { this.placeOfBirth = other.placeOfBirth; }
		if (other.placeOfDeath != null) { this.placeOfDeath = other.placeOfDeath; }
		if (other.education != null) { this.education = other.education; }
		if (other.profession != null) { this.profession = other.profession; }
		if (other.note != null) { this.note = other.note; }
		if (other.nationality != null) { this.nationality = other.nationality; }
		if (other.picture != null) { this.picture = other.picture; }
		if (other.externalId != null) { this.externalId = other.externalId; }
		if (other.comment != null) {this.comment = other.comment;}

		this.forceMergeAddresses(other.getAddresses());
	}

	public void forceMergeAddresses(List<Address> otherAddresses) {
		for (Address fromAddress : otherAddresses) {
			Optional<Address> destAddress = this.getAddresses().stream().filter(address -> address.getAddressType() == fromAddress.getAddressType()).findAny();
			if (destAddress.isPresent()) {
				destAddress.orElseThrow(IllegalStateException::new).forceMergeFrom(fromAddress);
			} else {
				this.getAddresses().add(fromAddress);
			}
		}
	}

    public String getAdministrativeNote() {
        return administrativeNote;
    }

    public void setAdministrativeNote(String administrativeNote) {
        this.administrativeNote = administrativeNote;
    }

    public DeactivationReason getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(DeactivationReason deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    public Set<String> getEncryptedAdministrativesDocuments() {
        return encryptedAdministrativesDocuments;
    }

    public void setEncryptedAdministrativesDocuments(Set<String> encryptedAdministrativesDocuments) {
        this.encryptedAdministrativesDocuments = encryptedAdministrativesDocuments;
    }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public CodeStub getFatherBirthCountry() { return fatherBirthCountry; }

    public void setFatherBirthCountry(CodeStub fatherBirthCountry) { this.fatherBirthCountry = fatherBirthCountry; }

    public CodeStub getBirthCountry() { return birthCountry; }

    public void setBirthCountry(CodeStub birthCountry) { this.birthCountry = birthCountry; }

    public CodeStub getNativeCountry() { return nativeCountry; }

    public void setNativeCountry(CodeStub nativeCountry) { this.nativeCountry = nativeCountry; }

    public CodeStub getSocialStatus() { return socialStatus; }

    public void setSocialStatus(CodeStub socialStatus) { this.socialStatus = socialStatus; }

    public CodeStub getMainSourceOfIncome() { return mainSourceOfIncome; }

    public void setMainSourceOfIncome(CodeStub mainSourceOfIncome) { this.mainSourceOfIncome = mainSourceOfIncome; }

    public List<SchoolingInfo> getSchoolingInfos() { return schoolingInfos; }

    public void setSchoolingInfos(List<SchoolingInfo> schoolingInfos) { this.schoolingInfos = schoolingInfos; }

    public List<EmploymentInfo> getEmployementInfos() { return employementInfos; }

    public void setEmployementInfos(List<EmploymentInfo> employementInfos) { this.employementInfos = employementInfos; }

    public Set<Property> getProperties() { return properties; }

    public void setProperties(Set<Property> properties) { this.properties = properties; }
}
