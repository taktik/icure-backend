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

import java.io.Serializable;

/**
 * Created by emad7105 on 11/07/2014.
 */
public class PaginatedDocumentKeyIdPair<K> implements Serializable {

	private K startKey;
	private String startKeyDocId;

	public PaginatedDocumentKeyIdPair() {
	}

	public PaginatedDocumentKeyIdPair(K startKey, String startKeyDocId) {
		this.startKey = startKey;
		this.startKeyDocId = startKeyDocId;
	}

	public K getStartKey() {
		return startKey;
	}

	public void setStartKey(K startKey) {
		this.startKey = startKey;
	}

	public String getStartKeyDocId() {
		return startKeyDocId;
	}

	public void setStartKeyDocId(String startKeyDocId) {
		this.startKeyDocId = startKeyDocId;
	}
}
