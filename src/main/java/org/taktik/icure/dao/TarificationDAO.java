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

package org.taktik.icure.dao;

import java.util.List;

import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Tarification;
import org.taktik.icure.entities.base.Code;

public interface TarificationDAO extends GenericDAO<Tarification> {

	List<Tarification> findTarifications(String type, String code, String version);
	List<Tarification> findTarifications(String region, String type, String code, String version);

	PaginatedList<Tarification> findTarifications(String region, String type, String code, String version, PaginationOffset paginationOffset);
	PaginatedList<Tarification> findTarificationsByLabel(String region, String language, String label, PaginationOffset pagination);
	PaginatedList<Tarification> findTarificationsByLabel(String region, String language, String type, String label, PaginationOffset paginationOffset);
}
