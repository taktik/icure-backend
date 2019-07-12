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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.AccessLogDAO;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.AccessLog;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.AccessLogLogic;
import org.taktik.icure.logic.ICureSessionLogic;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccessLogLogicImpl extends GenericLogicImpl<AccessLog, AccessLogDAO> implements AccessLogLogic {

	private AccessLogDAO accessLogDAO;
	private ICureSessionLogic sessionLogic;

	@Override
	public AccessLog createAccessLog(AccessLog accessLog) {
		Instant now = Instant.now();
		if (accessLog.getDate() == null) {
			accessLog.setDate(now);
		}
		accessLog.setUser(sessionLogic.getCurrentUserId());
		return accessLogDAO.create(accessLog);
	}

    @Override
    public List<String> deleteAccessLogs(List<String> ids) throws DeletionException {
        try {
            deleteEntities(ids);
            return ids;
        } catch (Exception e) {
            throw new DeletionException(e.getMessage(), e);
        }
    }

    @Override
    public List<AccessLog> findByHCPartySecretPatientKeys(String hcPartyId, ArrayList<String> secretForeignKeys) {
        return accessLogDAO.findByHCPartySecretPatientKeys(hcPartyId, secretForeignKeys);
    }

    @Override
	public AccessLog getAccessLog(String accessLogId) {
		return accessLogDAO.get(accessLogId);
	}

	@Override
	public PaginatedList<AccessLog> listAccessLogs(PaginationOffset paginationOffset) {
		return accessLogDAO.list(paginationOffset);
	}

    @Override
    public PaginatedList<AccessLog> findByUserAfterDate(String userId, String accessType, Instant startDate, PaginationOffset pagination, boolean descending) {
        return accessLogDAO.findByUserAfterDate(userId,accessType,startDate,pagination,descending);
    }

	@Override
	public AccessLog modifyAccessLog(AccessLog accessLog) {
		return accessLogDAO.save(accessLog);
	}

	@Autowired
  public void setAccessLogDAO(AccessLogDAO accessLogDAO) {
		this.accessLogDAO = accessLogDAO;
	}

	@Autowired
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Override
	protected AccessLogDAO getGenericDAO() {
		return accessLogDAO;
	}
}
