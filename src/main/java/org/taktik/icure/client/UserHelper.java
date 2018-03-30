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


import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto;
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList;
import org.taktik.icure.services.external.rest.v1.dto.UserDto;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;

public class UserHelper {
	private static final Logger log = LoggerFactory.getLogger(UserHelper.class);

	private ICureHelper client;

	public UserHelper(ICureHelper iCureHelper) {
		this.client = iCureHelper;
	}

	/**
	 * Listing users. If you want to have a pagination strategy of 10 users
	 * per page. You have to set the limit as 10. Then, the 10-th user will
	 * be your next startKey and startKeyDocId for the next 10 rows, and so on.
	 *
	 * @param startKey is the email of the users. It can be null.
	 * @param startKeyDocId is the ID of the users' document. It can be null.
	 * @param limit is the number of users.
	 * @return PaginatedList list of users.
	 */
	public PaginatedList list(String startKey, String startKeyDocId, String limit) throws IOException {
		String getMethod = "user/";
		if (startKey != null) { getMethod +=  (getMethod.equals("user/")?"?":"&") + "startKey=" + URLEncoder.encode(startKey, "UTF-8"); }
		if (startKeyDocId != null) { getMethod += (getMethod.equals("user/")?"?":"&") + "startKeyDocId=" + URLEncoder.encode(startKeyDocId, "UTF-8"); }
		if (limit != null) { getMethod += (getMethod.equals("user/")?"?":"&") + "limit=" + URLEncoder.encode(limit, "UTF-8"); }

		String response = client.doRestGET(getMethod);

		Type userListType = new TypeToken<PaginatedList<UserDto>>() {}.getType();
		return client.getGson().fromJson(response, userListType);
	}

	public UserDto create(UserDto user) throws IOException {
		String response = client.doRestPOST("user", user);
		return client.getGson().fromJson(response, UserDto.class);
	}

	public UserDto get(String id) throws IOException {
		String response = client.doRestGET("user/" + id);
		return client.getGson().fromJson(response, UserDto.class);
	}

	public UserDto getByEmail(String email) throws IOException {
		String response = client.doRestGET("user/byEmail/" + email);
		return client.getGson().fromJson(response, UserDto.class);
	}

	public UserDto modify(UserDto user) throws IOException {
		String response = client.doRestPOST("user/modify", user);
		return client.getGson().fromJson(response, UserDto.class);
	}

	public String delete(String id) throws IOException {
		return client.doRestPOST("user/delete/" + id, null);
	}

	public UserDto assignHealthcareParty(String healthcarePartyId) throws IOException {
		String response = client.doRestPUT("user/hcparty/" + healthcarePartyId, null);
		return client.getGson().fromJson(response, UserDto.class);
	}
}
