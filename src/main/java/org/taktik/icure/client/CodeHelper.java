/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.client;

import com.google.common.base.Preconditions;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taktik.icure.services.external.rest.v1.dto.CodeDto;
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList;


import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;

public class CodeHelper {
	private static final Logger log = LoggerFactory.getLogger(CodeHelper.class);

	private ICureHelper client;

	public CodeHelper(ICureHelper iCureHelper) {
		this.client = iCureHelper;
	}

	public CodeDto create(CodeDto code) throws IOException {
		String response = client.doRestPOST("code", code);
		return client.getGson().fromJson(response, CodeDto.class);
	}

	public CodeDto get(String id) throws IOException {
		String response = client.doRestGET("code/?codeId=" + URLEncoder.encode(id, "UTF-8"));
		return client.getGson().fromJson(response, CodeDto.class);
	}

	public CodeDto get(String type, String code, String version) throws IOException {
		Preconditions.checkNotNull(type, "Code field is null.");
		Preconditions.checkNotNull(code, "Type field is null.");
		Preconditions.checkNotNull(version, "Version code field is null.");

		return get(type + "|" + code + "|" + version);
	}

	public CodeDto modify(CodeDto code) throws IOException {
		String id = code.getType() + "|" + code.getCode() + "|" + code.getVersion();
		Preconditions.checkState(id.equals(code.getId()), "The modification of Code, Type, and version is not allowed.");

		String response = client.doRestPOST("code/modify", code);
		return client.getGson().fromJson(response, CodeDto.class);
	}

	public List<CodeDto> findCodes(String region, String type, String code, String version) throws IOException {
		String getMethod = "code/codes/";
		if (region != null) { getMethod +=  (getMethod.equals("code/codes/")?"?":"&") + "region=" + URLEncoder.encode(region, "UTF-8"); }
		if (type != null) { getMethod +=  (getMethod.equals("code/codes/")?"?":"&") + "type=" + URLEncoder.encode(type, "UTF-8"); }
		if (code != null) { getMethod +=  (getMethod.equals("code/codes/")?"?":"&") + "code=" + URLEncoder.encode(code, "UTF-8"); }
		if (version != null) { getMethod +=  (getMethod.equals("code/codes/")?"?":"&") + "version=" + URLEncoder.encode(version, "UTF-8"); }

		String response = client.doRestGET(getMethod);

		return client.getGson().<List<CodeDto>>fromJson(response, List.class);
	}

	public PaginatedList findPaginatedCodes(String startKeyRegion, String startKeyType, String startKeyCode, String startKeyVersion, String startKeyDocId, Integer limit) throws IOException {
		String getMethod = "code/codes/paginated";
		if (startKeyRegion != null) { getMethod +=  (getMethod.equals("code/codes/paginated")?"?":"&") + "startKeyRegion=" + URLEncoder.encode(startKeyRegion, "UTF-8"); }
		if (startKeyType != null) { getMethod +=  (getMethod.equals("code/codes/paginated")?"?":"&") + "startKeyType=" + URLEncoder.encode(startKeyType, "UTF-8"); }
		if (startKeyCode != null) { getMethod +=  (getMethod.equals("code/codes/paginated")?"?":"&") + "startKeyCode=" + URLEncoder.encode(startKeyCode, "UTF-8"); }
		if (startKeyVersion != null) { getMethod +=  (getMethod.equals("code/codes/paginated")?"?":"&") + "startKeyVersion=" + URLEncoder.encode(startKeyVersion, "UTF-8"); }
		if (startKeyDocId != null) { getMethod +=  (getMethod.equals("code/codes/paginated")?"?":"&") + "startKeyDocId=" + URLEncoder.encode(startKeyDocId, "UTF-8"); }
		if (limit != null) { getMethod += (getMethod.equals("code/codes/paginated")?"?":"&") + "limit=" + URLEncoder.encode(String.valueOf(limit), "UTF-8"); }

		String response = client.doRestGET(getMethod);

		Type codeListType = new TypeToken<PaginatedList<CodeDto>>() {}.getType();
		return client.getGson().fromJson(response, codeListType);
	}

	public List<String> findCodeTypes(String region, String type) throws IOException {
		String getMethod = "code/codetypes/";
		if (region != null) { getMethod +=  (getMethod.equals("code/codetypes/")?"?":"&") + "region=" + URLEncoder.encode(region, "UTF-8"); }
		if (type != null) { getMethod +=  (getMethod.equals("code/codetypes/")?"?":"&") + "type=" + URLEncoder.encode(type, "UTF-8"); }


		String response = client.doRestGET(getMethod);

		return client.getGson().<List<String>>fromJson(response, List.class);
	}


}
