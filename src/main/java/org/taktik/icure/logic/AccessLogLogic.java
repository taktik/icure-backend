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

package org.taktik.icure.logic;

import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.AccessLog;
import org.taktik.icure.entities.HealthElement;
import org.taktik.icure.exceptions.DeletionException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public interface AccessLogLogic extends EntityPersister<AccessLog, String> {

	AccessLog createAccessLog(AccessLog accessLog);

	AccessLog getAccessLog(String accessLogId);

	PaginatedList<AccessLog> listAccessLogs(PaginationOffset paginationOffset);

    PaginatedList<AccessLog> findByUserAfterDate(String userId, String accessType, Instant startDate, PaginationOffset pagination, boolean descending);

    AccessLog modifyAccessLog(AccessLog accessLog);

    List<String> deleteAccessLogs(List<String> ids) throws DeletionException;

    List<AccessLog> findByHCPartySecretPatientKeys(String hcPartyId, ArrayList<String> secretForeignKeys);
}
