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

package org.taktik.icure.db;

import java.util.List;

public class PaginatedList<T> {
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
