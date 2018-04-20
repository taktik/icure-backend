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

import java.io.Serializable;
import java.util.List;

/**
 * Created by emad7105 on 11/07/2014.
 */
public class PaginatedList<T extends Serializable> implements Serializable {
	private int totalSize;
	private int pageSize;
	private PaginatedDocumentKeyIdPair nextKeyPair;

	private List<T> rows;

	public PaginatedList(int pageSize, int totalSize, List<T> rows, PaginatedDocumentKeyIdPair nextKeyPair) {

		this.pageSize = pageSize;
		this.totalSize = totalSize;
		this.rows = rows;
		this.nextKeyPair = nextKeyPair;
	}

	public PaginatedList() {}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public PaginatedDocumentKeyIdPair getNextKeyPair() {
		return nextKeyPair;
	}

	public void setNextKeyPair(PaginatedDocumentKeyIdPair nextKeyPair) {
		this.nextKeyPair = nextKeyPair;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}
}
