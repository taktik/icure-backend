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

import org.taktik.icure.entities.Property;
import org.taktik.icure.entities.Role;
import org.taktik.icure.entities.base.Principal;
import org.taktik.icure.entities.embed.Permission;

import java.util.Set;

public interface PrincipalLogic<P extends Principal> {
	P getPrincipal(String principalId);

	Set<Property> getProperties(String principalId, boolean includeDirect, boolean includeHerited, boolean includeDefault);

	Set<Permission> getPermissions(String principalId, String virtualHostId, boolean includeDirect, boolean includeHerited, boolean includeDefault);

	Set<Role> getAscendantRoles(String principalId);
}