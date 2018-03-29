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

package org.taktik.icure.logic;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.taktik.icure.entities.Group;
import org.taktik.icure.entities.Replication;
import org.taktik.icure.entities.embed.DatabaseSynchronization;

import javax.ws.rs.core.Response;

public interface ReplicationLogic extends EntityPersister<Replication, String> {

	Map<DatabaseSynchronization, Number> getPendingChanges();

	void startReplications();

	@Nullable
	Replication createGroupReplication(String protocol, String replicationHost, String port, String groupId, String password) throws Exception;

	Replication createReplication(Replication userHcpReplication);

	Replication createBaseTemplateReplication(String protocol, String replicationHost, String port, String language, String specialtyCode) throws Exception;

	void createUserGroupReplications(List<Group> allGroups);
}
