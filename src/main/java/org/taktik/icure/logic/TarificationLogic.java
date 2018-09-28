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

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Tarification;

public interface TarificationLogic {
	Tarification get(String id);

	Tarification get(@NotNull String type, @NotNull String tarification, @NotNull String version);

	List<Tarification> get(List<String> ids);

	Tarification create(Tarification tarification);

	Tarification modify(Tarification tarification) throws Exception;

	List<Tarification> findTarificationsBy(String type, String tarification, String version);

	List<Tarification> findTarificationsBy(String region, String type, String tarification, String version);

	PaginatedList<Tarification> findTarificationsBy(String region, String type, String tarification, String version, PaginationOffset paginationOffset);

	PaginatedList<Tarification> findTarificationsByLabel(String region, String language, String label, PaginationOffset paginationOffset);

	PaginatedList<Tarification> findTarificationsByLabel(String region, String language, String type, String label, PaginationOffset paginationOffset);

	Tarification getOrCreateTarification(String type, String tarification);
}
