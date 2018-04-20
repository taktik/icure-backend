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

package org.taktik.icure.client;

import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taktik.icure.exceptions.EncryptionException;
import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto;
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList;
import org.taktik.icure.services.external.rest.v1.dto.PatientDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HealthcarePartyHelper {
	private static final Logger log = LoggerFactory.getLogger(HealthcarePartyHelper.class);

	private ICureHelper client;

	public HealthcarePartyHelper(ICureHelper iCureHelper) {
		this.client = iCureHelper;
	}


	/**
	 * Listing healthcare parties. If you want to have a pagination strategy of 10 healthcare
	 * parties per page. You have to set the limit as 10. Then, the 10-th healthcare party will
	 * be your next startKey and startKeyDocId for the next 10 rows, and so on.
	 *
	 * @param startKey is the last name of the healthcare parties. It can be null.
	 * @param startKeyDocId is the ID of the healthcare parties document. It can be null.
	 * @param limit is the number of healthcare parties.
	 * @return list of patients.
	 */
	public PaginatedList list(String startKey, String startKeyDocId, String limit) throws IOException {
		String getMethod = "hcparty/";
		if (startKey != null) { getMethod +=  (getMethod.equals("hcparty/")?"?":"&") + "startKey=" + URLEncoder.encode(startKey, "UTF-8"); }
		if (startKeyDocId != null) { getMethod += (getMethod.equals("hcparty/")?"?":"&") + "startKeyDocId=" + URLEncoder.encode(startKeyDocId, "UTF-8"); }
		if (limit != null) { getMethod += (getMethod.equals("hcparty/")?"?":"&") + "limit=" + URLEncoder.encode(limit, "UTF-8"); }

		String response = client.doRestGET(getMethod);

		Type hcPartyListType = new TypeToken<PaginatedList<HealthcarePartyDto>>() {}.getType();
		return client.getGson().fromJson(response, hcPartyListType);
	}

	public HealthcarePartyDto create(HealthcarePartyDto healthcareParty) throws IOException {
		String response = client.doRestPOST("hcparty", healthcareParty);
		return client.getGson().fromJson(response, HealthcarePartyDto.class);
	}

	public HealthcarePartyDto get(String id) throws IOException {
		String response = client.doRestGET("hcparty/" + id);
		return client.getGson().fromJson(response, HealthcarePartyDto.class);
	}

	public HealthcarePartyDto modify(HealthcarePartyDto healthcareParty) throws IOException {
		String response = client.doRestPOST("hcparty/modify", healthcareParty);
		return client.getGson().fromJson(response, HealthcarePartyDto.class);
	}

	public String delete(String id) throws IOException {
		return client.doRestPOST("hcparty/delete/" + id, null);
	}

	public Map<String, String[]> updateHcPartyKeys(Map<String, String[]> newHcParyKeyMap) {
		//TODO
		return null;
	}

	/**
	 * Normally current healthcare party is the delegateHcParty. But, sometimes it can be the owner as well.
	 *
	 * @param patient
	 * @param delegateHcPartyId
	 * @return It returns secret generated keys located in delegation list of delegate HcParty. Those key
	 * 			get decrypted by owner-delegate AES exchange key and get returned as a List of Array. This
	 * 			list could be useful for in 'findBy' functions within Contacts.
	 * @throws IOException
	 * @throws EncryptionException
	 */
	public List<String> getSecretForeignKeys(PatientDto patient,  String delegateHcPartyId) throws IOException, EncryptionException {
		List<String> result = new ArrayList<>();
		List<DelegationDto> delegations = patient.getDelegations().get(delegateHcPartyId);

		if (delegations == null  || delegations.size() == 0) {
			return result;
		}

		for (DelegationDto d : delegations) {
			result.addAll(client.getSecretKeys(patient, d.getOwner(), delegateHcPartyId));
		}

		return result;
	}
}
