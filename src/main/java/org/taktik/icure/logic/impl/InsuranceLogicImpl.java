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

package org.taktik.icure.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.InsuranceDAO;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Insurance;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.InsuranceLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class InsuranceLogicImpl extends GenericLogicImpl<Insurance, InsuranceDAO> implements InsuranceLogic {

	private InsuranceDAO insuranceDAO;

	@Override
	public Insurance createInsurance(Insurance insurance) {
		return insuranceDAO.create(insurance);
	}

	@Override
	public String deleteInsurance(String insuranceId) throws DeletionException {
		try {
			deleteEntities(Arrays.asList(insuranceId));
			return insuranceId;
		} catch (Exception e) {
			throw new DeletionException(e.getMessage(), e);
		}
	}

	@Override
	public Insurance getInsurance(String insuranceId) {
		return insuranceDAO.get(insuranceId);
	}

	@Override
	public List<Insurance> listInsurancesByCode(String code) {
		return insuranceDAO.listByCode(code);
	}

	@Override
	public List<Insurance> listInsurancesByName(String name) {
		return insuranceDAO.listByName(name);
	}

	@Override
	public Insurance modifyInsurance(Insurance insurance) {
		return insuranceDAO.save(insurance);
	}

	@Override
	public List<Insurance> getInsurances(Set<String> ids) {
		return insuranceDAO.getList(ids);
	}

	@Autowired
	public void setInsuranceDAO(InsuranceDAO insuranceDAO) {
		this.insuranceDAO = insuranceDAO;
	}

	@Override
	protected InsuranceDAO getGenericDAO() {
		return insuranceDAO;
	}
}
