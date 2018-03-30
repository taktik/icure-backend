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

package org.taktik.icure.client;

import com.google.common.base.Joiner;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taktik.icure.exceptions.EncryptionException;
import org.taktik.icure.exceptions.ICureException;
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList;
import org.taktik.icure.services.external.rest.v1.dto.PatientDto;
import org.taktik.icure.services.external.rest.v1.dto.PatientPaginatedList;
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class PatientHelper {
	private static final Logger log = LoggerFactory.getLogger(PatientHelper.class);

	private ICureHelper client;

	public PatientHelper(ICureHelper iCureHelper) {
		this.client = iCureHelper;
	}

	/**
	 *
	 * @param startKeyList is a Serializable (ArrayList here) which should be the [HcPartId, PatientLastName]. It can be null.
	 * @param startKeyDocId is the ID of the patient document. It can be null.
	 * @param limit is the number of patients.
	 * @return list of patients containing next start key.
	 */
	public PaginatedList list(ArrayList startKeyList, String startKeyDocId, String limit) throws IOException {
		String startKeys = null;
		if (startKeyList != null) {
			startKeys = Joiner.on(",").join(startKeyList);
		}

		String getMethod = "patient";
		if (startKeyList != null) { getMethod +=  (getMethod.equals("patient")?"?":"&") + "startKey=" + URLEncoder.encode(startKeys, "UTF-8"); }
		if (startKeyDocId != null) { getMethod += (getMethod.equals("patient")?"?":"&") + "startKeyDocId=" + URLEncoder.encode(startKeyDocId, "UTF-8"); }
		if (limit != null) { getMethod += (getMethod.equals("patient")?"?":"&") + "limit=" + URLEncoder.encode(limit, "UTF-8"); }

		String response = client.doRestGET(getMethod);

		Type patientListType = new TypeToken<PaginatedList<PatientDto>>() {}.getType();
		return client.getGson().fromJson(response, patientListType);
	}

	/**
	 *
	 * @param startKeyList is a Serializable (ArrayList here) which should be the [HcPartId, PatientLastName]. It can be null.
	 * @param startKeyDocId is the ID of the patient document. It can be null.
	 * @param limit is the number of patients.
	 * @param filter
	 * @return list of patients containing next start key.
	 */
	public PaginatedList filterBy(ArrayList startKeyList, String startKeyDocId, String limit, Filter filter) throws IOException {
		String startKeys = null;
		if (startKeyList != null) {
			startKeys = Joiner.on(",").join(startKeyList);
		}

		String postMethod = "patient/filter";
		if (startKeyList != null) { postMethod +=  (postMethod.equals("patient/filter")?"?":"&") + "startKey=" + URLEncoder.encode(startKeys, "UTF-8"); }
		if (startKeyDocId != null) { postMethod += (postMethod.equals("patient/filter")?"?":"&") + "startKeyDocId=" + URLEncoder.encode(startKeyDocId, "UTF-8"); }
		if (limit != null) { postMethod += (postMethod.equals("patient/filter")?"?":"&") + "limit=" + URLEncoder.encode(limit, "UTF-8"); }

		String response = client.doRestPOST(postMethod, filter);

		Type patientListType = new TypeToken<PaginatedList<PatientDto>>() {}.getType();
		return client.getGson().fromJson(response, patientListType);
	}

	public PatientDto create(PatientDto patient, String ownerId) throws IOException, ExecutionException, EncryptionException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
		// create a patient
		String responseCreatedPatient = client.doRestPOST("patient", patient);
		PatientDto createdPatient = client.getGson().fromJson(responseCreatedPatient, PatientDto.class);

		if (createdPatient == null || createdPatient.getId() == null) {
			log.error("Patient creation failed");
			return null;
		}

		// create a self delegation
		return client.initObjectDelegations(createdPatient, null, ownerId, "patient/{id}/delegate", null);
	}

	public PatientDto get(String id) throws IOException {
		String response = client.doRestGET("patient/" + id);
		return client.getGson().fromJson(response, PatientDto.class);
	}

	public PatientDto modify(PatientDto patient) throws IOException {
		String response = client.doRestPOST("patient/modify", patient);
		return client.getGson().fromJson(response, PatientDto.class);
	}

	public Set<String> delete(Set<String> ids) throws IOException {
		String response = client.doRestPOST("patient/delete", ids);
		return client.getGson().<Set<String>>fromJson(response, Set.class);
	}

	public PatientDto newDelegation(String patientId, String owner, String delegate) throws IOException, ExecutionException, ICureException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
		PatientDto fetchedPatient = get(patientId);
		if (fetchedPatient != null) {
			return newDelegation(fetchedPatient, owner, delegate);
		} else {
			log.error("newDelegation failed: fetching patient (" + patientId + ") has been failed.");
			return null;
		}
	}

	public PatientDto newDelegation(PatientDto modifiedPatient, String owner, String delegate) throws IOException, ExecutionException, ICureException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
		if (modifiedPatient.getId() != null) {
			// Fetching the SKD (i.e. Secret Key Document, the key in Delegation which is encrypted by
			// exchange key of owner and delegate) of the owner to pass in appendObjectDelegations method
			// It needs the SKD in order to decrypt and obtain the previously generated key for creation of new
			// delegation.
/*			List<DelegationDto> patientOwnerDelegations = modifiedPatient.getDelegations().get(owner);
			String ownerCryptedDelegationSkd = null;
			if (patientOwnerDelegations.size() > 0) {
				ownerCryptedDelegationSkd = patientOwnerDelegations.get(0).getKey();
			}*/
			return client.appendObjectDelegations(modifiedPatient, null, owner, delegate , "patient/delegate/", null, null);
		} else {
			log.error("newDelegation failed: input patient has no Id.");
			return null;
		}
	}
}
