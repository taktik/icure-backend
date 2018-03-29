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

package org.taktik.icure.db;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aduchate on 3/11/13, 14:38
 */
@SuppressWarnings("UnusedDeclaration")
public class PaginationOffset<K> implements Serializable {
    K startKey;
    String startDocumentId;
    Integer offset;
    Integer limit;
	Integer page;

    public PaginationOffset() {
    }

	public PaginationOffset(int limit) {
		this.limit = limit;
	}

	public PaginationOffset(PaginatedList paginatedList) {
		this.startKey = paginatedList.getNextKeyPair().getStartKey() != null ? (K)paginatedList.getNextKeyPair().getStartKey().toArray() : (K) paginatedList.getNextKeyPair().getStartKey();
		this.startDocumentId = paginatedList.getNextKeyPair().getStartKeyDocId();
		this.limit = paginatedList.getPageSize();
	}

    public PaginationOffset(K startKey, String startDocumentId, Integer offset, Integer limit) {
        this.startKey = startKey;
        this.startDocumentId = startDocumentId;
        this.offset = offset;
        this.limit = limit;
    }

    public K getStartKey() {
        return startKey;
    }

    public void setStartKey(K startKey) {
        this.startKey = startKey;
    }

    public String getStartDocumentId() {
        return startDocumentId;
    }

    public void setStartDocumentId(String startDocumentId) {
        this.startDocumentId = startDocumentId;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}
}
