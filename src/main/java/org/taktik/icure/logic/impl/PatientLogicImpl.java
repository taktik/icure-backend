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

package org.taktik.icure.logic.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.dao.Option;
import org.taktik.icure.dao.PatientDAO;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.db.PaginatedDocumentKeyIdPair;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.db.Sorting;
import org.taktik.icure.dto.filter.chain.FilterChain;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.User;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.entities.embed.Gender;
import org.taktik.icure.entities.embed.PatientHealthCareParty;
import org.taktik.icure.entities.embed.ReferralPeriod;
import org.taktik.icure.exceptions.DocumentNotFoundException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.logic.UserLogic;
import org.taktik.icure.logic.impl.filter.Filters;
import org.taktik.icure.services.external.rest.v1.dto.PatientDto;
import org.taktik.icure.utils.FuzzyValues;
import org.taktik.icure.validation.aspect.Check;

import javax.security.auth.login.LoginException;
import javax.validation.constraints.NotNull;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.taktik.icure.db.StringUtils.safeConcat;
import static org.taktik.icure.db.StringUtils.sanitizeString;

@org.springframework.stereotype.Service
public class PatientLogicImpl extends GenericLogicImpl<Patient, PatientDAO> implements PatientLogic {
	private static final Logger log = LoggerFactory.getLogger(PatientLogicImpl.class);

	private PatientDAO patientDAO;
	private UUIDGenerator uuidGenerator;
	private MapperFacade mapper;
	private UserLogic userLogic;
	private org.taktik.icure.logic.impl.filter.Filters filters;

	public void setUuidGenerator(UUIDGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}

	@Override
    protected PatientDAO getGenericDAO() {
        return patientDAO;
    }

    @Autowired
    public void setPatientDAO(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

	@Override
	public Integer countByHcParty(String healthcarePartyId) {
		return patientDAO.countByHcParty(healthcarePartyId);
	}

	@Override
	public Integer countOfHcParty(String healthcarePartyId) {
		return patientDAO.countOfHcParty(healthcarePartyId);
	}

	@Override
	public List<String> listByHcPartyIdsOnly(String healthcarePartyId) {
		return patientDAO.listIdsByHcParty(healthcarePartyId);
	}

	@Override
    public List<String> listByHcPartyAndSsinIdsOnly(String ssin, String healthcarePartyId) {
        return patientDAO.listIdsByHcPartyAndSsin(ssin, healthcarePartyId);
    }

	@Override
	public List<String> listByHcPartyAndSsinsIdsOnly(Collection<String> ssins, String healthcarePartyId) {
		return patientDAO.listIdsByHcPartyAndSsins(ssins, healthcarePartyId);
	}

	@Override
    public List<String> listByHcPartyDateOfBirthIdsOnly(Integer date, String healthcarePartyId) {
        return patientDAO.listIdsByHcPartyAndDateOfBirth(date, healthcarePartyId);
    }

    @Override
    public List<String> listByHcPartyGenderEducationProfessionIdsOnly(String healthcarePartyId, Gender gender, String education, String profession) {
        return patientDAO.listIdsByHcPartyGenderEducationProfession(healthcarePartyId, gender, education, profession);
    }

    @Override
	public List<String> listByHcPartyDateOfBirthIdsOnly(Integer startDate, Integer endDate, String healthcarePartyId) {
		return patientDAO.listIdsByHcPartyAndDateOfBirth(startDate, endDate, healthcarePartyId);
	}

	@Override
    public List<String> listByHcPartyNameContainsFuzzyIdsOnly(String searchString, String healthcarePartyId) {
        return patientDAO.listIdsByHcPartyAndNameContainsFuzzy(searchString, healthcarePartyId, 1000);
    }

    @Override
    public List<String> listByHcPartyName(String searchString, String healthcarePartyId) {
        return patientDAO.listByHcPartyName(searchString, healthcarePartyId);
    }

    @Override
	public List<String> listByHcPartyAndExternalIdsOnly(String externalId, String healthcarePartyId) {
		return patientDAO.listIdsByHcPartyAndExternalId(externalId, healthcarePartyId);
	}

	@Override
	public List<String> listByHcPartyAndActiveIdsOnly(boolean active, String healthcarePartyId) {
		return patientDAO.listIdsByActive(active, healthcarePartyId);
	}

	@Override
	public List<Patient> listOfMergesAfter(Long date) {
		return patientDAO.listOfMergesAfter(date);
	}

	@Override
	public PaginatedList<String> findByHcPartyIdsOnly(String healthcarePartyId, PaginationOffset offset) {
		return patientDAO.findIdsByHcParty(healthcarePartyId, offset);
	}

	@Override
    public PaginatedList<Patient> findByHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(String healthcarePartyId, PaginationOffset offset, String searchString, Sorting sorting) {
	    PaginatedList<Patient> patientsPaginatedList;

	    boolean descending = "desc".equals(sorting.getDirection());

	    if (searchString == null || searchString.isEmpty()) {
		    if ("ssin".equals(sorting.getField())) {
			    patientsPaginatedList = patientDAO.findPatientsByHcPartyAndSsin(null, healthcarePartyId, offset, descending);
		    } else if ("dateOfBirth".equals(sorting.getField())) {
			    patientsPaginatedList = patientDAO.findPatientsByHcPartyDateOfBirth(null, null, healthcarePartyId, offset, descending);
		    } else {
				patientsPaginatedList = patientDAO.findPatientsByHcPartyAndName(null, healthcarePartyId, offset, descending);
			}

	    } else {
		    if (FuzzyValues.isSsin(searchString)) {
			    patientsPaginatedList = patientDAO.findPatientsByHcPartyAndSsin(searchString, healthcarePartyId, offset, false);
		    } else if (FuzzyValues.isDate(searchString)) {
			    patientsPaginatedList = patientDAO.findPatientsByHcPartyDateOfBirth(FuzzyValues.toYYYYMMDD(searchString), FuzzyValues.getMaxRangeOf(searchString), healthcarePartyId, offset, false);
		    } else {
				patientsPaginatedList = findByHcPartyNameContainsFuzzy(searchString, healthcarePartyId, offset, descending);
		    }
	    }

	    return patientsPaginatedList;
	}

	@Override
	public PaginatedList<Patient> listPatients(PaginationOffset paginationOffset, FilterChain<Patient> filterChain, String sort, Boolean desc) throws LoginException {
		SortedSet<String> ids = new TreeSet<>(filters.resolve(filterChain.getFilter()));
		if (filterChain.getPredicate()!=null || (sort != null && !sort.equals("id"))) {
			List<Patient> patients = this.getPatients(new ArrayList<>(ids));
			if (filterChain.getPredicate() != null) { patients =  patients.stream().filter(filterChain.getPredicate()::apply).collect(Collectors.toList()); }
			PropertyUtilsBean pub = new PropertyUtilsBean();

			patients.sort((a, b) -> {
				try {
					Comparable ap = (Comparable) pub.getProperty(a, sort != null ? sort : "id");
					Comparable bp = (Comparable) pub.getProperty(b, sort != null ? sort : "id");
					return (ap instanceof String && bp instanceof String) ?
							(desc!=null && desc ? StringUtils.compareIgnoreCase((String)bp, (String)ap) : StringUtils.compareIgnoreCase((String)ap, (String)bp)) :
							(desc!=null && desc ? ObjectUtils.compare(bp, ap) : ObjectUtils.compare(ap, bp));
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {}
				return 0;
			});

			int firstIndex = paginationOffset != null && paginationOffset.getStartDocumentId() != null ? patients.stream().map(StoredDocument::getId).collect(Collectors.toList()).indexOf(paginationOffset.getStartDocumentId()) : 0;
			if (firstIndex==-1) {
				return new PaginatedList<>(0,ids.size(),new ArrayList<>(),null);
			} else {
				firstIndex+=paginationOffset != null && paginationOffset.getOffset()!=null?paginationOffset.getOffset():0;
				boolean hasNextPage = paginationOffset != null && paginationOffset.getLimit() != null && firstIndex+paginationOffset.getLimit() < patients.size();
				return hasNextPage ? new PaginatedList<>(paginationOffset.getLimit(), patients.size(), patients.subList(firstIndex, firstIndex+paginationOffset.getLimit()),
					new PaginatedDocumentKeyIdPair(null, patients.get(firstIndex+paginationOffset.getLimit()).getId())) :
					new PaginatedList<>(patients.size()-firstIndex, patients.size(), patients.subList(firstIndex, patients.size()),null);
			}
		} else {
			if (desc != null && desc) { ids = ((TreeSet<String>) ids).descendingSet(); }
			if (paginationOffset != null && paginationOffset.getStartDocumentId() != null) {
				ids = ids.subSet(paginationOffset.getStartDocumentId(), ((TreeSet) ids).last() + "\0");
			}
			List<String> idsList = new ArrayList<>(ids);
			if (paginationOffset != null && paginationOffset.getOffset() != null) {
				idsList = idsList.subList(paginationOffset.getOffset(),idsList.size());
			}
			boolean hasNextPage = paginationOffset != null && paginationOffset.getLimit() != null && paginationOffset.getLimit()<idsList.size();
			if (hasNextPage) {
				idsList = idsList.subList(0,paginationOffset.getLimit()+1);
			}
			List<Patient> patients = this.getPatients(idsList);
			return new PaginatedList<>(hasNextPage ? paginationOffset.getLimit() : patients.size(), ids.size(), hasNextPage ? patients.subList(0,paginationOffset.getLimit()) : patients, hasNextPage ? new PaginatedDocumentKeyIdPair(null, patients.get(patients.size()-1).getId()) : null);
		}
	}

	@Override
	public PaginatedList<Patient> findOfHcPartyAndSsinOrDateOfBirthOrNameContainsFuzzy(String healthcarePartyId, PaginationOffset offset, String searchString, Sorting sorting) {
		PaginatedList<Patient> patientsPaginatedList;

		boolean descending = "desc".equals(sorting.getDirection());

		if (searchString == null || searchString.isEmpty()) {
			if ("ssin".equals(sorting.getField())) {
				patientsPaginatedList = patientDAO.findPatientsOfHcPartyAndSsin(null, healthcarePartyId, offset, descending);

			} else if ("dateOfBirth".equals(sorting.getField())) {
				patientsPaginatedList = patientDAO.findPatientsOfHcPartyDateOfBirth(null, null, healthcarePartyId, offset, descending);
			} else {
				patientsPaginatedList = patientDAO.findPatientsOfHcPartyAndName(null, healthcarePartyId, offset, descending);
			}

		} else {
			if (FuzzyValues.isSsin(searchString)) {
				patientsPaginatedList = patientDAO.findPatientsOfHcPartyAndSsin(searchString, healthcarePartyId, offset, false);

			} else if (FuzzyValues.isDate(searchString)) {
				patientsPaginatedList = patientDAO.findPatientsOfHcPartyDateOfBirth(FuzzyValues.toYYYYMMDD(searchString),
						FuzzyValues.getMaxRangeOf(searchString), healthcarePartyId, offset, false);

			} else {
				patientsPaginatedList = findOfHcPartyNameContainsFuzzy(searchString, healthcarePartyId, offset, descending);
			}
		}

		return patientsPaginatedList;
	}

	@Override
    public PaginatedList<Patient> findByHcPartyAndSsin(String ssin, String healthcarePartyId, PaginationOffset paginationOffset) {
        return patientDAO.findPatientsByHcPartyAndSsin(ssin, healthcarePartyId, paginationOffset, false);
    }

	@Override
    public PaginatedList<Patient> findByHcPartyDateOfBirth(Integer date, String healthcarePartyId, PaginationOffset paginationOffset) {
        return patientDAO.findPatientsByHcPartyDateOfBirth(date, date , healthcarePartyId, paginationOffset, false);
    }

	@Override
	public PaginatedList<Patient> findByHcPartyModificationDate(Long start, Long end, String healthcarePartyId, boolean descending, PaginationOffset paginationOffset) {
		return patientDAO.findPatientsByHcPartyModificationDate(start, end , healthcarePartyId, paginationOffset, descending);
	}

	@Override
	public PaginatedList<Patient> findOfHcPartyModificationDate(Long start, Long end, String healthcarePartyId, boolean descending, PaginationOffset paginationOffset) {
		return patientDAO.findPatientsOfHcPartyModificationDate(start, end , healthcarePartyId, paginationOffset, descending);
	}

	private Comparator<Patient> getPatientComparator(String sanitizedSearchString, boolean descending) {
		return (a, b) -> {
			if (a==null && b==null) { return 0; }
			if (a==null) { return -1; }
			if (b==null) { return  1; }

			int res = ObjectUtils.compare(sanitizeString(safeConcat(a.getLastName(), a.getFirstName())).startsWith(sanitizedSearchString)?0:1,
					sanitizeString(safeConcat(b.getLastName(), b.getFirstName())).startsWith(sanitizedSearchString)?0:1);
			if (res != 0) return res*(descending ? -1 : 1);
			res = ObjectUtils.compare(sanitizeString(a.getLastName()),sanitizeString(b.getLastName()));
			if (res != 0) return res*(descending ? -1 : 1);
			res = ObjectUtils.compare(sanitizeString(a.getFirstName()),sanitizeString(b.getFirstName()));
			return res*(descending ? -1 : 1);
		};
	}

	@Override
    public PaginatedList<Patient> findByHcPartyNameContainsFuzzy(String searchString, String healthcarePartyId, PaginationOffset offset, boolean descending) {
		String sanSs = sanitizeString(searchString);

        //TODO return usefull data from the view like 3 first letters of names and date of birth that can be used to presort and reduce the number of items that have to be fully fetched
        //We will get partial results but at least we will not overload the servers
        Integer limit = offset.getStartKey() == null && offset.getLimit() != null ? Math.max(1000, offset.getLimit() * 10) : null;

        Set<String> ids = new HashSet<>(patientDAO.listIdsByHcPartyAndNameContainsFuzzy(searchString, healthcarePartyId, limit));
		List<Patient> patients = patientDAO.get(ids).stream().sorted(getPatientComparator(sanSs, descending)).collect(Collectors.toList());

		List<String> patientKeys = patients.stream().map(p -> sanitizeString(safeConcat(p.getLastName(), p.getFirstName()))).collect(Collectors.toList());
		return buildPatientPaginatedList(healthcarePartyId, offset, patients, patientKeys, descending);
	}

	@Override
	public PaginatedList<Patient> findOfHcPartyNameContainsFuzzy(String searchString, String healthcarePartyId, PaginationOffset offset, boolean descending) {
		String sanSs = sanitizeString(searchString);

		//TODO return usefull data from the view like 3 first letters of names and date of birth that can be used to presort and reduce the number of items that have to be fully fetched
		//We will get partial results but at least we will not overload the servers
        Integer limit = offset.getStartKey() == null && offset.getLimit() != null ? Math.max(1000, offset.getLimit() * 10) : null;

        Set<String> ids = new HashSet<>(patientDAO.listIdsOfHcPartyNameContainsFuzzy(searchString, healthcarePartyId, limit));
		List<Patient> patients = patientDAO.get(ids).stream().sorted(getPatientComparator(sanSs, descending)).collect(Collectors.toList());

		List<String> patientKeys = patients.stream().map(p -> sanitizeString(safeConcat(p.getLastName(), p.getFirstName()))).collect(Collectors.toList());
		return buildPatientPaginatedList(healthcarePartyId, offset, patients, patientKeys, descending);
	}

	private PaginatedList<Patient> buildPatientPaginatedList(String healthcarePartyId, PaginationOffset offset, List<Patient> patients, List<String> patientKeys, boolean descending) {
		if (offset.getStartKey() != null) {
			String safeKey = sanitizeString(((String) ((Object[]) offset.getStartKey())[1]));
			int idx = Collections.binarySearch(patientKeys, safeKey, (a, b) -> (descending?-1:1) * ObjectUtils.compare(a, b)); //Not sorted anymore... binary search won't work as is... Must be done in two parts
			if (idx<0) {
				idx = patientKeys.indexOf(safeKey);
			}

			if (idx<0) {
				//throw new IllegalArgumentException("Invalid start key");
				return new PaginatedList<>(offset.getLimit(), patients.size(), new ArrayList<>(),  null);
			}
			while(idx>0 && safeKey.equals(patientKeys.get(idx-1))) {
				idx--;
			}
			while(idx<patientKeys.size() && safeKey.equals(patientKeys.get(idx)) && offset.getStartDocumentId() != null && !offset.getStartDocumentId().equals(patients.get(idx).getId())) {
				idx++;
			}
			if (offset.getStartDocumentId() != null && !offset.getStartDocumentId().equals(patients.get(idx).getId())) {
				//throw new IllegalArgumentException("Invalid document id");
				return new PaginatedList<>(offset.getLimit(), patients.size(), new ArrayList<>(),  null);
			}
			int lastIdx = Math.min(patients.size(), idx + offset.getLimit());
			return new PaginatedList<>(offset.getLimit(), patients.size(), patients.subList(idx, lastIdx), lastIdx < patients.size() ? new PaginatedDocumentKeyIdPair(Arrays.asList(healthcarePartyId, patientKeys.get(lastIdx)), patients.get(lastIdx).getId()) : null);
		} else {
			int lastIdx = Math.min(patients.size(), offset.getLimit());
			return new PaginatedList<>(offset.getLimit(), patients.size(), patients.subList(0, lastIdx), lastIdx < patients.size() ? new PaginatedDocumentKeyIdPair(Arrays.asList(healthcarePartyId, patientKeys.get(lastIdx)), patients.get(lastIdx).getId()) : null);
		}
	}

	@Override
	public Patient findByUserId(String id) {
		return patientDAO.findPatientsByUserId(id);
	}

	@Override
	public Patient getPatient(String patientId) {
		return patientDAO.get(patientId);
	}

	@Override
	public Map<String, Object> getPatientSummary(PatientDto patientDto, List<String> propertyExpressions) {

//		return patientDtoBeans.getAsMapOfValues(patientDto, propertyExpressions);
		return null;
	}

	@Override
    public List<Patient> getPatients(List<String> patientIds) {
		if (patientIds == null || patientIds.isEmpty()) { return new ArrayList<>(); }
        return  patientDAO.get(patientIds);
    }

    @Override
	public Patient addDelegation(String patientId, Delegation delegation) {
		Patient patient = getPatient(patientId);
		patient.addDelegation(delegation.getDelegatedTo(), delegation);
		return patientDAO.save(patient);
	}

	@Override
	public Patient addDelegations(String patientId, Collection<Delegation> delegations) {
		Patient patient = getPatient(patientId);

		delegations.forEach(d->patient.addDelegation(d.getDelegatedTo(),d));

		return patientDAO.save(patient);
	}

	@Override
	public Patient createPatient(@Check @NotNull Patient patient) throws MissingRequirementsException {
		// checking requirements
		if (patient.getPreferredUserId() != null && (patient.getDelegations() == null || patient.getDelegations().size() == 0)) {
			patient.setDelegations(new HashMap<>());
			User user = userLogic.getUser(patient.getPreferredUserId());

			if (user==null) {
				patient.getDelegations().put(user.getHealthcarePartyId(), new HashSet<>());
				user.getAutoDelegations().values().forEach(ads -> ads.forEach(ad -> patient.getDelegations().put(ad, new HashSet<>())));
			}
		}

		List<Patient> createdPatients = new ArrayList<>(1);
		try {
			createEntities(Collections.singleton(patient), createdPatients);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid patient", e);
		}

		Patient createdPatient = createdPatients.get(0);
		if (createdPatients.size()>0) {
			logPatient(createdPatient, "patient.create.");
		}
		return createdPatients.size() == 0 ? null: createdPatient;
	}

	@Override
	public Patient modifyPatient(@Check @NotNull Patient patient) throws MissingRequirementsException {
		log.debug("Modifying patient with id:"+patient.getId());
		// checking requirements
		if ((patient.getFirstName() == null || patient.getLastName() == null) && patient.getEncryptedSelf() == null) {
			throw new MissingRequirementsException("modifyPatient: Name, Last name  are required.");
		}

		try {
			updateEntities(Collections.singleton(patient));
			Patient modifiedPatient = getPatient(patient.getId());

			if (modifiedPatient!=null) {
				logPatient(modifiedPatient, "patient.modify.");
			}
			return modifiedPatient;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid patient", e);
		}
	}

	@Override
	public void logAllPatients(String hcPartyId) {
		Lists.partition(patientDAO.listIdsByHcParty(hcPartyId),100).forEach(ids->getPatients(ids).forEach(p->logPatient(p,"patient.init."+p.getId()+".")));
	}

	private void logPatient(Patient modifiedPatient, String prefix) {
		File dir = new File("/Library/Application Support/iCure/Patients");
		if (dir.exists() && dir.isDirectory()) {
            XStream xs = new XStream();
            File file = new File(dir, prefix + System.currentTimeMillis() + ".xml");
            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                xs.toXML(modifiedPatient, out);
                out.close();
            } catch (java.io.IOException ignored) {
                //
            }
        }
	}


	@Override
	public Patient modifyPatientReferral(@NotNull Patient patient, String referralId, Instant start, Instant end) throws MissingRequirementsException {
		final Instant startOrNow = start == null ? Instant.now() : start;
		final boolean[] shouldSave = {false};

		//Close referrals relative to other healthcare parties
		patient.getPatientHealthCareParties().stream().filter(phcp -> phcp.isReferral() && (referralId == null || referralId.equals(phcp.getHealthcarePartyId()))).forEach(phcp -> {
			phcp.setReferral(false); shouldSave[0] = true;
			phcp.getReferralPeriods().forEach((p) -> {
				if (p.getEndDate() == null || !p.getEndDate().equals(startOrNow)) {
					p.setEndDate(startOrNow);
				}
			});
		});
		if (referralId != null) {
			PatientHealthCareParty patientHealthCareParty = patient.getPatientHealthCareParties().stream().filter(phcp -> referralId.equals(phcp.getHealthcarePartyId())).findFirst().orElse(null);
			if (patientHealthCareParty != null) {
				if (!patientHealthCareParty.isReferral()) { patientHealthCareParty.setReferral(true); shouldSave[0] = true; }
				patientHealthCareParty.getReferralPeriods().stream().filter(rp-> Objects.equals(start, rp.getStartDate())).findFirst().ifPresent(rp -> {
					if (!Objects.equals(end, rp.getEndDate())) { rp.setEndDate(end); shouldSave[0] = true; }
				});
			} else {
				PatientHealthCareParty newRefPer = new PatientHealthCareParty();

				newRefPer.setHealthcarePartyId(referralId);
				newRefPer.setReferral(true);
				newRefPer.getReferralPeriods().add(new ReferralPeriod(startOrNow, end));

				patient.getPatientHealthCareParties().add(newRefPer);

				shouldSave[0] = true;
			}
		}
		return shouldSave[0] ? modifyPatient(patient) : patient;
	}

	@Override
	public Patient mergePatient(Patient patient, List<Patient> fromPatients) {
		for (Patient from:fromPatients) {
			Set<Map.Entry<String, Set<Delegation>>> entries = from.getDelegations().entrySet();
			for (Map.Entry<String, Set<Delegation>> entry : entries) {
				Set<Delegation> secondMapValue = patient.getDelegations().get(entry.getKey());
				if (secondMapValue == null) {
					patient.getDelegations().put(entry.getKey(), entry.getValue());
				} else {
					secondMapValue.addAll(entry.getValue());
				}
			}

			from.setMergeToPatientId(patient.getId());
			from.setDeletionDate(Instant.now().toEpochMilli());

			patient.mergeFrom(from);
			try {
				modifyPatient(from);
			} catch (MissingRequirementsException e) {
				throw new IllegalStateException(e);
			}
		}

		patient.getMergedIds().addAll(fromPatients.stream().map(p->p.getId()).collect(Collectors.toList()));

		try {
			return modifyPatient(patient);
		} catch (MissingRequirementsException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Patient getByExternalId(String externalId) {
		return patientDAO.getByExternalId(externalId);
	}

	@Override
	public void solveConflicts() {
		List<Patient> patientsInConflict = patientDAO.listConflicts().stream().map(it -> patientDAO.get(it.getId(), Option.CONFLICTS)).collect(Collectors.toList());
		patientsInConflict.forEach(p-> {
			Arrays.stream(p.getConflicts()).map(c->patientDAO.get(p.getId(),c)).forEach(cp -> {
				p.solveConflictWith(cp);
				patientDAO.purge(cp);
			});
			patientDAO.save(p);
		});
	}

	@Override
	public Map<String, String> getHcPartyKeysForDelegate(String healthcarePartyId) {
		return patientDAO.getHcPartyKeysForDelegate(healthcarePartyId);
	}

	@Override
	public PaginatedList<Patient> listOfPatientsModifiedAfter(Long date, Long startKey, String startDocumentId, Integer limit) {
		return patientDAO.listOfPatientsModifiedAfter(date, new PaginationOffset<>(startKey, startDocumentId, 0, limit == null ? 1000 : limit ));
	}

	@Override
	public PaginatedList<Patient> getDuplicatePatientsBySsin(String healthcarePartyId, PaginationOffset paginationOffset) throws JsonProcessingException {
    	return this.patientDAO.getDuplicatePatientsBySsin(healthcarePartyId, paginationOffset);
  	}

  	@Override
	public PaginatedList<Patient> getDuplicatePatientsByName(String healthcarePartyId, PaginationOffset paginationOffset) throws JsonProcessingException {
		return this.patientDAO.getDuplicatePatientsByName(healthcarePartyId, paginationOffset);
	}

	@Override
	public List<Patient> fuzzySearchPatients(String healthcarePartyId, String firstName, String lastName, Integer dateOfBirth) {
		if (dateOfBirth != null) {
			//Patients with the right date of birth
			PaginatedList<Patient> patients = this.findByHcPartyDateOfBirth(dateOfBirth, healthcarePartyId, new PaginationOffset(1000));
			List<Patient> selection = patients.getRows();
			//Patients for which the date of birth is unknown
			if (firstName != null && lastName != null) {
				selection.addAll(this.findByHcPartyDateOfBirth(null, healthcarePartyId, new PaginationOffset()).getRows());
			}

			selection = selection.stream()
					.filter(p -> (firstName == null || p.getFirstName() == null || p.getFirstName().toLowerCase().startsWith(firstName.toLowerCase()) || firstName.toLowerCase().startsWith(p.getFirstName().toLowerCase()) || StringUtils.getLevenshteinDistance(firstName.toLowerCase(), p.getFirstName().toLowerCase())<=2))
					.filter(p -> (lastName == null || p.getLastName() == null || StringUtils.getLevenshteinDistance(lastName.toLowerCase(), p.getLastName().toLowerCase())<=2))
					.filter(p -> (p.getFirstName() != null && p.getFirstName().length()>=3) || (p.getLastName() != null && p.getLastName().length()>=3)).collect(Collectors.toList());

			return selection;
		} else if (lastName != null) {
			PaginatedList<Patient> patients = this.findByHcPartyNameContainsFuzzy(lastName.substring(0, Math.min(Math.max(lastName.length()-2,6),lastName.length())), healthcarePartyId, new PaginationOffset(1000), false);

			List<Patient> selection = patients.getRows();
			selection = selection.stream()
					.filter(p -> (firstName == null || p.getFirstName() == null || p.getFirstName().toLowerCase().startsWith(firstName.toLowerCase()) || firstName.toLowerCase().startsWith(p.getFirstName().toLowerCase()) || StringUtils.getLevenshteinDistance(firstName.toLowerCase(), p.getFirstName().toLowerCase())<=2))
					.filter(p -> (p.getLastName() == null || StringUtils.getLevenshteinDistance(lastName.toLowerCase(), p.getLastName().toLowerCase())<=2))
					.filter(p -> (p.getFirstName() != null && p.getFirstName().length()>=3) || (p.getLastName() != null && p.getLastName().length()>=3)).collect(Collectors.toList());

			return selection;
		}
		return new ArrayList<>();
	}

	@Override
	public Set<String> deletePatients(Set<String> ids) throws DocumentNotFoundException {
		try {
			deleteEntities(ids);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DocumentNotFoundException(e.getMessage() ,e);
		}
		return ids;
	}

	@Override
	public PaginatedList<Patient> findDeletedPatientsByDeleteDate(Long start, Long end, boolean descending, PaginationOffset paginationOffset) {
		return patientDAO.findDeletedPatientsByDeleteDate(start, end, descending, paginationOffset);
	}

	@Override
	public List<Patient> findDeletedPatientsByNames(String firstName, String lastName) {
		return patientDAO.findDeletedPatientsByNames(firstName, lastName);
	}

	@Override
	public Set<String> undeletePatients(Set<String> ids) throws DocumentNotFoundException {
		try {
			undeleteEntities(ids);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DocumentNotFoundException(e.getMessage() ,e);
		}
		return ids;
	}

	@Autowired
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}

	@Autowired
	public void setUserLogic(UserLogic userLogic) {
		this.userLogic = userLogic;
	}

	@Autowired
	public void setFilters(Filters filters) {
		this.filters = filters;
	}

}
