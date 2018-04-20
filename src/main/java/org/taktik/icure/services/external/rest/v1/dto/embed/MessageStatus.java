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

package org.taktik.icure.services.external.rest.v1.dto.embed;

import java.util.HashMap;
import java.util.Map;

public enum MessageStatus {
	CREATED(0),
	SENT(1),
	PUBLISHED(2),
	RECEIVED(3),
	READ(4);

	private static final Map<Integer, MessageStatus> enumsByCode = new HashMap<>();

	private Integer code;

	MessageStatus(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}

	public static MessageStatus fromCode(Integer code) {
		return enumsByCode().get(code);
	}

	private static Map<Integer, MessageStatus> enumsByCode() {
		if (enumsByCode.isEmpty()) {
			for (MessageStatus messageStatus : MessageStatus.values()) {
				enumsByCode.put(messageStatus.getCode(), messageStatus);
			}
		}
		return enumsByCode;
	}
}
