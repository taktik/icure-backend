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


import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.TarificationDAO;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Tarification;
import org.taktik.icure.logic.TarificationLogic;

@Service
public class TarificationLogicImpl extends GenericLogicImpl<Tarification, TarificationDAO> implements TarificationLogic {
	private static final Logger logger = LoggerFactory.getLogger(TarificationLogicImpl.class);

	private TarificationDAO tarificationDAO;

	@Override
	public Tarification get(String id) {
		return tarificationDAO.get(id);
	}

	@Override
	public Tarification get(@NotNull String type, @NotNull String tarification, @NotNull String version) {
		return tarificationDAO.get(type + "|" + tarification + "|" + version);
	}

	@Override
	public List<Tarification> get(List<String> ids) {
		return tarificationDAO.getList(ids);
	}

	@Override
	public Tarification create(Tarification tarification) {
		Preconditions.checkNotNull(tarification.getCode(), "Tarification field is null.");
		Preconditions.checkNotNull(tarification.getType(), "Type field is null.");
		Preconditions.checkNotNull(tarification.getVersion(), "Version tarification field is null.");

		// assigning Tarification id type|tarification|version
		tarification.setId(tarification.getType() + "|" + tarification.getCode() + "|" + tarification.getVersion());

		return tarificationDAO.create(tarification);
	}

	@Override
	public Tarification modify(Tarification tarification) throws Exception {
		Tarification existingTarification = tarificationDAO.get(tarification.getId());

		Preconditions.checkState(existingTarification.getCode().equals(tarification.getCode()), "Modification failed. Tarification field is immutable.");
		Preconditions.checkState(existingTarification.getType().equals(tarification.getType()), "Modification failed. Type field is immutable.");
		Preconditions.checkState(existingTarification.getVersion().equals(tarification.getVersion()), "Modification failed. Version field is immutable.");

		updateEntities(Collections.singleton(tarification));

		return this.get(tarification.getId());
	}


	@Override
	public List<Tarification> findTarificationsBy(String type, String tarification, String version) {
		return tarificationDAO.findTarifications(type, tarification, version);
	}

	@Override
	public List<Tarification> findTarificationsBy(String region, String type, String tarification, String version) {
		return tarificationDAO.findTarifications(region, type, tarification, version);
	}

	@Override
	public PaginatedList<Tarification> findTarificationsBy(String region, String type, String tarification, String version, PaginationOffset paginationOffset) {
		return tarificationDAO.findTarifications(region, type, tarification, version, paginationOffset);
	}

	@Override
	public PaginatedList<Tarification> findTarificationsByLabel(String region, String language, String label, PaginationOffset paginationOffset) {
		return tarificationDAO.findTarificationsByLabel(region, language, label, paginationOffset);
	}

	@Override
	public PaginatedList<Tarification> findTarificationsByLabel(String region, String language, String type, String label, PaginationOffset paginationOffset) {
		return tarificationDAO.findTarificationsByLabel(region, language, type, label, paginationOffset);
	}

	@Override
	public Tarification getOrCreateTarification(String type, String tarification) {
		List<Tarification> tarifications = findTarificationsBy(type, tarification, null);

		if (tarifications.size()>0) {
			return tarifications.stream().sorted((a,b)->b.getVersion().compareTo(a.getVersion())).findFirst().get();
		}

		return this.create(new Tarification(type,tarification,"1.0"));
	}

	@Override
	protected TarificationDAO getGenericDAO() {
		return tarificationDAO;
	}

	@Autowired
	public void setTarificationDAO(TarificationDAO tarificationDAO) {
		this.tarificationDAO = tarificationDAO;
	}
}
